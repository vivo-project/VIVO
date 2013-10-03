/* $This file is distributed under the terms of the license in /doc/license.txt$ */


var addOrcidIdToPersonUtils = {

    onLoad: function() {
        this.initObjectReferences();                 
        this.bindEventListeners();
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasOrcidId');
    this.orcidId = $('#orcidId');

    },
    
    bindEventListeners: function() {

        this.form.submit(function() {
            addOrcidIdToPersonUtils.buildOrcidIdURL();
        });    
    
    },
    
    buildOrcidIdURL: function() {
        
        var orcidBase = "http://www.orcid.org/";
        var orcidIdVal = "";
        if ( this.orcidId.val().length > 0 ) {
            orcidIdVal = orcidBase + this.orcidId.val();
            this.orcidId.val(orcidIdVal);
        }
    },
       
}
$(document).ready(function() {   
    addOrcidIdToPersonUtils.onLoad();
}); 
