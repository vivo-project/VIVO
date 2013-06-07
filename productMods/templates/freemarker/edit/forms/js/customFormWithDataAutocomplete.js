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
        var disableWrapper = $('#ie67DisableWrapper');
        
        // Check for unsupported browsers only if the element exists on the page
        if (disableWrapper.length) {
            if (vitro.browserUtils.isIELessThan8()) {
                disableWrapper.show();
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
        $.extend(this, i18nStrings);
    },
    
    // On page load, create references for easy access to form elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function(){

        this.form = $('form.customForm');
        this.fullViewOnly = $('.fullViewOnly');
        this.button = $('#submit');
        this.requiredLegend = $('#requiredLegend');
       

        // These are classed rather than id'd in case we want more than one autocomplete on a form.
        // At that point we'll use ids to match them up with one another.
        this.acSelector = this.form.find('.acSelector');
        this.acSelection = this.form.find('.acSelection');
        this.acSelectionInfo = this.form.find('.acSelectionInfo');
        
        this.acSelectorWrapper = this.acSelector.parent();
        
        this.or = $('span.or');       
        this.cancel = this.form.find('.cancel');
        this.acHelpTextClass = 'acSelectorWithHelpText';
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
        
        this.initElementData();
        
        this.initFormView();

    },

    initFormView: function() {
        
        // Put this case first, because in edit mode with
        // validation errors we just want initFormFullView.
        if (this.editMode == 'repair') {
            this.initFormFullView();
        } else if(this.editMode == 'edit') {
        	this.initFormEditFullView();
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

        this.hideFields(this.fullViewOnly);
        this.button.hide();
        this.requiredLegend.hide();
        this.or.hide();

        this.cancel.unbind('click');
    },
    
    
    initFormFullView: function() {

        this.fullViewOnly.show();
        this.or.show();
        this.requiredLegend.show();
        this.button.show();
//        this.setButtonText('new');
        this.setLabels(); 

        // Set the initial autocomplete help text in the acSelector field.
        this.addAcHelpText();

        this.cancel.unbind('click');           
        if (this.formSteps > 1) {     
            this.cancel.click(function() {
                customForm.clearFormData(); // clear any input and validation errors
                customForm.initFormTypeView();
                return false;            
            });
        // In one-step forms, if there is a type selection field, but no value is selected,
        // hide the acSelector field. The type selection must be made first so that the
        // autocomplete type can be determined. If a type selection has been made, 
        // unhide the acSelector field.
        } 

    },
    
    initFormWithValidationErrors: function() {
        var  label = this.acSelector.val(); 
        
        // Call initFormFullView first, because showAutocompleteSelection needs
        // acType, which is set in initFormFullView. 
        this.initFormFullView();
        
        //See if value exists, either b/c editing or label is in input if validation error
        if(label.length > 0) {
        	this.showAutocompleteSelection(label);
        }
        
       
    },
    
    initFormEditFullView: function() {
    	  var  label = this.acSelector.val(); 
          
          // Call initFormFullView first, because showAutocompleteSelection needs
          // acType, which is set in initFormFullView. 
          this.initFormFullView();
          
          //See if value exists, either b/c editing or label is in input if validation error
          if(this.editMode == 'edit' || label.length > 0) {
          	this.showAutocompleteSelection(label);
          }
    },
    
    
    // Bind event listeners that persist over the life of the page. Event listeners
    // that depend on the view should be initialized in the view setup method.
    bindEventListeners: function() {

       //no longer need type selector and verify match
        
        this.acSelector.focus(function() {
            customForm.deleteAcHelpText();
        });   
        
        this.acSelector.blur(function() {
            customForm.addAcHelpText();
        }); 
        
        this.form.submit(function() {
            customForm.deleteAcHelpText();
        });
        
    },
    
    initAutocomplete: function() {	
        
        this.getAcFilter();
        this.acCache = {};
        
        this.acSelector.autocomplete({
            minLength: 3,
            source: customForm.doAutoComplete,
            select: function(event, ui) {
              	customForm.showAutocompleteSelection(ui.item.value);                     
            }
        });
    },
   
    //For debugging, trying to extract auto complete method
    doAutoComplete: function(request, response) {
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
                property: customForm.property
            },
            complete: function(xhr, status) {
                // Not sure why, but we need an explicit json parse here. 
                var results = $.parseJSON(xhr.responseText), 
                filteredResults = customForm.filterAcResults(results);
                customForm.acCache[request.term] = filteredResults;
                response(filteredResults);
            }
        });
    }, 
    
    
    // Store original or base text with elements that will have text substitutions.
    // Generally the substitution cannot be made on the current value, since that value
    // may have changed from the original. So we store the original text with the element to
    // use as a base for substitutions.
    initElementData: function() {
        
        this.placeholderText = '###';
        this.labelsWithPlaceholders = this.form.find('label, .label').filter(function() {
            return $(this).html().match(customForm.placeholderText);
        });        
        this.labelsWithPlaceholders.each(function(){
            $(this).data('baseText', $(this).html());
        });
        
        this.button.data('baseText', this.button.val());
   
        
    },
    //get autocomplete filter with sparql query
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
            dataType: "json",
            data: {
                query: customForm.sparqlForAcFilter
            },
            success: function(data, status, xhr) {
                customForm.setAcFilter(data);
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
        	//Here this should refer to the results array value being iterated through
            if ($.inArray(String(this), customForm.acFilter) == -1) {
                filteredResults.push(String(this));
            }
            else {
           
            }
        });
        return filteredResults;
    },
    
    // Reset some autocomplete values after type is changed
    resetAutocomplete: function(typeVal) {
        // Append the type parameter to the base autocomplete url
        var glue = this.baseAcUrl.indexOf('?') > -1 ? '&' : '?';
        this.acUrl = this.baseAcUrl + glue;
        
        // Flush autocomplete cache when type is reset, since the cached values 
        // pertain only to the previous type.
        this.acCache = {};
    },       
    //in our case, we have only the literal value itself    
    showAutocompleteSelection: function(label) {

        this.hideFields(this.acSelectorWrapper);              
        this.acSelection.show();

        this.acSelector.val(label);        
        this.acSelectionInfo.html(label);
     
//        this.setButtonText('existing');            
        
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
 
        // The test is not just for efficiency: undoAutocompleteSelection empties the acSelector value,
        // which we don't want to do if user has manually entered a value, since he may intend to
        // change the type but keep the value. If no new value has been selected, form initialization
        // below will correctly empty the value anyway.
        if (!this.acSelection.is(':hidden')) {       
            this.acSelectorWrapper.show();
            this.hideFields(this.acSelection);
            this.acSelector.val('');
            this.acSelectionInfo.html('');
            
            if (this.formSteps > 1) {
                htmlString = customForm.selectedString + " ";
                this.acSelection.find('label').html(htmlString);
            }
        }      
    },
    
    // Set field labels based on type selection. Although these won't change in edit
    // mode, it's easier to specify the text here than in the jsp.
    setLabels: function() {
        var typeName = this.defaultTypeName;

        this.labelsWithPlaceholders.each(function() {
            var newLabel = $(this).data('baseText').replace(customForm.placeholderText, typeName);
            $(this).html(newLabel);
        });

    },
    
    // Set button text based on both type selection and whether it's an autocomplete selection
    // or a new related individual. Called when setting up full view of form, and after
    // an autocomplete selection.
    //Commenting out for now because keeping in synch with regular data property form and
    //not changing the button text at all
    setButtonText: function(newOrExisting) {
    	
    	/*
        var typeText, 
            buttonText,
            baseButtonText = this.button.data('baseText');
        
        // Edit mode button doesn't change, so it's specified in the jsp
        if (this.editMode === 'edit') {
            return;
        }  

        typeText = this.defaultTypeName;
                
        // Creating new related individual      
        if (newOrExisting === 'new') {
            if (this.submitButtonTextType == 'compound') { // use == to tolerate nulls
                // e.g., 'Create Grant & Principal Investigator'
                buttonText = 'Create ' + typeText + ' & ' + baseButtonText;          
            } else {
                // In repair mode, baseButtonText is "Edit X". Keep that for this case.
                // In add mode, baseButtonText is "X", so we get, e.g., "Create Publication"
                buttonText = this.editMode == 'repair' ? baseButtonText : 'Create ' + baseButtonText;
            }            
        }
        // Using existing related individual
        else {  
            // In repair mode, baseButtonText is "Edit X". Keep that for this case.
            buttonText = this.editMode == 'repair' ? baseButtonText : 'Add ' + baseButtonText;
        } 
        
        this.button.val(buttonText);
        */
    },
    

    // Set the initial help text that appears in the autocomplete field and change the class name
    addAcHelpText: function() {
        var typeText = this.defaultTypeName;
    
        // First case applies on page load; second case applies when the type gets changed.
        if (!this.acSelector.val() || this.acSelector.hasClass(this.acHelpTextClass)) {            
        	var helpText = customForm.selectExisting + " " + typeText + " " + customForm.orCreateNewOne;
        	//Different for object property autocomplete
			this.acSelector.val(helpText)
		               	   .addClass(this.acHelpTextClass);     
		}
	},

    deleteAcHelpText: function() {
        if (this.acSelector.hasClass(this.acHelpTextClass)) {
			this.acSelector.val('')
		                   .removeClass(this.acHelpTextClass);
		}
	}
	
};

$(document).ready(function() {   
    customForm.onLoad();
});
