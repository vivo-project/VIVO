/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var profilePageType = {

    /* *** Initial page setup *** */
   
    onLoad: function() {
    
            this.mixIn();   
            // in the event that the individual does not have a profile type set,
            // the controller returns "none" and the select is set to "standard".
            // we need to distinguish these when it comes time to do a retraction or
            // not. So "default" = the type defined by the triple; "selected" = 
            // the selected option.
            var selectedProfileType = "";
            this.initPage();
        },

    mixIn: function() {

        // Get the custom form data from the page
        $.extend(this, profileTypeData);
        $.extend(this, i18nStrings);
    },

    // Initial page setup. Called only at page load.
    initPage: function() {
        
        profilePageType.selectedProfileType = $('select#profilePageType').val();
        this.bindEventListeners();
                       
    },
    
    bindEventListeners: function() {
               
        $('select#profilePageType').change( function() {
            profilePageType.processSelection($('select#profilePageType').val())
        });

    },

    processSelection: function(newType) {

        // if no profile page type is defined for this individual, don't do a retraction but pass an empty string to the controller
        var retract = "";
        var add = "<" + profilePageType.individualUri + "> <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#hasDefaultProfilePageType> "
                      + "<http://vitro.mannlib.cornell.edu/ontologies/display/1.1#" + newType + "> .";
        
        if ( profilePageType.defaultProfileType != "none" ) {
            retract = "<" + profilePageType.individualUri + "> <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#hasDefaultProfilePageType> "
                          + "<http://vitro.mannlib.cornell.edu/ontologies/display/1.1#" + profilePageType.selectedProfileType + "> .";
        }

        $.ajax({
            url: profilePageType.processingUrl,
            type: 'POST', 
            data: {
                additions: add,
                retractions: retract
            },
            dataType: 'json',
            context: newType, // context for callback
            complete: function(request, status) {
                
                if (status == 'success') {
                    location.reload(true);
                }
                else {
                    alert(profilePageType.errorProcessingTypeChange);
                    $('select#profilePageType').val(profilePageType.selectedProfileType);
                }
            }
        });        
    }
};

$(document).ready(function() {   
    profilePageType.onLoad();
}); 
