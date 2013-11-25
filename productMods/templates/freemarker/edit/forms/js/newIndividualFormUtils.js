/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var newIndividualFormUtils = {

    onLoad: function(mode,country) {
        this.initObjectReferences();                 
        this.bindEventListeners();
    },

    initObjectReferences: function() {
    this.form = $('#newIndividual');
    
    // The external auth ID field and messages
    this.fName = $('#firstName');
    this.lName = $('#lastName');
    this.mName = $('#middleName');
    this.rdfsLabel = $('#label');
    this.submitButton = $('#submit');
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.form.submit(function() {
            newIndividualFormUtils.buildRDFSLabel();
            newIndividualFormUtils.submitButton.attr("disabled",true);
        });    
    
    },

    buildRDFSLabel: function() {
        if ( this.fName.length > 0 ) {
        	var label = this.lName.val() + ", " + this.fName.val();
        	if(this.mName.length > 0) {
        		label += " " + this.mName.val();
        	}
            this.rdfsLabel.val(label);
        }
    },
}

$(document).ready(function() {   
    newIndividualFormUtils.onLoad();
}); 
