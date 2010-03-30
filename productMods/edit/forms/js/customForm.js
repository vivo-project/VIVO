/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var customForm = {

    requiredHintText: ' <span class="requiredHint"> *</span>',  

    onLoad: function() {

        // Create references to form elements.
        // NB must be done after document has loaded, else the elements aren't present 
        // in the DOM yet.
        this.form = $('#content form'),
        this.button = $('#submit'),
        this.addNewLink = $('#addNewLink'),
        this.existing = $('#existing'),
        this.addNew = $('#new'),
        this.entry = $('#entry'),
        this.cancel = this.form.children('.cancel'),
        this.requiredLegend = $('#requiredLegend'), 
        this.requiredHints = $('.requiredHint'),
        
        // Read values used to control display
        this.editType = $("input[name='editType']").val(),
        this.entryType = $("input[name='entryType']").val().capitalize(),
        this.newType = $("input[name='newType']").val().capitalize();
        
        if (this.editType == 'add') { //adding a new entry
            this.initAddForm();
       
        } else { // editing existing entry
            this.initEditForm();
        }                      
    },
    
    // Restore the form to its initial state when returning to step 1
    resetAddForm: function() {

        // Clear all form data and error messages
        $('input:text').val('');
        $('.error').text('');
        // Remove previously bound event handlers
        //this.cancel.unbind('click');
        this.button.unbind('click');    
        
        initAddForm();
    },

    // Set up add form on page load, or when returning to initial state
    // (The latter is not yet implemented, but we are preparing for it. Note
    // that initializations to occur ONLY on page load are done in the onLoad() method.)    
    // RY *** SOME of this will be shared with the edit form - figure out which
    initAddForm: function() {

        // Step 1 of the form
        this.addNewLink.show();
        this.existing.show();
        this.addNew.hide();
        this.entry.hide();          
        this.requiredLegend.hide();
        this.button.val('Continue');  
        
        // Assign event listeners           
        // add new link => step 2b
        this.addNewLink.bind('click', function() {
            $(this).hide();
            customForm.existing.hide();
            customForm.showFields(customForm.addNew);
            customForm.entry.show();
            customForm.requiredLegend.show();
            customForm.button.val('Create ' + customForm.entryType + ' & ' + customForm.newType);
            customForm.button.unbind('click');
            
            // RY This would return us to step 1, but it's not working
            //customForm.cancel.unbind('click');
            //customForm.cancel.bind('click', customForm.resetAddForm);
        });
            
        // button => step 2a
        this.button.bind('click', function() {
            customForm.entry.show();  
            customForm.showFields(customForm.existing);          
            customForm.addNewLink.hide();
            customForm.requiredLegend.show();
            $(this).val('Create ' + customForm.entryType);
            $(this).unbind('click');
            
            // RY This would return us to step 1, but it's not working
            //customForm.cancel.bind('click', customForm.resetAddForm);  
            
            return false;              
        });
    },
    
    initEditForm: function() {
    
    },
     
    // Add required hints to required fields.
    // Use when the non-Javascript version should not show the required hint,
    // because the field is not required in that version.
    addRequiredHints: function(el) {

        var labelText;

        el.children('label.required').each(function() {
            labelText = $(this).html();
            $(this).html(labelText + customForm.requiredHintText);
        });

    },
    
    // We will need to remove some of the required hints when we return to step 1.
    // Not used for now.
    removeRequiredHints: function(el) {
        var labelText;

        el.children('label.required').each(function() {
            labelText = $(this).html();
            $(this).html(labelText.replace(customForm.requiredHintText, ''));
        });    
    },
    
    showFields: function(el) {
        el.show();
        this.addRequiredHints(el);
    },    

};

$(document).ready(function(){   
    customForm.onLoad();
});
