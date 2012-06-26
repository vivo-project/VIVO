/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var mailingAddressUtils = {

    onLoad: function(mode,country) {
        this.initObjectReferences();                 
        this.bindEventListeners();
        this.sortCountrySelector(mode,country);

        if ( mode == "add" && !this.errorSection.attr('id') ) {
            this.containerDiv.hide();
            this.submitButton.hide();
        }
        else {
            this.processCountryRelatedFields();
        }
    },

    initObjectReferences: function() {
    this.form = $('#personHasMailingAddress');
    
    // The external auth ID field and messages
    this.countrySelector = $('#country');
    this.countrySelectorOptions = $('#country option');
    this.address1Field = $('#addrLineOne');
    this.cityField = $('#city');
    this.stateField = $('#state');
    this.stateSelector= $('#stateSelect');
    this.stateLabel = $('#stateLabel');
    this.postalCodeField = $('#postalCode');
    this.postalCodeLabel = $('#postalCodeLabel');
    this.subjectField = $('#subjectName');
    this.rdfsLabel = $('#addrLabel');
    this.addrTypeField = $('#addressType');
    this.submitButton = $('#submit');
    this.containerDiv = $('#addressDetails');
    this.orSpan = $('span.or');
    this.errorSection = $('section#error-alert');
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.countrySelector.change(function() {
            mailingAddressUtils.processCountryRelatedFields();
            mailingAddressUtils.showHiddenElements();
        });
      
        this.form.submit(function() {
            mailingAddressUtils.buildAddressLabel();
        });    
    
        this.stateSelector.change(function() {
            mailingAddressUtils.setStateValue();
        });
    },

    addressClassIsNonUS: function() {
        var country =  this.countrySelector.val();
        if ( country.search( 'United States' ) == -1 ) {
            return true;
        }
        else {
            return false;
        }
    },
    
    buildAddressLabel: function() {
        if ( mailingAddressUtils.addressClassIsNonUS() ) {
            this.rdfsLabel.val(this.address1Field.val() + " " + this.cityField.val() + " " + this.countrySelector.val());
        }
        else {
            this.rdfsLabel.val(this.address1Field.val() + " " + this.cityField.val() + " " + this.stateField.val());
        }    
    },
    
    processCountryRelatedFields: function() {
        if ( mailingAddressUtils.addressClassIsNonUS() ) {
            this.stateLabel.text("Province or Region");
            this.postalCodeField.attr('size', '40');
            this.stateSelector.hide();
            this.stateField.show();
            this.addrTypeField.val("http://vivoweb.org/ontology/core#Address");
        }
        else {
            this.stateLabel.text("State");
            this.postalCodeField.attr('size', '8');
            this.stateField.hide();
            this.stateSelector.show();
            this.addrTypeField.val("http://vivoweb.org/ontology/core#USPostalAddress");
        } 
    },
    
    showHiddenElements: function() {
        this.containerDiv.show();
        this.submitButton.show();
        this.orSpan.show();
    },
    
    setStateValue: function() {
        this.stateField.val(this.stateSelector.val());
    },

    // in the ftl we remove the "the" that precedes some countries, so we need to
    // re-sort them alphabetically
    sortCountrySelector: function(mode,country) {
         // Get options from select box
         var the_options = this.countrySelectorOptions;
         // sort alphabetically
         the_options.sort(function(a,b) {
             if (a.text > b.text) return 1;
             else if (a.text < b.text) return -1;
             else return 0
         })
        //replace with sorted the_options;
        this.countrySelector.append( the_options );

        // if it's add mode, add the "select one" option have it be selected;
        // if it's edit mode, add the "Select one" option but have the correct country selected.
        // if it's repair mode, add the "Select one" option but only select it if there's no country
        
        if ( this.errorSection.is(':visible') ) {
            this.countrySelector.prepend($("<option></option>")
                                .attr("value","")
                                .text("Select one"));
            this.countrySelector.val(country);                 
        }
        else if ( mode == "add" ) {
            this.countrySelector.prepend($("<option selected></option>")
                                .attr("value","")
                                .text("Select one"));
        }
        else if ( mode == "edit" || country.length > 1 ) {
            this.countrySelector.prepend($("<option></option>")
                                .attr("value","")
                                .text("Select one"));
            this.countrySelector.val(country);
        } 
        else if ( country.length == 0 ) {
            this.countrySelector.prepend($("<option selected></option>")
                                .attr("value","")
                                .text("Select one"));
            this.countrySelector.val(country);
        } 
    }
}