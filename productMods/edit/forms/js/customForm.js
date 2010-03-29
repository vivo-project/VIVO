/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var customForm = {
    
    onLoad: function() {
        
        var button = $('#submit');
        var addNewLink = $('#addNewLink');
        var existing = $('#existing');
        var addNew = $('#new');
        var entry = $('#entry');
        var editType = $("input[name='editType']").val();
        var entryType = $("input[name='entryType']").val();
        
        if (editType == 'add') {
            // Set up form for step 1
            addNewLink.show();
            addNew.hide();
            entry.hide();             
            button.val('Continue');
        
            // Add event listeners
            button.bind('click', function() {
                entry.show();
                addNewLink.hide();
                $(this).val('Create ' + entryType);
                $(this).unbind('click');
                return false;   
           
            });
        } else { // editing existing entry
            
        }
        
        
        
    }
};

$(document).ready(function(){   
    customForm.onLoad();
});
