/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var customForm = {

    onLoad: function() {

        // Create references to form elements.
        // NB must be done after the elements have been loaded.

        this.form = $('#content form'),
        this.button = $('#submit'),
        this.requiredLegend = $('#requiredLegend'),
        
        // These may need to be changed to classes rather than ids, if there are
        // multiple sets of divs to show/hide during the workflow.
        this.addNewLink = $('#addNewLink'),
        this.existing = $('#existing'),
        this.addNew = $('#new'),
        this.entry = $('#entry'),
        this.existingOrNew = $('#existingOrNew'),

        this.cancel = this.form.find('.cancel'),
        this.requiredHints = this.form.find('.requiredHint'),        
        
        // Read values used to control display
        this.editType = $("input[name='editType']").val(),
        this.entryType = $("input[name='entryType']").val().capitalize(),
        this.newType = $("input[name='newType']").val().capitalize();
 
        this.adjustForJs();        
         
        if (this.editType == 'add') { //adding a new entry
            this.initAddForm();       
            
        } else { // editing existing entry
            this.initEditForm();
        }            
    },
    
    // On page load, make changes to the non-Javascript version for the Javascript version.
    // These are features that will not change in the Javascript version.
    adjustForJs: function() {
        this.existingOrNew.hide();
        
        var selectExistingLabel = $('#existing label');
        selectExistingLabel.html(selectExistingLabel.html().replace('Select Existing ', ''));

    },
        
    // Reset add form to initial state (step 1)
    // Should only be needed after returning to step 1 from step 2, 
    // but sometimes seems to be needed on page load as well, so call it from initAddForm()
    resetAddForm: function() {
    
        // Clear all form data and error messages
        $('input:text').val('');
        $('.error').text('');
        
        // Remove previously bound event handlers
        this.cancel.unbind('click');
        this.button.unbind('click');    
        this.addNewLink.unbind('click');
        
        this.toggleRequiredHints('remove', this.addNew, this.existing);        
    },

    // Set up add form on page load, or when returning to initial state
    initAddForm: function() {

        this.resetAddForm();
        
        // Step 1 of the form
        this.addNewLink.show();
        this.existing.show();
        this.addNew.hide();
        this.entry.hide();    
        this.requiredLegend.hide();
        this.button.val('Continue');  
        
        // Assign event listeners    
        
        // button => step 2a
        this.button.bind('click', function() {
            customForm.entry.show();  
            customForm.showFields(customForm.existing);          
            customForm.addNewLink.hide();
            customForm.requiredLegend.show();
            
            $(this).val('Create ' + customForm.entryType);
            $(this).unbind('click');
            
            customForm.doCancel();
            
            return false;              
        });
        
        // add new link => step 2b
        this.addNewLink.bind('click', function() {
            $(this).hide();
            customForm.existing.hide();
            customForm.showFields(customForm.addNew);
            customForm.entry.show();
            customForm.requiredLegend.show();
            
            customForm.button.val('Create ' + customForm.entryType + ' & ' + customForm.newType);
            customForm.button.unbind('click');
            
            customForm.doCancel();
        });     
    },
    
    initEditForm: function() {

        this.showFields(this.existing);
        this.addNewLink.show();
        this.addNew.hide();       
        this.entry.show();
        this.requiredLegend.show();
        this.button.val('Save Changes'); 

        this.addNewLink.bind('click', function() {
            $(this).hide();
            customForm.existing.hide();
            customForm.showFields(customForm.addNew);
            
            customForm.button.val('Create ' + customForm.newType + ' & Save Changes');
        });
        
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
            requiredHintText = '<span class="requiredHint"> *</span>';

        for (var i = 1; i < arguments.length; i++) {          
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
        this.toggleRequiredHints('add', el);
    },    
    
    // Add event listener to the cancel link in step 2
    doCancel: function() {
        this.cancel.unbind('click');
        this.cancel.bind('click', function() {
            customForm.initAddForm();
            return false;
        });         
    }

};

$(document).ready(function(){   
    customForm.onLoad();
});
