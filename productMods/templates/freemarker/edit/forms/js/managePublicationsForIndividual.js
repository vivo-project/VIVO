/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var managePublications = {

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

        this.initPublicationData();
       
        this.bindEventListeners();
                       
    },

    // On page load, associate data with each list item. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // items.
    initPublicationData: function() {
        $('.pubCheckbox').each(function(index) {
            $(this).data(publicationData[index]);  
        });
    },
    
    bindEventListeners: function() {

        $('.pubCheckbox').click(function() {
            managePublications.processPublication(this);
            //return false;
        });
               
    },
                      
    processPublication: function(publication) {
        
        var add = "";
        var retract = "";
        var n3String = "<" + $(publication).data('authorshipUri') + "> <http://vivoweb.org/ontology/core#hideFromDisplay> \"true\" ." ;

        if ( $(publication).is(':checked') ) {
            add = n3String;
        }
        else {
            retract = n3String;
        } 
        
        $.ajax({
            url: managePublications.processingUrl,
            type: 'POST', 
            data: {
                additions: add,
                retractions: retract
            },
            dataType: 'json',
            context: publication, // context for callback
            complete: function(request, status) {
            
                if (status === 'success') {
                    window.status = "The publication will has been successfully excluded from the profile page."; 

                } else {
                    alert('Error processing request: the publication cannot be excluded from the profile page.');
                    $(publication).removeAttr('checked');
                }
            }
        });        
    },

};

$(document).ready(function() {   
    managePublications.onLoad();
}); 
