/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/* ADD new property form
/* Step 1: Initial step, with choice to select existing or add new secondary individual.
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

var customForm = {

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
        this.entryType = $("input[name='entryType']").val().capitalize();
        this.newType = $("input[name='newType']").val().capitalize();
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

    	customForm.resetForm();        
    	customForm.doAddFormStep1();
    },

    // Set up the add form for step 1
    doAddFormStep1: function() {

    	customForm.existing.show();
    	customForm.addNewLink.show();
    	customForm.addNew.hide();
    	customForm.entry.hide();    
    	customForm.requiredLegend.hide();
    	customForm.button.val('Continue');  
        
        // Assign event listeners 
        //this.button.unbind('click');   // RY *** Don't need this if we've done a reset
    	customForm.button.bind('click', function() {
            customForm.doAddFormStep2SelectExisting();           
            return false;              
        });
        
        //this.addNewLink.unbind('click'); // RY *** Don't need this if we've done a reset
    	customForm.addNewLink.bind('click', function() {
            customForm.doAddFormStep2AddNew();
        });     
    },
    
    // Set up form when returning directly to step 2, such as after validation errors
    // on the form submission.
    doAddFormStep2: function() {
        
        // If possible, determine which view of the form we were on
        var existingInputs = this.existing.find(':input'),
            existingInputsLen = existingInputs.length,
            addNewInputs = this.addNew.find(':input'),
            addNewInputsLen = addNewInputs.length,
            input,
            i,
            fn = null;
        
        // If a value was entered in the addNew section, go back to the addNew view
        for (i = 0; i < addNewInputsLen; i++) {
            input = $(addNewInputs[i]);
            if (input.val() != '') {
                fn = this.doAddFormStep2AddNew;
                break;
            }
        }
        
        // If a value was selected in the existing section, go back to the existing view
        if (fn === null) {
            for (i = 0; i < existingInputsLen; i++) {
                input = $(existingInputs[i]);
                if (input.val() != '') {
                    fn = this.doAddFormStep2SelectExisting;
                    break;
                }
            }
        }
        
        // Otherwise, default to the combined view
        // (same as view used to edit existing entry)
        if (fn === null) {
            fn = this.doAddFormStep2Combined;
        }

        fn.call();             
    },
    
    // Most methods below use 'customForm' rather than 'this', because 'this' doesn't reference
    // customForm when the method is called from an event listener. Only if the method never
    // gets called from an event listener can we use the 'this' reference.
    
    // Step 2: selecting an existing individual
    doAddFormStep2SelectExisting: function() {
        
        customForm.entry.show();  
        customForm.showFields(customForm.existing);  
        customForm.addNew.hide();
        customForm.addNewLink.hide();
        customForm.requiredLegend.show();
        
        customForm.doButtonForStep2('Create ' + customForm.entryType);
        customForm.doCancelForStep2();        
    },
    
    // Step 2: adding a new individual
    doAddFormStep2AddNew: function() {

        // NB Use customForm instead of 'this', because 'this' 
        // doesn't reference customForm when called from an event handler.
        customForm.addNewLink.hide();
        customForm.existing.hide();
        customForm.showFields(customForm.addNew);
        customForm.entry.show();
        customForm.requiredLegend.show();

        customForm.doButtonForStep2('Create ' + customForm.entryType + ' & ' + customForm.newType);
        customForm.doCancelForStep2();
    },
    
    // Step 2: combined view, when we are returning from validation errors and we
    // can't determine which view of the form we had been on.
    doAddFormStep2Combined: function() {
        
        customForm.showCombinedView();
        customForm.doAddNewLink();        
        customForm.doButtonForStep2('Create ' + customForm.newType);        
        customForm.doCancelForStep2();
    },
    

    /***** Edit form *****/

    // RY Here we need logic for returning from validation errors, as in add form ********
    initEditForm: function() {

    	this.showCombinedView();
        this.doAddNewLink();       
        this.button.val('Save Changes'); 
        // Cancel just takes us back to the individual page - no event listener needed
        
    },
    
    /***** Utilities *****/
 
    unbindEventListeners: function() {
    	customForm.cancel.unbind('click');
    	customForm.button.unbind('click');    
    	customForm.addNewLink.unbind('click');   	
    },
    
    clearFormData: function() {
    	customForm.clearFields(customForm.form);
    },
    
    // Clear data from form elements in element el
    clearFields: function(el) {
        el.find('input:text').val('');
        el.find('select').val('');
        el.find('textarea').val('');
        
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
        customForm.toggleRequiredHints('add', el);
    },    
    
    hideFields: function(el) {
        // Clear any input, so if we reshow the element the input won't still be there.
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
    	
        customForm.cancel.unbind('click');
        customForm.cancel.bind('click', function() {
            customForm.resetAddFormToStep1();
            return false;
        });         
    },
    
    doAddNewLink: function() {

    	customForm.addNewLink.unbind('click');
    	customForm.addNewLink.bind('click', function() {
            $(this).hide();
            customForm.hideFields(customForm.existing);
            customForm.showFields(customForm.addNew);
            
            customForm.button.val('Create ' + customForm.newType + ' & Save Changes');
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
       
        // Remove required field hints
        customForm.toggleRequiredHints('remove', customForm.addNew, customForm.existing);     	
    },
    
    // This version of the form shows both the existing select and add new link.
    // Used when loading edit form, and when returning from failed submission
    // of add form when we can't determine which view was being used to make
    // the submission.
    showCombinedView: function() {

    	customForm.showFields(customForm.existing);
    	customForm.addNewLink.show();
    	customForm.addNew.hide();       
    	customForm.entry.show();
    	customForm.requiredLegend.show();
    }

};

$(document).ready(function(){   
    customForm.onLoad();
});
