/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/* RY In a later iteration of the custom form Javascript, we'll create a customForm object
 * with subclasses for one-step and two-step forms. The parent object will contain
 * the utilities used by all form types. 
 */

/* Two-step custom form workflow:
 * 
 * ADD new property form
 * Step 1: Initial step, with choice to select existing or add new secondary individual.
 *         Displays:
 *             - On page load, unless there are validation error messages in the form
 *                     - if there are validation error messages in the form, we are returning from a failed
 *                    submission, and will go directly to view 2 to display the error messages.
 *             - After cancelling out of step 2      
 *             
 * Step 2: Main data entry step
 *         Displays: 
 *             - On page load after an attempted submission that fails validation
 *             - After clicking button or add new link in view 1
 *         Has three view variations:
 *             - Select an existing secondary individual view
 *             - Add new secondary individual view
 *             - Combined view, if we are returning from a failed validation and can't determine 
 *                which variant of view 2 we had submitted the form from. Contains the select
 *                existing element plus the add new link.
 *              
 * EDIT existing property form
 * View variations are like one-step form, though button text is specific to edit version.
 * 
 * 
 * We're jerry-rigging one-step forms into the two-step form process due to time constraints.
 * In a later iteration of the custom form Javascript, we'll create a customForm object
 * with subclasses for one-step and two-step forms. The parent object will contain
 * the utilities used by all form types. The two-step edit form will be a type of one-step form.
 * 
 * One-step custom form workflow:
 *             
 * Has two view variations:
 *      - Combined view, as above for two step form. This is the default view for
 *        the one-step form, which shows unless we have clicked the add new link
 *        or are returning from a validation error where we had been in the add new view.
 *      - Add new secondary individual view. As above for two step form, but add new
 *        box contains a close link to return to combined view.
 */
 
var customForm = {

	views: {
	    ADD_STEP_ONE: 1, // not 0, else can't test if (!view)
		ADD_NEW: 2, 
		SELECT_EXISTING: 3,
		COMBINED: 4
	},


    onLoad: function() {

		this.initObjects();		
        this.adjustForJs();             
        this.initForm();      
    },
    
    // On page load, create references within the customForm scope to DOM elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function() {

        this.form = $('#content form');
        this.button = $('#submit');
        this.or = $('span.or');
        this.requiredLegend = $('#requiredLegend');
        
        // These may need to be changed to classes rather than ids, if there are
        // multiple sets of divs to show/hide during the workflow.
        this.addNewLink = $('.addNewLink');
        this.existing = $('.existing');
        this.existingSelect = this.existing.children('select');
        this.addNew = $('.new');
        this.entry = $('.entry');
        this.existingOrNew = $('.existingOrNew'); // just the word "or" between existing and add new
        this.viewField = $("input[name='view']");

        this.cancel = this.form.find('.cancel');   
        
        this.close = this.form.find('.close');
        
        // Read values used to control display
        this.editType = $("input[name='editType']").val();
        this.entryType = $("input[name='entryType']").val().capitalizeWords();
        this.secondaryType = $("input[name='secondaryType']").val().capitalizeWords();
        this.formSteps = $("input[name='steps']").val();
        this.viewVal = parseInt(this.viewField.val());

    },
    
    // On page load, make changes to the non-Javascript version for the Javascript version.
    // These are features that will NOT CHANGE throughout the workflow of the Javascript version..
    adjustForJs: function() {
    
        var selectExistingLabel = $('.existing label');
        selectExistingLabel.html(selectExistingLabel.html().replace(/Select (Existing )?/, ''));
        
        this.existingOrNew.hide();
        
        this.toggleRequiredHints('show', this.addNew); 
    },
    
    initForm: function() {
        
    	//Adding a new entry
        if (this.editType == 'add') { 
            this.initAddForm();    
        // Editing an existing entry
        } else { 
            this.initEditForm();
        } 
    },
        
    /***** ADD form *****/

    // Set up add form on page load, or when returning to initial state from step 2
    initAddForm: function() {
        
    	this.defaultButtonText = 'Create ' + this.entryType;
    	this.addNewButtonText = 'Create ' + this.secondaryType + ' & ' + this.entryType;
    	
        // If there are validation errors on the page, we're returning from 
        // an attempted submission that failed validation, and we need to go
        // directly to step 2.
//        if (this.findValidationErrors()) {
//            this.doAddFormStep2();
//        } else {
//            this.doAddFormStep1();
//        }
    	if (this.viewVal) {
    		this.doAddFormStep2(this.viewVal);
    	} else {
    		this.doAddFormStep1();
    	}
    	
    },
    
    // Reset add form to initial state (step 1) after cancelling out of step 2
    resetAddFormToStep1: function() {   

    	customForm.resetForm();        
    	customForm.doAddFormStep1();
    },

    // Set up the add form for step 1
    doAddFormStep1: function() {
    	
    	if (this.formSteps == 1) {
    		customForm.doAddFormStep2(customForm.views.COMBINED);
    		customForm.doClose();
    		return;
    	}
    	
    	customForm.existing.show();
        customForm.toggleRequiredHints('hide', customForm.existing);
    	customForm.addNewLink.show();
    	customForm.addNew.hide();
    	customForm.entry.hide();    
    	customForm.requiredLegend.hide();
    	customForm.button.hide(); 
    	customForm.or.hide();
    	customForm.setViewValue(customForm.views.ADD_STEP_ONE);
        
        // Assign event listeners 
    	customForm.existingSelect.bind('change', function() {
    		if ($(this).val() != '') {
    			customForm.doAddFormStep2(customForm.views.SELECT_EXISTING);
    			return false; 
    		}
        });
        // Note that addNewLink event listener is different
    	// in different views.
    	customForm.addNewLink.bind('click', function() {
    		customForm.doAddFormStep2(customForm.views.ADD_NEW);    		
        });     
    },
    
    // Set up add form for step 2. If no view is passed in, we're returning
    // from a failed submission due to validation errors, and will attempt to
    // determine the previous view from the form data that's been entered.
    doAddFormStep2: function(view) {
        
    	switch (view) {
			case customForm.views.SELECT_EXISTING: { fn = customForm.doAddFormStep2SelectExisting; break; }
    		case customForm.views.ADD_NEW: { fn = customForm.doAddFormStep2AddNew; break; }
    		default: { fn = customForm.doAddFormStep2Combined; break; }
    	}

        fn.call(customForm);  
        
        customForm.button.show();
        customForm.or.show();
        customForm.toggleRequiredHints('show', customForm.existing, customForm.addNew);        
    },

    // Most methods below use 'customForm' rather than 'this', because 'this' doesn't reference
    // customForm when the method is called from an event listener. Only if the method never
    // gets called from an event listener can we use the 'this' reference.
    
    // Step 2: selecting an existing individual
    doAddFormStep2SelectExisting: function() {

    	if (customForm.formSteps == 1) {
    		customForm.doAddFormStep2Combined();
    		return;
    	}
    	customForm.showSelectExistingFields();
    	// This hint shows in step 2 but not in step 1
        customForm.toggleRequiredHints('show', customForm.existing);
        customForm.doButtonForStep2(customForm.defaultButtonText);
        customForm.doCancelForStep2();        
    	customForm.setViewValue(customForm.views.SELECT_EXISTING);
    },
    
    // Step 2: adding a new individual
    doAddFormStep2AddNew: function() {

        customForm.showAddNewFields();
        customForm.doButtonForStep2(customForm.addNewButtonText);
        customForm.doCancelForStep2();
        customForm.doClose();
    	customForm.setViewValue(customForm.views.ADD_NEW);
    },
    
    // Step 2: combined view, when we are returning from validation errors and we
    // can't determine which view of the form we had been on.
    doAddFormStep2Combined: function() {
        
        customForm.showCombinedFields();
        customForm.doAddNewLinkForCombinedView();        
        customForm.doButtonForStep2(customForm.defaultButtonText);        
        customForm.doCancelForStep2();
    	customForm.setViewValue(customForm.views.COMBINED);
    },
    

    /***** Edit form *****/

    initEditForm: function() {
    	
    	this.defaultButtonText = 'Save Changes';
    	this.addNewButtonText = 'Create ' + this.secondaryType + ' & Save Changes';
    	this.toggleRequiredHints('show', this.existing);
    	
    	switch (this.viewVal) {
			case this.views.ADD_NEW: { fn = this.doEditFormAddNew; break; }
			default: { fn = this.doEditFormDefaultView; break; }
    	}
  
        fn.call(customForm);        
    },
    
    doEditFormAddNew: function() {   	
    	this.showAddNewFields();
    	this.button.val(this.addNewButtonText);  
    	this.setViewValue(this.views.ADD_NEW);
    },
    
    doEditFormDefaultView: function() {    	
    	this.showCombinedFields();
    	this.button.val(this.defaultButtonText);   
    	this.doAddNewLinkForCombinedView();
    	this.setViewValue(this.views.COMBINED);
    },
    
    /***** Utilities *****/
 
    unbindEventListeners: function() {
    	customForm.cancel.unbind('click');
    	customForm.button.unbind('click');    
    	customForm.addNewLink.unbind('click');  
    	customForm.close.unbind('click');
    	customForm.existingSelect.unbind('change');
    },
    
    clearFormData: function() {
    	customForm.clearFields(customForm.form);
    },
    
    // Clear data from form elements in element el
    clearFields: function(el) {
    	el.find(':input[type!="hidden"][type!="submit"][type!="button"]').val('');
        
        // For now we can remove the error elements. Later we may include them in
        // the markup, for customized positioning, in which case we will empty them
        // but not remove them here. See findValidationErrors().  
        el.find('.validationError').remove();    	
    }, 
    
    hideFields: function(el) {
        // Clear any input, so if we re-show the element the input won't still be there.
        customForm.clearFields(el);
        el.hide();
    },

    // Add event listener to the submit button in step 2
    doButtonForStep2: function(text) {
    	customForm.button.unbind('click');
    	customForm.button.val(text);
    },
    
    // Add event listener to the cancel link in step 2
    doCancelForStep2: function() {
    	
    	if (customForm.formSteps == 1) { return; }

        customForm.cancel.unbind('click');
        customForm.cancel.bind('click', function() {
            customForm.resetAddFormToStep1();
            return false;
        });         
    },
    
    doAddNewLinkForCombinedView: function() {

    	customForm.addNewLink.unbind('click');
    	customForm.addNewLink.bind('click', function() {
            $(this).hide();
            
            // Make sure to clear out what's in the existing select element,
            // else it could be submitted even when hidden.
            // RY When we have multiple existing and addNew divs, we won't
            // show/hide them all, only the siblings of the addNewLink.
            // And we'll need to figure out the button text based on which
            // div we're opening.
            customForm.hideFields(customForm.existing);
            customForm.addNew.show();
            customForm.button.val(customForm.addNewButtonText);            	
            customForm.doClose();
            return false;
        });  
    },
    
    doClose: function() {

    	if (customForm.formSteps > 1) { return; }

    	customForm.close.unbind('click');
    	customForm.close.bind('click', function() {
    		// RY When we have multiple existing and addNew divs, we won't
    		// show/hide them all, only the siblings of the addNewLink.
    		customForm.existing.show();
    		customForm.hideFields(customForm.addNew);
    		customForm.addNewLink.show();
    		customForm.button.val(customForm.defaultButtonText);
    		customForm.doAddNewLinkForCombinedView();
    		return false;
    	});
    },
    
    // Return true iff there are validation errors on the form
    findValidationErrors: function() {

        return customForm.form.find('.validationError').length > 0;
    	
// RY For now, we just need to look for the presence of the error elements.
// Later, however, we may generate empty error messages in the markup, for
// customized positioning, in which case we need to look for whether they have 
// content. See clearFormData().
//    	var foundErrors = false,
//    	    errors = this.form.find('.validationError'),
//    	    numErrors = errors.length,
//    	    i,
//    	    error;
//    	
//    	for (i = 0; foundErrors == false && i < numErrors; i++) {
//    		error = errors[i];
//    		if (error.html() != '') {
//    			foundErrors = true;
//    		}
//    	}
//    	
//    	return foundErrors;
    },
    
    resetForm: function() {
    	
        // Clear all form data and error messages
        customForm.clearFormData();
        
        // Remove previously bound event handlers
        customForm.unbindEventListeners();    	
    },
    
    showSelectExistingFields: function() {
    	
    	customForm.existing.show();
        customForm.addNewLink.hide();
        customForm.addNew.hide();
        customForm.showFieldsForAllViews();
    },
    
    showAddNewFields: function() {

        customForm.existing.hide();
        customForm.addNewLink.hide();
        customForm.addNew.show();
        customForm.showFieldsForAllViews();      
    },
    
    // This version of the form shows both the existing select and add new link.
    // Used when loading edit form, and when returning from failed submission
    // of add form when we can't determine which view was being used to make
    // the submission.
    showCombinedFields: function() {

    	customForm.existing.show();
    	customForm.addNewLink.show();
    	customForm.addNewLink.css('margin-bottom', '1em');
    	customForm.addNew.hide();       
        customForm.showFieldsForAllViews();
    },
    
    // Show fields that appear in all views for add form step 2 and edit form
    showFieldsForAllViews: function() {
        customForm.entry.show();  
        customForm.requiredLegend.show();     	
    },
    
    // When returning to the add/edit form after a failed form submission (due to 
    // validation errors), attempt to determine which view the form was on when
    // submitted, based on the form data present.
    getPreviousViewFromFormData: function() {
    	
    	// NB ':input' selector includes select elements
        var existingInputs = this.existing.find(':input'),
        	existingInputsLen = existingInputs.length,
        	addNewInputs = this.addNew.find(':input'),
        	addNewInputsLen = addNewInputs.length,
        	input,
        	i,
        	view = null;
    
	    // If a value was entered in the addNew section, go back to the addNew view
	    for (i = 0; i < addNewInputsLen; i++) {
	        input = $(addNewInputs[i]);
	        if (input.val() != '') {
	            view = customForm.views.ADD_NEW;
	            break;
	        }
	    }
	    
	    // If a value was selected in the existing section, go back to the existing view
	    if (view === null) {
	        for (i = 0; i < existingInputsLen; i++) {
	            input = $(existingInputs[i]);
	            if (input.val() != '') {
	                view = customForm.views.SELECT_EXISTING;
	                break;
	            }
	        }
	    }  
	    return view;
    },
    
    toggleRequiredHints: function(action /* elements */) {
    	
    	var hints,
    	    selector = 'span.requiredHint',
    	    numArgs = arguments.length;
    	
    	if (numArgs < 2) {
    		return;
    	}
    	hints = arguments[1].find(selector);
    	for (var i = 2; i < numArgs; i++) { 
    		hints.add(arguments[i].find(selector));
    	}

    	action == 'show' ? hints.show() : hints.hide();
    },
    
    setViewValue: function(value) {
    	customForm.viewField.val(value);
    }

};

$(document).ready(function(){   
    customForm.onLoad();
});
