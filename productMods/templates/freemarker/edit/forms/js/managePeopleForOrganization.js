/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var managePeople = {

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

        this.initPeopleData();
       
        this.bindEventListeners();
                       
    },

    // On page load, associate data with each list item. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // items.
    initPeopleData: function() {
        $('.pubCheckbox').each(function(index) {
            $(this).data(peopleData[index]);  
        });
    },
    
    bindEventListeners: function() {

        $('.pubCheckbox').click(function() {
            managePeople.processPeople(this);
            //return false;
        });
               
    },
                      
    processPeople: function(person) {
        
        var add = "";
        var retract = "";
        var n3String = "<" + $(person).data('positionUri') + "> <http://vivoweb.org/ontology/core#hideFromDisplay> \"true\" ." ;

        if ( $(person).is(':checked') ) {
            add = n3String;
        }
        else {
            retract = n3String;
        } 
        
        $.ajax({
            url: managePeople.processingUrl,
            type: 'POST', 
            data: {
                additions: add,
                retractions: retract
            },
            dataType: 'json',
            context: person, // context for callback
            complete: function(request, status) {
            
                if (status === 'success') {
                    window.status = "The person has been successfully excluded from the organization page."; 

                } else {
                    alert('Error processing request: the person cannot be excluded from the organization page.');
                    $(person).removeAttr('checked');
                }
            }
        });        
    },

};

$(document).ready(function() {   
    managePeople.onLoad();
}); 
