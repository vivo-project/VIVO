/* $This file is distributed under the terms of the license in /doc/license.txt$ */


var addOrcidIdToPersonUtils = {

    onLoad: function() {
        this.initObjectReferences();                 
        this.bindEventListeners();
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasOrcidId');
    this.orcidId = $('#orcidId');
    this.orcidIdDisplay = $('#orcidIdDisplay');

    },
    
    bindEventListeners: function() {

        this.form.submit(function() {
            addOrcidIdToPersonUtils.buildOrcidIdURL();
        });    
    
    },
    
    buildOrcidIdURL: function() {
        
        var orcidBase = "http://orcid.org/";
        var orcidIdVal = "";
        if ( this.orcidIdDisplay.val().length > 0 ) {
            if ( this.orcidIdDisplay.val().substring(0,17) == "http://orcid.org/" ) {
                orcidIdVal = this.orcidIdDisplay.val();
            }
            else {
                orcidIdVal = orcidBase + this.orcidIdDisplay.val();
                this.orcidId.val(orcidIdVal);
            }
        }
    },
       
}
$(document).ready(function() {   
    addOrcidIdToPersonUtils.onLoad();
}); 
