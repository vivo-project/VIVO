/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var advisingRelUtils = {
        
    onLoad: function(subject) {
        if ( subject ) { subjName = subject; }

        this.initObjectReferences();                 
        this.bindEventListeners();
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasAdvisingRelationship');
    this.label = $('#advisingRelLabel');
    this.advisee = $('#advisee');
    this.subjArea = $('#SubjectArea');
    this.firstName = $('#firstName');
    this.lastName = $('#lastName');
    this.adviseeUri = $('#adviseeUri');
    
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.form.submit(function() {
            advisingRelUtils.resolveAdviseeNames();
            advisingRelUtils.buildAdvisingRelLabel();
        });            
    },
    
    resolveAdviseeNames: function() {
        var firstName,
            lastName,
            name;

        // If selecting an existing person, don't submit name fields
        if (this.adviseeUri.val() != '') {
            this.firstName.attr('disabled', 'disabled');
            this.lastName.attr('disabled', 'disabled');
        } 
        else {
            firstName = this.firstName.val();
            lastName = this.advisee.val();
            
            name = lastName;
            if (firstName) {
                name += ', ' + firstName;
            }            
            this.advisee.val(name);
            this.lastName.val(lastName);
            
        }

    },    
    buildAdvisingRelLabel: function() {
        if ( this.advisee.val().substring(0, 18) != "Select an existing") {
            this.label.val(subjName + " advising " + this.advisee.val());
        }
        else if ( this.subjArea.val().substring(0, 18) != "Select an existing" ) {
            this.label.val(subjName + " advising in " + this.subjArea.val());
        }
        else {
            this.label.val(subjName + " advising relationship");
        }
    }
    
} 
