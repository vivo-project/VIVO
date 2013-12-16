/* $This file is distributed under the terms of the license in /doc/license.txt$ */


var awardReceiptUtils = {

    onLoad: function(mode, subjectName, href) {
        this.initObjectReferences();                 
        this.bindEventListeners();
        this.baseHref = href;
        this.editMode = mode;
        $.extend(this, vitro.customFormUtils);
        $.extend(this, i18nStrings);

        // in edit mode copy the year awarded to the displayed input element
        if ( this.editMode == "edit"  ) {
            this.hiddenOrgDiv = $('div#hiddenOrgLabel');
            this.displayedYear.val(this.yearAwarded.val());
            if ( this.org.val() != '' ) {
                window.setTimeout('awardReceiptUtils.hiddenOrgDiv.removeClass("hidden")', 100);
                window.setTimeout('awardReceiptUtils.orgAcSelection.hide()', 100);
            }
        }
        this.subjectName = subjectName;
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasAwardOrHonor');
    this.recLabel = $('#awardReceiptLabel');
    this.award = $('#award');
    this.awardDisplay = $('#awardDisplay');
    this.org = $('#org');
    this.yearAwarded = $('#yearAwarded-year');
    this.displayedYear = $('#yearAwardedDisplay');
    this.awardAcSelection = $('div#awardAcSelection');
    this.orgAcSelection = $('div#orgAcSelection');
    this.orgUriReceiver = $('input#orgUri');
    this.changeLink = this.awardAcSelection.children('p').children('#changeSelection');

    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        // the delay ensures that the function is called after the ac selection is completed
        this.award.change( function(objEvent) {
           window.setTimeout('awardReceiptUtils.hideConferredBy()', 180); 
        });
        
        this.award.blur( function(objEvent) {
           window.setTimeout('awardReceiptUtils.hideConferredBy()', 180); 
        });

        this.form.submit(function() {
            awardReceiptUtils.setYearAwardedValue();
            awardReceiptUtils.buildAwardReceiptLabel();
        });    
    
        this.changeLink.click( function() {
           awardReceiptUtils.showConferredBy(); 
        });
    },
    
    hideConferredBy: function() {
        if ( this.awardAcSelection.attr('class').indexOf('userSelected') != -1 ) {
            this.org.parent('p').hide();
            this.org.val('');
            this.resetAcSelection();       }
    },

    showConferredBy: function() {
        this.org.val(awardReceiptUtils.selectAnOrganization);
        this.org.addClass('acSelectorWithHelpText');
        this.org.parent('p').show();
        if ( this.editMode == "edit" ) {
            this.hiddenOrgDiv.hide();
        }
        this.resetAcSelection();
    },

    resetAcSelection: function() {
        var $acSelection = $("div.acSelection[acGroupName='org']");
        
        if ( this.orgUriReceiver.val() != '' ) {
            this.hideFields($acSelection);
            $acSelection.removeClass('userSelected');
            $acSelection.find("span.acSelectionInfo").text('');
            $acSelection.find("a.verifyMatch").attr('href', this.baseHref);
        }
    },

    buildAwardReceiptLabel: function() {
        var rdfsLabel = "";
        var $acSelection = $("div.acSelection[acGroupName='award']")
        if ( $acSelection.find("span.acSelectionInfo").text().length > 0 ) {
            rdfsLabel = $("span.acSelectionInfo").text();
        }
        else {
            rdfsLabel = this.award.val();
        }
        if ( this.yearAwarded.val().length ) {
            rdfsLabel += " (" + this.subjectName + ' - ' + this.yearAwarded.val() + ")";
        }
        else {
            rdfsLabel += " (" + this.subjectName + ")";
        }
        this.recLabel.val(rdfsLabel);
    },

    setYearAwardedValue: function() {
        this.yearAwarded.val(this.displayedYear.val());
    }
       
}