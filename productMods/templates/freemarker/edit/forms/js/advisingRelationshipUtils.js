/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var advisingRelUtils = {
        
    onLoad: function(subject,blankSentinel) {
        this.subjName = '';
        if ( subject ) { this.subjName = subject; }
        
        this.sentinel = '';
        if ( blankSentinel ) { this.sentinel = blankSentinel; }

        this.initObjectReferences();                 
        this.bindEventListeners();
        
        $.extend(this, vitro.customFormUtils);

        if ( this.findValidationErrors() ) {
            this.resetLastNameLabel();
        }
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasAdvisingRelationship');
    this.adRelshiplabel = $('#advisingRelLabel');
    this.advisee = $('#advisee');
    this.subjArea = $('#SubjectArea');
    this.firstName = $('#firstName');
    this.lastName = $('#lastName');
    this.adviseeUri = $('#adviseeUri');
    this.subjAreaUri = $('#subjAreaUri');
    this.saveAdviseeLabel = $('#saveAdviseeLabel');
    this.adviseeAcSelection = $('div#adviseeAcSelection');
    

    // may not need this
    this.firstName.attr('disabled', '');
    
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        //we want to use the advisee label in the relationship label.
        // since the former gets cleared on submit in some cases, store
        //the value in a hidden field and map to relationship label
        this.advisee.change( function(objEvent) {
           window.setTimeout('advisingRelUtils.mapAdviseeValue()', 180); 
        });
        this.advisee.blur( function(objEvent) {
           window.setTimeout('advisingRelUtils.mapAdviseeValue()', 180); 
        });
        
        
        this.form.submit(function() {
            advisingRelUtils.resolveAdviseeNames();
            advisingRelUtils.buildAdvisingRelLabel();
        });            
    },
    
    mapAdviseeValue: function() {
       if ( this.adviseeAcSelection.attr('class').indexOf('userSelected') != -1 ) {
           this.saveAdviseeLabel.val(this.advisee.val());
       }
    },
    resolveAdviseeNames: function() {
        var firstName,
            lastName,
            name;

        // If selecting an existing person, don't submit name fields
        if (this.adviseeUri.val() == '' || this.adviseeUri.val() == this.sentinel ) {
            firstName = this.firstName.val();
            lastName = this.advisee.val();
            
            name = lastName;
            if (firstName) {
                name += ', ' + firstName;
            }            
            this.advisee.val(name);
            this.lastName.val(lastName);
        } 
        else {
            this.firstName.attr('disabled', 'disabled');
            this.lastName.attr('disabled', 'disabled');
        }

    },    

    buildAdvisingRelLabel: function() {
        if ( this.advisee.val() != "" ) {
            this.adRelshiplabel.val(this.subjName + " advising " + this.advisee.val());
        }
        else if ( this.saveAdviseeLabel.val() != "" ){
            this.adRelshiplabel.val(this.subjName + " advising " + this.saveAdviseeLabel.val());
        }
        else {
            this.adRelshiplabel.val(this.subjName + " advising relationship");
        }
    },

    resetLastNameLabel: function() {
        var indx = this.advisee.val().indexOf(", ");
        if ( indx != -1 ) {
            var temp = this.advisee.val().substr(0,indx);
            this.advisee.val(temp);
        }
    }
    
} 
