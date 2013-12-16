/* $This file is distributed under the terms of the license in /doc/license.txt$ */


var mailingAddressUtils = {

    onLoad: function(editMode) {
        this.initObjectReferences();                 
        this.bindEventListeners();
        
        if ( editMode != "add" ) {
            this.setStreetAddressDisplayFields();
        }
        
        this.setAddressFieldsIfErrors();
    },

    initObjectReferences: function() {
    
        this.form = $('#personHasMailingAddress');
        this.street1 = $('#streetAddressOne');
        this.street2 = $('#streetAddressTwo');
        this.streetAddress = $('#streetAddress');
        this.errorSection = $('section#error-alert');

    },
    
    setAddressFieldsIfErrors: function() {
      if ( this.errorSection.length ) {
          mailingAddressUtils.setStreetAddressDisplayFields();
      }
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.form.submit(function() {
            mailingAddressUtils.setStreetAddressField();
        }); 
        
    },

    // the vcard only has one address field, so combine the two 
    // displayed addresses into the hidden field which gets asserted in the N3
    setStreetAddressField: function() {
        var tempString = this.street1.val() + "; " + this.street2.val();
        this.streetAddress.val(tempString);
    },

    // in edit mode, take the asserted streetAddress value and parse it into
    // the two displayed address fields
    setStreetAddressDisplayFields: function() {
        var tempString = this.streetAddress.val();
        var lineOne = tempString.substring(0,tempString.lastIndexOf(";"));
        var lineTwo = tempString.substring(tempString.lastIndexOf(";") + 2);
        
        this.street1.val(lineOne);
        this.street2.val(lineTwo);
    }
    
}