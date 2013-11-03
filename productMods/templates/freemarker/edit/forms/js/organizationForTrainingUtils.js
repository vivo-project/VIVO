/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var orgForTrainingUtils = {
        
    onLoad: function(blankSentinel) {
        
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
    
    this.form = $('#organizationForTraining');
    this.person = $('#person');
    this.fauxLabel = $('#maskLabelBuilding');
    this.firstName = $('#firstName');
    this.lastName = $('#lastName');
    this.personUri = $('#personUri');
    this.aDLabel = $('#awardedDegreeLabel');
    this.degreeSelector = $('#degreeUri');    

    // may not need this
    this.firstName.attr('disabled', '');
    
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.form.submit(function() {
            orgForTrainingUtils.resolveFirstLastNames();

        });            
    },
    
    resolveFirstLastNames: function() {
        var firstName,
            lastName,
            name;

        // If selecting an existing person, don't submit name fields
        if (this.personUri.val() == '' || this.personUri.val() == this.sentinel ) {
            firstName = this.firstName.val();
            lastName = this.person.val();
            
            name = lastName;

            if (firstName) {
                name += ', ' + firstName;
            }            

            // we don't want the user to see the label getting built, so hide the acSelector
            // field and display a bogus field that just has the last name in it.
            this.fauxLabel.val(lastName);
            this.person.hide();
            this.fauxLabel.show();
            this.person.val(name);
            this.lastName.val(lastName);
        } 
        else {
            this.firstName.attr('disabled', 'disabled');
            this.lastName.attr('disabled', 'disabled');
        }
        orgForTrainingUtils.setAwardedDegreeLabel(this.person.val()); 
    },    

    resetLastNameLabel: function() {
        var indx = this.person.val().indexOf(", ");
        if ( indx != -1 ) {
            var temp = this.person.val().substr(0,indx);
            this.person.val(temp);
        }
    },
    
    setAwardedDegreeLabel: function(name) {
        var degreeLabel = "";
        if ( name == '' || name == null ) {
            degreeLabel = $('span.acSelectionInfo').text() + ": " + this.degreeSelector.find(":selected").text();
        }
        else {
            degreeLabel = name + ": " + this.degreeSelector.find(":selected").text();
        }
        this.aDLabel.val(degreeLabel);
    }    
} 
