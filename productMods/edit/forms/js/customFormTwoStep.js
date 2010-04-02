/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/* RY In a later iteration of the custom form Javascript, this will subclass the customForm object. */

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
 * Looks like add form step 2 except shows add new link 
 * Has same three view variants as step 2 add form
 * 
 */

var customFormTwoStep = {

    onLoad: function() {

		this.initObjects();		
        this.adjustForJs();             
        this.initForm();      
    },
    
    // On page load, create references within the customFormTwoStep scope to DOM elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function() {

        this.form = $('#content form');
        this.button = $('#submit');
        this.requiredLegend = $('#requiredLegend');
        
        // These may need to be changed to classes rather than ids, if there are
        // multiple sets of divs to show/hide during the workflow.
        this.addNewLink = $('#addNewLink');
        this.existing = $('#existing');
        this.addNew = $('#new');
        this.entry = $('#entry');
        this.existingOrNew = $('#existingOrNew');

        this.cancel = this.form.find('.cancel');
        this.requiredHints = this.form.find('.requiredHint');        
        
        // Read values used to control display
        this.editType = $("input[name='editType']").val();
        this.entryType = $("input[name='entryType']").val().capitalizeWords();
        this.secondaryType = $("input[name='secondaryType']").val().capitalizeWords();

    },
    
    // On page load, make changes to the non-Javascript version for the Javascript version.
    // These are features that will NOT CHANGE throughout the workflow of the Javascript version..
    adjustForJs: function() {
    
        var selectExistingLabel = $('#existing label');
        selectExistingLabel.html(selectExistingLabel.html().replace(/Select (Existing )?/, ''));
        
        this.existingOrNew.hide();
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
        if (this.findValidationErrors()) {
            this.doAddFormStep2();
        } else {
            this.doAddFormStep1();
        }
    },
    
    // Reset add form to initial state (step 1) after cancelling out of step 2
    resetAddFormToStep1: function() {   

    	customFormTwoStep.resetForm();        
    	customFormTwoStep.doAddFormStep1();
    },

    // Set up the add form for step 1
    doAddFormStep1: function() {

    	customFormTwoStep.existing.show();
    	customFormTwoStep.addNewLink.show();
    	customFormTwoStep.addNew.hide();
    	customFormTwoStep.entry.hide();    
    	customFormTwoStep.requiredLegend.hide();
    	customFormTwoStep.button.val('Continue');  
        
        // Assign event listeners 
        //this.button.unbind('click');   // RY *** Don't need this if we've done a reset
    	customFormTwoStep.button.bind('click', function() {
            customFormTwoStep.doAddFormStep2SelectExisting();           
            return false;              
        });
        
        //this.addNewLink.unbind('click'); // RY *** Don't need this if we've done a reset
    	customFormTwoStep.addNewLink.bind('click', function() {
            customFormTwoStep.doAddFormStep2AddNew();
        });     
    },
    
    // Set up form when returning directly to step 2, such as after validation errors
    // on the form submission.
    doAddFormStep2: function() {

    	var view = customFormTwoStep.getPreviousViewFromFormData();
        
    	switch (view) {
			case "existing": { fn = this.doAddFormStep2SelectExisting; break; }
    		case "addNew": { fn = this.doAddFormStep2AddNew; break; }
    		default: { fn = this.doAddFormStep2Combined; break; }
    	}

        fn.call();             
    },

    // Most methods below use 'customFormTwoStep' rather than 'this', because 'this' doesn't reference
    // customFormTwoStep when the method is called from an event listener. Only if the method never
    // gets called from an event listener can we use the 'this' reference.
    
    // Step 2: selecting an existing individual
    doAddFormStep2SelectExisting: function() {

    	customFormTwoStep.showSelectExistingFields();
        customFormTwoStep.doButtonForStep2(customFormTwoStep.defaultButtonText);
        customFormTwoStep.doCancelForStep2();        
    },
    
    // Step 2: adding a new individual
    doAddFormStep2AddNew: function() {

        customFormTwoStep.showAddNewFields();
        customFormTwoStep.doButtonForStep2(customFormTwoStep.addNewButtonText);
        customFormTwoStep.doCancelForStep2();
    },
    
    // Step 2: combined view, when we are returning from validation errors and we
    // can't determine which view of the form we had been on.
    doAddFormStep2Combined: function() {
        
        customFormTwoStep.showCombinedFields();
        customFormTwoStep.doAddNewLink(customFormTwoStep.addNewButtonText);        
        customFormTwoStep.doButtonForStep2(customFormTwoStep.defaultButtonText);        
        customFormTwoStep.doCancelForStep2();
    },
    

    /***** Edit form *****/

    initEditForm: function() {

    	var view;
    	
    	this.defaultButtonText = 'Save Changes';
    	this.addNewButtonText = 'Create ' + this.secondaryType + ' & Save Changes';
    	
        // If there are validation errors on the page, we're returning from 
        // an attempted submission that failed validation, and we need to go
        // directly to step 2.
        if (this.findValidationErrors()) {
        	view = this.getPreviousViewFromFormData();
        	
        	switch (view) {
    			case "existing": { fn = this.doEditFormSelectExisting; break; }
    			case "addNew": { fn = this.doEditFormAddNew; break; }
    			default: { fn = this.doEditFormDefaultView; break; }
        	}
        } else {
        	fn = this.doEditFormDefaultView;
        }    
        fn.call(customFormTwoStep);        
    },
    
    doEditFormSelectExisting: function() {   	
    	this.showSelectExistingFields();
    	this.button.val(this.defaultButtonText);
    },
    
    doEditFormAddNew: function() {   	
    	this.showAddNewFields();
    	this.button.val(this.addNewButtonText);   	
    },
    
    doEditFormDefaultView: function() {    	
    	this.showCombinedFields();
    	this.button.val(this.defaultButtonText);   
    	this.doAddNewLink(this.addNewButtonText);
    },
    
    /***** Utilities *****/
 
    unbindEventListeners: function() {
    	customFormTwoStep.cancel.unbind('click');
    	customFormTwoStep.button.unbind('click');    
    	customFormTwoStep.addNewLink.unbind('click');   	
    },
    
    clearFormData: function() {
    	customFormTwoStep.clearFields(customFormTwoStep.form);
    },
    
    // Clear data from form elements in element el
    clearFields: function(el) {
    	el.find(':input[type!="hidden"][type!="submit"][type!="button"]').val('');
        
        // For now we can remove the error elements. Later we may include them in
        // the markup, for customized positioning, in which case we will empty them
        // but not remove them here. See findValidationErrors().
        //this.form.find('.validationError').text('');   
        el.find('.validationError').remove();    	
    },
    
    // Add required hints to required fields in a list of elements.
    // Use when the non-Javascript version should not show the required hint,
    // because the field is not required in that version (e.g., it's one of two
    // fields, where one of the two must be filled in but neither one is required). 
    // Arguments: action = 'add' or 'remove'
    // Varargs: element(s)
    toggleRequiredHints: function(action /* elements */) {
    
        var labelText,
            newLabelText,
            requiredHintText = '<span class="requiredHint"> *</span>',
            numArgs = arguments.length;

        for (var i = 1; i < numArgs; i++) {          
            arguments[i].find('label.required').each(function() {
                labelText = $(this).html();
                newLabelText = action == 'add' ? labelText + requiredHintText : 
                                                 labelText.replace(requiredHintText, ''); 
                $(this).html(newLabelText);
            });
        }
    },
    
    showFields: function(el) {
        el.show();
        customFormTwoStep.toggleRequiredHints('add', el);
    },    
    
    hideFields: function(el) {
        // Clear any input, so if we reshow the element the input won't still be there.
        customFormTwoStep.clearFields(el);
        el.hide();
    },

    // Add event listener to the submit button in step 2
    doButtonForStep2: function(text) {
    	customFormTwoStep.button.unbind('click');
    	customFormTwoStep.button.val(text);
    },
    
    // Add event listener to the cancel link in step 2
    doCancelForStep2: function() {
    	
        customFormTwoStep.cancel.unbind('click');
        customFormTwoStep.cancel.bind('click', function() {
            customFormTwoStep.resetAddFormToStep1();
            return false;
        });         
    },
    
    doAddNewLink: function(buttonText) {

    	customFormTwoStep.addNewLink.unbind('click');
    	customFormTwoStep.addNewLink.bind('click', function() {
            $(this).hide();
            // Make sure to clear out what's in the existing select element,
            // else it could be submitted even when hidden.
            customFormTwoStep.hideFields(customFormTwoStep.existing);
            customFormTwoStep.showFields(customFormTwoStep.addNew);
            
            if (buttonText) {
                customFormTwoStep.button.val(buttonText);            	
            }
        });  
    },
    
    // Return true iff there are validation errors on the form
    findValidationErrors: function() {

        return customFormTwoStep.form.find('.validationError').length > 0;
    	
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
        customFormTwoStep.clearFormData();
        
        // Remove previously bound event handlers
        customFormTwoStep.unbindEventListeners();
       
        // Remove required field hints
        customFormTwoStep.toggleRequiredHints('remove', customFormTwoStep.addNew, customFormTwoStep.existing);     	
    },
    
    showSelectExistingFields: function() {
    	
        customFormTwoStep.showFields(customFormTwoStep.existing);  
        customFormTwoStep.addNewLink.hide();
        customFormTwoStep.addNew.hide();
        customFormTwoStep.showFieldsForAllViews();
    },
    
    showAddNewFields: function() {

        customFormTwoStep.existing.hide();
        customFormTwoStep.addNewLink.hide();
        customFormTwoStep.showFields(customFormTwoStep.addNew);
        customFormTwoStep.showFieldsForAllViews();      
    },
    
    // This version of the form shows both the existing select and add new link.
    // Used when loading edit form, and when returning from failed submission
    // of add form when we can't determine which view was being used to make
    // the submission.
    showCombinedFields: function() {

    	customFormTwoStep.showFields(customFormTwoStep.existing);
    	customFormTwoStep.addNewLink.show();
    	customFormTwoStep.addNew.hide();       
        customFormTwoStep.showFieldsForAllViews();
    },
    
    // Show fields that appear in all views for add form step 2 and edit form
    showFieldsForAllViews: function() {
        customFormTwoStep.entry.show();  
        customFormTwoStep.requiredLegend.show();     	
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
	            view = "addNew";
	            break;
	        }
	    }
	    
	    // If a value was selected in the existing section, go back to the existing view
	    if (view === null) {
	        for (i = 0; i < existingInputsLen; i++) {
	            input = $(existingInputs[i]);
	            if (input.val() != '') {
	                view = "existing";
	                break;
	            }
	        }
	    }  
	    return view;
    }

};

$(document).ready(function(){   
    customFormTwoStep.onLoad();
});
