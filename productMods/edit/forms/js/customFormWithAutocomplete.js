/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var customForm = {
    
    /* *** Initial page setup *** */
   
    onLoad: function() {
        
        if (this.disableFormInUnsupportedBrowsers()) {
            return;
        }
        this.mixIn();
        this.initObjects();                 
        this.initPage();       
    },
    
    disableFormInUnsupportedBrowsers: function() {       
        this.disableWrapper = $('#ie67DisableWrapper');
        
        // Check for unsupported browsers only if the element exists on the page
        if (this.disableWrapper.length) {
            if (vitro.browserUtils.isIELessThan8()) {
                this.disableWrapper.show();
                $('.noIE67').hide();
                return true;
            }
        }            
        return false;      
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
        //this.acLabelReceiver = this.form.find('.acLabelReceiver');
        this.verifyMatch = this.form.find('.verifyMatch');    
        this.verifyMatchBaseHref = this.verifyMatch.attr('href');    
        this.acSelectorWrapper = this.acSelector.parent();
        
        this.or = $('span.or');       
        this.cancel = this.form.find('.cancel');
    },

    // Set up the form on page load
    initPage: function() {

        if (!this.editMode) {
            this.editMode = 'add'; // edit vs add: default to add
        }
               
        if (!this.formSteps) { // Don't override formSteps specified in form data
            if ( !this.fullViewOnly.length || this.editMode === 'edit' || this.editMode === 'repair' ) {
                this.formSteps = 1;
            // there may also be a 3-step form - look for this.subTypeSelector
            }
            else {
                this.formSteps = 2;
            }
        }
                
        this.bindEventListeners();
        
        this.initAutocomplete();
        
        this.initPlaceholderData();
        
        this.initFormView();

    },

    initFormView: function() {
      
        var typeVal = this.typeSelector.val();  
        
        // Put this case first, because in edit mode with
        // validation errors we just want initFormFullView.
        if (this.editMode == 'edit' || this.editMode == 'repair') {
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

        this.setType();        
        this.fullViewOnly.show();
        this.or.show();
        this.requiredLegend.show();
        this.button.show();
        this.setButtonText('new');
        this.setLabels(); 
           
        if (this.formSteps > 1) {  // includes this.editMode == 1
            this.cancel.unbind('click');   
            this.cancel.click(function() {
                customForm.clearFormData(); // clear any input and validation errors
                customForm.initFormTypeView();
                return false;            
            });
        // In one-step forms, if there is a type selection field, but no value is selected,
        // hide the acSelector field. The type selection must be made first so that the
        // autocomplete type can be determined. If a type selection has been made, 
        // unhide the acSelector field.
        } else if (this.typeSelector.length) {
            if (this.typeSelector.val()) {
                this.acSelectorWrapper.show()
            }
            else {
                this.acSelectorWrapper.hide();
            }
        }
    },
    
    initFormWithValidationErrors: function() {
        var uri = this.acUriReceiver.val(), 
            label = this.acSelector.val(); 
        
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
    
    // Bind event listeners that persist over the life of the page. Event listeners
    // that depend on the view should be initialized in the view setup method.
    bindEventListeners: function() {

        this.typeSelector.change(function() {
            var typeVal = $(this).val();
            
            // If an autocomplete selection has been made, undo it
            customForm.undoAutocompleteSelection();

            // If no selection, go back to type view. This prevents problems like trying to run autocomplete
            // or submitting form without a type selection. Exceptions: (1) a one-step form; (2) a two-step
            // form in repair mode, so we don't lose the other data in the form.      
            (typeVal.length || customForm.formSteps === 1) ? customForm.initFormFullView() : customForm.initFormTypeView();
    
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
    
    initPlaceholderData: function() {
        this.placeholderText = '###';
        this.labelsWithPlaceholders = this.form.find('label, .label').filter(function() {
            return $(this).html().match(customForm.placeholderText);
        });
        
        this.labelsWithPlaceholders.each(function(){
            $(this).data('originalLabel', $(this).html());
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
        //this.acSelector.attr('disabled', 'disabled');
        
        // If form has a type selector, add type name to label. If form has no type selector,
        // type name is coded into the html.
        if (this.typeSelector.length) {
            this.acSelection.find('label').html('Selected ' + this.typeName + ':');
        }
              
        this.acSelection.show();

        this.acUriReceiver.val(uri);
        this.acSelector.val(label);
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
        this.hideFields(this.acSelection);
        this.acSelector.val('');
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
        var typeName = this.getTypeNameForLabels();

        this.labelsWithPlaceholders.each(function() {
            var newLabel = $(this).data('originalLabel').replace(customForm.placeholderText, typeName);
            $(this).html(newLabel);
        });

    },
    
    // Set button text based on both type selection and whether it's an autocomplete selection
    // or a new related individual. Called when setting up full view of form, and after
    // an autocomplete selection.
    setButtonText: function(newOrExisting) {
        var typeText, buttonText;
        
        // Edit mode button doesn't change, so it's specified in the jsp
        if (this.editMode === 'edit') {
            return;
        }  

        typeText = this.getTypeNameForLabels();
                
        // Creating new related individual      
        if (newOrExisting === 'new') {
            if (this.submitButtonTextType == 'compound') { // use == to tolerate nulls
                // e.g., 'Create Grant & Principal Investigator'
                buttonText = 'Create ' + typeText + ' & ' + this.baseButtonText;                
            } else {
                // e.g., 'Create Publication'
                buttonText = 'Create ' + this.baseButtonText;
            }            
        }
        // Using existing related individual
        else {  
            // In repair mode, baseButtonText is "Edit X". Keep that for this case.
            buttonText = this.editMode == 'repair' ? this.baseButtonText : 'Add ' + this.baseButtonText;
        } 
        
        this.button.val(buttonText);
    },
    
    getTypeNameForLabels: function() {
        // If this.acType is empty, we are either in a one-step form with no type yet selected,
        // or in repair mode in a two-step form with no type selected. Use the default type
        // name specified in the form data (this.typeName is 'Select one').
        return this.acType ? this.typeName : this.capitalize(this.defaultTypeName);
    }
    
};

$(document).ready(function() {   
    customForm.onLoad();
});
