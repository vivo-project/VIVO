/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var manageHideShowStatus = {

    /* *** Initial page setup *** */
   
    onLoad: function() {
    
            this.mixIn();               
            this.initPage();       
        },

    mixIn: function() {

        // Get the custom form data from the page
        $.extend(this, customFormData);
        $.extend(this, i18nStrings);
    },

    // Initial page setup. Called only at page load.
    initPage: function() {

        this.initItemData();
       
        this.bindEventListeners();
                       
    },

    // On page load, associate data with each list item. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // items.
    initItemData: function() {
        $('.itemCheckbox').each(function(index) {
            $(this).data(itemData[index]);  
        });
    },
    
    bindEventListeners: function() {

        $('.itemCheckbox').click(function() {
            manageHideShowStatus.processItem(this);
            //return false;
        });
               
    },
                      
    processItem: function(item) {
        
        var add = "";
        var retract = "";
        var n3String = "<" + $(item).data('relatedUri') + "> <http://vivoweb.org/ontology/core#hideFromDisplay> \"true\" ." ;

        if ( $(item).is(':checked') ) {
            add = n3String;
        }
        else {
            retract = n3String;
        } 
        
        $.ajax({
            url: manageHideShowStatus.processingUrl,
            type: 'POST', 
            data: {
                additions: add,
                retractions: retract
            },
            dataType: 'json',
            context: item, // context for callback
            complete: function(request, status) {
            
                if (status === 'success') {
                    window.status = manageHideShowStatus.itemSuccessfullyExcluded; 

                } else {
                    alert(manageHideShowStatus.errorExcludingItem);
                    $(item).removeAttr('checked');
                }
            }
        });        
    },

};

$(document).ready(function() {   
    manageHideShowStatus.onLoad();
}); 
