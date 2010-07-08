/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var customFormWATS = {
    
    /* *** Initial page setup *** */
   
    onLoad: function() {
        this.mixIn();
        this.initObjects();                 
        this.initPage();       
    },

    mixIn: function() {
        // Mix in the custom form utility methods
        vitro.utils.borrowMethods(vitro.customFormUtils, this);
    },
    
    // On page load, create references for easy access to form elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function(){
        
        this.form = $('#content form');
        this.button = $('#submit');
        this.or = $('span.or');
        this.requiredLegend = $('#requiredLegend');

        this.typeSelector = this.form.find('.typeSelector');
        this.acInput = this.form.find('.acInput');
        this.acSelection = this.form.find('.acSelection');        
        this.cancel = this.form.find('cancel');
           
    },

    // Set up the form on page load
    initPage: function() {
        
        this.initFormTypeView();
        this.bindEventListeners();
        this.initAutocomplete();
 
    },
    
    initFormTypeView: function() {

        this.button.hide();
        this.or.hide();
        this.requiredLegend.hide();

        this.cancel.unbind('click');
        this.cancel('click', function() {
            
        });                
    },
    
    initFormFullView: function() {
        
        this.button.show();
        this.or.show();
        this.requiredLegend.show();
        
        this.cancel.unbind('click');
        this.cancel('click', function() {
            
        });        
    },
    
    // Bind event listeners that apply to all form views
    bindEventListeners: function() {
        
    },
    
    initAutocomplete: function() {
        
    }
    

};

$(document).ready(function() {   
    customFormWATS.onLoad();
});
