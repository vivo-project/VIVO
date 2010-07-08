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
        this.fullViewOnly = $('#fullViewOnly');
        this.button = $('#submit');
        this.requiredLegend = $('#requiredLegend');
        this.typeSelector = this.form.find('#typeSelector');
        
        this.or = $('span.or');       
        this.cancel = this.form.find('.cancel');
        
        // These are classed rather than id'd in case we want more than one autocomplete on a form.
        this.acSelector = this.form.find('.acSelector');
        this.acSelection = this.form.find('.acSelection'); 

           
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
              
    },
    
    initFormFullView: function() {
        
        this.fullViewOnly.show();
        this.or.show();
        this.requiredLegend.show();
        this.button.show();
        this.button.val('Create Publication');
        
        this.cancel.click(function() {
            customFormWATS.initFormTypeView();            
        });        
    },
    
    // Bind event listeners that can persist over the life of the page.
    bindEventListeners: function() {
        
        this.typeSelector.change(function() {
            var labelField,
                labelFieldLabel,
                labelText,
                selectedText;
                
            if ($(this).val().length) {
                
                // Set label for label field
                labelField = $('#label');
                labelFieldLabel = $('label[for=' + labelField.attr('id') + ']');
                labelText = labelFieldLabel.html();
                selectedText = $(this).find(':selected').html();
                labelFieldLabel.html(selectedText + ' ' + labelText);
                
                customFormWATS.initFormFullView();
                
                // set ac type 
            }
        });        
    },
    
    initAutocomplete: function() {
        
        // ac selection: disable typeSelector and acSelector
    }
    

};

$(document).ready(function() {   
    customFormWATS.onLoad();
});
