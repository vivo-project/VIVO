/* $This file is distributed under the terms of the license in /doc/license.txt$ */


var publicationToPersonUtils = {

    onLoad: function(mode) {
        this.initObjectReferences();                 
        this.bindEventListeners();
        
        this.autoDateLabel.hide();
    },

    initObjectReferences: function() {
    
        this.form = $('#addpublicationToPerson');
        this.collection = $('#collection');
        this.book = $('#book');
        this.presentedAt = $('#conference');
        this.proceedingsOf = $('#event');
        this.editor = $('#editor');
        this.editorUri = $('#editorUri');
        this.publisher = $('#publisher');
        this.locale = $('#locale');
        this.volume = $('#volume');
        this.volLabel = $('#volLabel');
        this.number = $('#number');
        this.nbrLabel = $('#nbrLabel');
        this.issue = $('#issue');
        this.issueLabel = $('#issueLabel');
        this.startPage = $('#startPage');
        this.sPLabel = $('#sPLabel');
        this.endPage = $('#endPage');
        this.ePLabel = $('#ePLabel');
        this.typeSelector = $('#typeSelector');
        this.autoDateLabel = null;
        
        this.form.find('label').each(function() {
            if ( $(this).attr('for') == "dateTime-year") {
                publicationToPersonUtils.autoDateLabel = $(this);
            }
        });

        this.pubTitle = $('input#title');
        this.pubAcSelection = $('div#pubAcSelection');
        this.fieldsForNewPub = $('#fieldsForNewPub');
        this.changeLink = this.pubAcSelection.children('p').children('#changeSelection');
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.typeSelector.change(function() {
            publicationToPersonUtils.showFieldsForPub(); 
            publicationToPersonUtils.displayFieldsForType(); 
        });
        
        // we need the delay in the next two functions to ensure the correct timing after the user
        // selects the ac item. The .change handles a mouse click; .blur an arrow key and tab selection
        this.pubTitle.change( function(objEvent) {
           window.setTimeout('publicationToPersonUtils.hideFieldsForPub()', 180); 
        });
        
        this.pubTitle.blur( function(objEvent) {
           window.setTimeout('publicationToPersonUtils.hideFieldsForPub()', 180); 
        });
        
        this.changeLink.click( function() {
           publicationToPersonUtils.showFieldsForPub(); 
        });
    },
    
    hideFieldsForPub: function() {
       if ( this.pubAcSelection.attr('class').indexOf('userSelected') != -1 ) {
           this.fieldsForNewPub.slideUp(250);
       }
    },
    
    showFieldsForPub: function() {
           this.fieldsForNewPub.show();
    },
    
    displayFieldsForType: function() {
        // hide everything, then show what's needed based on type
        // simpler in the event the user changes the type
        this.collection.parent('p').hide();
        this.book.parent('p').hide();
        this.presentedAt.parent('p').hide();
        this.proceedingsOf.parent('p').hide();
        this.editor.parent('p').hide();
        this.publisher.parent('p').hide();
        this.locale.parent('p').hide();
        this.volume.hide();
        this.volLabel.hide();
        this.number.hide();
        this.nbrLabel.hide();
        this.issue.hide();
        this.issueLabel.hide();
        this.startPage.parent('p').hide();
        this.sPLabel.parent('p').hide();
        
        var selectedType = this.typeSelector.find(':selected').text();
        if ( selectedType == 'Academic Article' || selectedType == 'Article' || selectedType == 'Editorial Article' || selectedType == 'Review') {
            this.collection.parent('p').show();
            this.volume.show();
            this.volLabel.show();
            this.number.show();
            this.nbrLabel.show();
            this.issue.show();
            this.issueLabel.show();
            this.startPage.parent('p').show();
            this.sPLabel.parent('p').show();
        }
        else if ( selectedType == 'Chapter' ) {
            this.book.parent('p').show();
            this.editor.parent('p').show();
            this.publisher.parent('p').show();
            this.locale.parent('p').show();            
            this.volume.show();
            this.volLabel.show();
        }
        else if ( selectedType == 'Book' || selectedType == 'Edited Book' ) {
            this.editor.parent('p').show();
            this.publisher.parent('p').show();
            this.locale.parent('p').show();            
            this.volume.show();
            this.volLabel.show();
        }
        else if ( selectedType == 'Conference Paper' ) {
//            this.collection.parent('p').show();
            this.presentedAt.parent('p').show();
//            this.startPage.parent('p').show();
//            this.sPLabel.parent('p').show();
        }
        else if ( selectedType == 'Conference Poster' || selectedType == 'Speech') {
            this.presentedAt.parent('p').show();
        }
     }
   
}