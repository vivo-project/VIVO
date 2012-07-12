/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var manageGrants = {

    /* *** Initial page setup *** */
   
    onLoad: function() {
    
            this.mixIn();               
            this.initPage();       
        },

    mixIn: function() {

        // Get the custom form data from the page
        $.extend(this, customFormData);
    },

    // Initial page setup. Called only at page load.
    initPage: function() {

        this.initGrantData();
       
        this.bindEventListeners();
                       
    },

    // On page load, associate data with each list item. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // items.
    initGrantData: function() {
        $('.grantCheckbox').each(function(index) {
            $(this).data(grantData[index]);  
        });
    },
    
    bindEventListeners: function() {

        $('.grantCheckbox').click(function() {
            manageGrants.processGrant(this);
            //return false;
        });
               
    },
                      
    processGrant: function(grant) {
        
        var add = "";
        var retract = "";
        var n3String = "<" + $(grant).data('roleUri') + "> <http://vivoweb.org/ontology/core#hideFromDisplay> \"true\" ." ;

        if ( $(grant).is(':checked') ) {
            add = n3String;
        }
        else {
            retract = n3String;
        } 
        
        $.ajax({
            url: manageGrants.processingUrl,
            type: 'POST', 
            data: {
                additions: add,
                retractions: retract
            },
            dataType: 'json',
            context: grant, // context for callback
            complete: function(request, status) {
            
                if (status === 'success') {
                    window.status = "The item has been successfully excluded from the profile page."; 

                } else {
                    alert('Error processing request: the item cannot be excluded from the profile page.');
                    $(grant).removeAttr('checked');
                }
            }
        });        
    },

};

$(document).ready(function() {   
    manageGrants.onLoad();
}); 
