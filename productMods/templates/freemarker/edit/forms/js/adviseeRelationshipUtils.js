/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var adviseeRelUtils = {
        
    onLoad: function(subject,blankSentinel) {
        this.subjName = '';
        if ( subject ) { this.subjName = subject; }
        
        this.sentinel = '';
        if ( blankSentinel ) { this.sentinel = blankSentinel; }

        this.initObjectReferences();                 
        this.bindEventListeners();
        
        $.extend(this, vitro.customFormUtils);
        $.extend(this, i18nStrings);

        if ( this.findValidationErrors() ) {
            this.resetLastNameLabel();
        }
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasAdvisingRelationship');
    this.adRelshiplabel = $('#advisingRelLabel');
    this.advisor = $('#advisor');
    this.subjArea = $('#SubjectArea');
    this.firstName = $('#firstName');
    this.lastName = $('#lastName');
    this.advisorUri = $('#advisorUri');
    this.subjAreaUri = $('#subjAreaUri');
    this.saveAdvisorLabel = $('#saveAdvisorLabel');
    this.advisorAcSelection = $('div#advisorAcSelection');
    

    // may not need this
    this.firstName.attr('disabled', '');
    
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        // we want to use the advisor label in the relationship label.
        // since the former gets cleared on submit in some cases, store
        // the value in a hidden field and map to relationship label
        this.advisor.change( function(objEvent) {
           window.setTimeout('adviseeRelUtils.mapAdvisorValue()', 180); 
        });
        this.advisor.blur( function(objEvent) {
           window.setTimeout('adviseeRelUtils.mapAdvisorValue()', 180); 
        });
        
        
        this.form.submit(function() {
            adviseeRelUtils.resolveAdvisorNames();
            adviseeRelUtils.buildAdvisingRelLabel();
            alert(this.adRelshiplabel.val());
            return false;
        });            
    },
    
    mapAdvisorValue: function() {
       if ( this.advisorAcSelection.attr('class').indexOf('userSelected') != -1 ) {
           this.saveAdvisorLabel.val(this.advisor.val());
       }
    },
    resolveAdvisorNames: function() {
        var firstName,
            lastName,
            name;

        // If selecting an existing person, don't submit name fields
        if (this.advisorUri.val() == '' || this.advisorUri.val() == this.sentinel ) {
            firstName = this.firstName.val();
            lastName = this.advisor.val();
            
            name = lastName;
            if (firstName) {
                name += ', ' + firstName;
            }            
            this.advisor.val(name);
            this.lastName.val(lastName);
        } 
        else {
            this.firstName.attr('disabled', 'disabled');
            this.lastName.attr('disabled', 'disabled');
        }

    },    

    buildAdvisingRelLabel: function() {
        alert("here");
        if ( this.advisor.val() != "" ) {
            this.adRelshiplabel.val(this.advisor.val() + " " + adviseeRelUtils.advisingString + " " + this.subjName);
        }
        else if ( this.saveAdvisorLabel.val() != "" ){
            this.adRelshiplabel.val(this.saveAdvisorLabel.val() + " " + adviseeRelUtils.advisingString + " " + this.subjName);
        }
        else {
            this.adRelshiplabel.val(this.subjName + " " + adviseeRelUtils.advisingRelationshipString);
        }
    },

    resetLastNameLabel: function() {
        var indx = this.advisor.val().indexOf(", ");
        if ( indx != -1 ) {
            var temp = this.advisor.val().substr(0,indx);
            this.advisor.val(temp);
        }
    }
    
} 
