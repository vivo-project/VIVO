/* $This file is distributed under the terms of the license in /doc/license.txt$ */


var issuedCredentialUtils = {

    onLoad: function() {
        this.initObjectReferences();                 
        this.bindEventListeners();
        $.extend(this, credentials);

        // copy the year awarded to the displayed input element for edit mode
        if ( this.yearCredentialed.val().length > 0  ) {
            this.displayedYear.val(this.yearCredentialed.val());
        }
    },

    initObjectReferences: function() {
    
        this.form = $('#personHasIssuedCredential');
        this.yearCredentialed = $('#yearCredentialed-year');
        this.displayedYear = $('#yearCredentialedDisplay');
        this.typeSelector = $('#typeSelector');
        this.issuedType = $('#issuedCredentialType');

    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.form.submit(function() {
            issuedCredentialUtils.setIssuedCredentialType($(issuedCredentialUtils.typeSelector).find(":selected").text());
            issuedCredentialUtils.setYearCredentialedValue();
        }); 
        
    },
    
    setYearCredentialedValue: function() {
        this.yearCredentialed.val(this.displayedYear.val());
    },
    
    setIssuedCredentialType: function(val) {
        this.issuedType.val(credentials[val]);
    }
       
}