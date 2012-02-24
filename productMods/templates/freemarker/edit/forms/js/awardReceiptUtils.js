/* $This file is distributed under the terms of the license in /doc/license.txt$ */


var awardReceiptUtils = {

    onLoad: function(mode) {
        this.initObjectReferences();                 
        this.bindEventListeners();
        // in edit mode, copy the year awarded to the displayed input element
        if ( mode == "edit" ) {
            this.displayedYear.val(this.yearAwarded.val());
        }
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasAwardOrHonor');
    this.recLabel = $('#awardReceiptLabel');
    this.award = $('#award');
    this.yearAwarded = $('#yearAwarded-year');
    this.displayedYear = $('#yearAwardedDisplay');
    
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.form.submit(function() {
            awardReceiptUtils.setYearAwardedLabel();
            awardReceiptUtils.buildAwardReceiptLabel();
        });    
    
    },
    
    buildAwardReceiptLabel: function() {
        var rdfsLabel = this.award.val();
        if ( this.yearAwarded.val().length ) {
            rdfsLabel += " (" + this.yearAwarded.val() + ")";
        }
        this.recLabel.val(rdfsLabel);
    },

    setYearAwardedLabel: function() {
        this.yearAwarded.val(this.displayedYear.val());
    }
       
}