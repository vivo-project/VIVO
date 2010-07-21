/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var customForm = {
    
    /* *** Initial page setup *** */
   
    onLoad: function() {
        this.mixIn();
        this.initObjects();                 
        this.initPage();       
    },

    mixIn: function() {
        // Mix in the custom form utility methods
        $.extend(this, vitro.customFormUtils);

        // Get the custom form data from the page
        $.extend(this, customFormData);
    },
    
    // On page load, create references for easy access to form elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function(){

        this.form = $('#content form');
        this.fullViewOnly = $('.fullViewOnly');
        this.button = $('#submit');
        this.baseButtonText = this.button.val();
        this.requiredLegend = $('#requiredLegend');
        this.typeSelector = this.form.find('#typeSelector');

        // These are classed rather than id'd in case we want more than one autocomplete on a form.
        // At that point we'll use ids to match them up with one another.
        this.acSelector = this.form.find('.acSelector');
        this.acSelection = this.form.find('.acSelection');
        this.acSelectionInfo = this.form.find('.acSelectionInfo');
        this.acUriReceiver = this.form.find('.acUriReceiver');
        this.acLabelReceiver = this.form.find('.acLabelReceiver');
        this.verifyMatch = this.form.find('.verifyMatch');    
        this.verifyMatchBaseHref = this.verifyMatch.attr('href');    
        this.acSelectorWrapper = this.acSelector.parent();
        
        this.relatedIndLabel = $('#relatedIndLabel');
        this.labelFieldLabel = $('label[for=' + this.relatedIndLabel.attr('id') + ']');       
        // Get this on page load, so we can prepend to it. We can't just prepend to the current label text,
        // because it may have already been modified for a previous selection.
        this.baseLabelText = this.labelFieldLabel.html();

        // Label field for new individual being created
        this.newIndLabel = $('#newIndLabel');
        this.newIndLabelFieldLabel = $('label[for=' + this.newIndLabel.attr('id') + ']');
        this.newIndBaseLabelText = this.newIndLabelFieldLabel.html();
        
        this.dateHeader = $('#dateHeader');
        this.baseDateHeaderText = this.dateHeader.html();
        
        this.or = $('span.or');       
        this.cancel = this.form.find('.cancel');
        
        this.placeHolderText = '###';

    },

    // Set up the form on page load
    initPage: function() {

        if (!this.editMode) {
            this.editMode = 'add'; // edit vs add: default to add
        }
        
        if (!this.typeSelector.length || this.editMode == 'edit') {
            this.formSteps = 1;
        // there may also be a 3-step form - look for this.subTypeSelector
        } else {
            this.formSteps = 2;
        }
                
        this.bindEventListeners();
        
        this.initAutocomplete();
        
        this.initFormView();

    },

    initFormView: function() {
      
        var typeVal = this.typeSelector.val();  
        
        // Put this case first, because in edit mode with
        // validation errors we just want initFormFullView.
        if (this.editMode == 'edit') {
            this.initFormFullView();
        }
        else if (this.findValidationErrors()) {
            this.initFormWithValidationErrors();
        }
        // If type is already selected when the page loads (Firefox retains value
        // on a refresh), go directly to full view. Otherwise user has to reselect
        // twice to get to full view.        
        else if ( this.formSteps == 1 || typeVal.length ) {
            this.initFormFullView();
        }
        else {
            this.initFormTypeView();
        }     
    },
    
    initFormTypeView: function() {

        this.setType(); // empty any previous values (perhaps not needed)
        this.hideFields(this.fullViewOnly);
        this.button.hide();
        this.requiredLegend.hide();
        this.or.hide();

        this.cancel.unbind('click');
    },
    
    initFormFullView: function() {
        
        if (this.editMode == 'edit') {
            this.initFormEditView();
        }

        this.setType();        
        this.fullViewOnly.show();
        this.or.show();
        this.requiredLegend.show();
        this.button.show();
        this.setButtonText('new');
        this.setLabels(); 
           
        if( this.formSteps > 1 ){  // NB includes this.editMode == 1
            this.cancel.unbind('click');   
            this.cancel.click(function() {
                customForm.clearFormData(); // clear any input and validation errors
                customForm.initFormTypeView();
                return false;            
            });
        }
    },
    
    initFormWithValidationErrors: function() {
        var uri = this.acUriReceiver.val(), 
            label = this.acLabelReceiver.val();
        
        // Call initFormFullView first, because showAutocompleteSelection needs
        // acType, which is set in initFormFullView. 
        this.initFormFullView();
        
        if (uri) {            
            this.showAutocompleteSelection(label, uri);
        }
        
        this.cancel.unbind('click');
        this.cancel.click(function() {
           // Cancel back to full view with only type selection showing
           customForm.undoAutocompleteSelection();
           customForm.clearFields(customForm.fullViewOnly);
           customForm.initFormFullView(); 
           return false;
        });
       
    },
    
    initFormEditView: function() {
        // These are not editable: only properties of the role are editable.
        this.typeSelector.attr('disabled', 'disabled');
        this.relatedIndLabel.attr('disabled', 'disabled');
        
        this.form.submit(function() {
            // Re-enable these fields so they get submitted, since they are required
            // in the edit config.
            customForm.typeSelector.attr('disabled', '');
            customForm.relatedIndLabel.attr('disabled', '');
        });
    },
    
    // Bind event listeners that persist over the life of the page. Event listeners
    // that depend on the view should be initialized in the view setup method.
    bindEventListeners: function() {

        this.typeSelector.change(function() {
            var typeVal = $(this).val();
            
            // If an autocomplete selection has been made, undo it
            customForm.undoAutocompleteSelection();

            // If no selection, go back to type view. This prevents problems like trying to run autocomplete
            // or submitting form without a type selection.            
            typeVal.length ? customForm.initFormFullView() : customForm.initFormTypeView();
    
        }); 
        
        this.verifyMatch.click(function() {
            window.open($(this).attr('href'), 'verifyMatchWindow', 'width=640,height=640,scrollbars=yes,resizable=yes,status=yes,toolbar=no,menubar=no,location=no');
            return false;
        });      
    },
    
    initAutocomplete: function() {

        if (this.editMode === 'edit') {
            return;
        }
        
        this.getAcFilter();
        this.acCache = {};
        
        this.acSelector.autocomplete({
            minLength: 3,
            source: function(request, response) {
                if (request.term in customForm.acCache) {
                    // console.log('found term in cache');
                    response(customForm.acCache[request.term]);
                    return;
                }
                // console.log('not getting term from cache');

                $.ajax({
                    url: customForm.acUrl,
                    dataType: 'json',
                    data: {
                        term: request.term,
                        type: customForm.acType
                    },
                    complete: function(xhr, status) {
                        // Not sure why, but we need an explicit json parse here. jQuery
                        // should parse the response text and return a json object.
                        var results = $.parseJSON(xhr.responseText), 
                            filteredResults = customForm.filterAcResults(results);
                        customForm.acCache[request.term] = filteredResults;
                        response(filteredResults);
                    }
                });
            },
            select: function(event, ui) {
                customForm.showAutocompleteSelection(ui.item.label, ui.item.uri);                     
            }
        });
    },
    
    getAcFilter: function() {

        if (!this.sparqlForAcFilter) {
            //console.log('autocomplete filtering turned off');
            this.acFilter = null;
            return;
        }
        
        //console.log("sparql for autocomplete filter: " + this.sparqlForAcFilter);

        // Define this.acFilter here, so in case the sparql query fails
        // we don't get an error when referencing it later.
        this.acFilter = [];
        $.ajax({
            url: customForm.sparqlQueryUrl,
            data: {
                resultFormat: 'RS_JSON',
                query: customForm.sparqlForAcFilter
            },
            success: function(data, status, xhr) {
                // Not sure why, but we need an explicit json parse here. jQuery
                // should parse the response text and return a json object.
                customForm.setAcFilter($.parseJSON(data));
            }
        });
    },
    
    setAcFilter: function(data) {

        var key = data.head.vars[0];
        
        $.each(data.results.bindings, function() {
            customForm.acFilter.push(this[key].value);
        });         
    },
    
    filterAcResults: function(results) {
        var filteredResults;
        
        if (!this.acFilter || !this.acFilter.length) {
            //console.log('no autocomplete filtering applied');
            return results;
        }
        
        filteredResults = [];
        $.each(results, function() {
            if ($.inArray(this.uri, customForm.acFilter) == -1) {
                //console.log('adding ' + this.label + ' to filtered results');
                filteredResults.push(this);
            }
            else {
                //console.log('filtering out ' + this.label);
            }
        });
        return filteredResults;
    },

    // Reset some autocomplete values after type is changed
    resetAutocomplete: function(typeVal) {
        // Append the type parameter to the base autocomplete url
        var glue = this.baseAcUrl.indexOf('?') > -1 ? '&' : '?';
        this.acUrl = this.baseAcUrl + glue + 'type=' + typeVal;
        
        // Flush autocomplete cache when type is reset, since the cached values 
        // pertain only to the previous type.
        this.acCache = {};
    },       
        
    showAutocompleteSelection: function(label, uri) {

        this.acSelectorWrapper.hide();
        this.acSelector.attr('disabled', 'disabled');
        
        // If only one form step, type is pre-selected, and the label is coded in the html.
        if (this.formSteps > 1) {
            this.acSelection.find('label').html('Selected ' + this.typeName + ':');
        }
              
        this.acSelection.show();

        this.acUriReceiver.val(uri);
        this.acLabelReceiver.val(label);
        this.acSelectionInfo.html(label);
        this.verifyMatch.attr('href', this.verifyMatchBaseHref + uri);
        
        this.setButtonText('existing');            

        this.cancel.unbind('click');
        this.cancel.click(function() {
            customForm.undoAutocompleteSelection();
            customForm.initFormFullView();
            return false;
        });
    },
    
    // Cancel action after making an autocomplete selection: undo autocomplete 
    // selection (from showAutocomplete) before returning to full view.
    undoAutocompleteSelection: function() {
        
        this.acSelectorWrapper.show();
        this.acSelector.attr('disabled', '');
        this.acSelector.val('');
        this.hideFields(this.acSelection);
        this.acUriReceiver.val('');
        this.acSelectionInfo.html('');
        this.verifyMatch.attr('href', this.verifyMatchBaseHref);
        
        if (this.formSteps > 1) {
            this.acSelection.find('label').html('Selected ');
        }
                
    },
    
    // Set type uri for autocomplete, and type name for labels and button text.
    // Note: we still need this in edit mode, to set the text values.
    setType: function() {
        
        var selectedType;
        
        // If there's no type selector, these values have been specified in customFormData,
        // and will not change over the life of the form.
        if (!this.typeSelector.length) {
            return;
        }

        selectedType = this.typeSelector.find(':selected'); 
        if (selectedType.length) {
            this.acType = selectedType.val();
            this.typeName = selectedType.html();
        } 
        // reset to empty values; may not need
        else {
            this.acType = '';
            this.typeName = '';
        }
    },

    // Set field labels based on type selection. Although these won't change in edit
    // mode, it's easier to specify the text here than in the jsp.
    setLabels: function() {
        var newLabelTextForNewInd;
            
        this.labelFieldLabel.html(this.typeName + ' ' + this.baseLabelText);
        
        if (this.dateHeader.length) {
            this.dateHeader.html(this.baseDateHeaderText + this.typeName);
        } 
                   
        if (this.newIndLabel.length) {
            newLabelTextForNewInd = this.newIndBaseLabelText.replace(this.placeHolderText, this.typeName);
            this.newIndLabelFieldLabel.html(newLabelTextForNewInd);
        }  

    },
    
    // Set button text based on both type selection and whether it's an autocomplete selection
    // or a new related individual. Called when setting up full view of form, and after
    // an autocomplete selection.
    setButtonText: function(newOrExisting) {
        
        // Edit mode button doesn't change, so it's specified in the jsp
        if (this.editMode === 'edit') {
            return;
        }  
        
        // Creating new related individual      
        if (newOrExisting === 'new') {
            if (this.submitButtonTextType == 'compound') { // use == to tolerate nulls
                // e.g., 'Create Grant & Principal Investigator'
                this.button.val('Create ' + this.typeName + ' & ' + this.baseButtonText);                
            } else {
                // e.g., 'Create Publication'
                this.button.val('Create ' + this.baseButtonText);
            }            
        }
        // Using existing related individual
        else {  
            this.button.val('Add ' + this.baseButtonText);
        } 
    }
    
};

$(document).ready(function() {   
    customForm.onLoad();
});
