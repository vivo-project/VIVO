/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var customForm = {
    
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
        //this.initAutocomplete();
 
    },
    
    initFormTypeView: function() {

        this.hideFields(this.fullViewOnly);
        this.button.hide();
        this.requiredLegend.hide();
        this.or.hide();

        this.cancel.unbind('click');
              
    },
    
    initFormFullView: function() {
        
        this.fullViewOnly.show();
        this.or.show();
        this.requiredLegend.show();
        this.button.show();
        this.button.val('Create Publication');
        
        this.cancel.unbind('click');
        this.cancel.click(function() {
            customForm.initFormTypeView();
            return false;            
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
                labelFieldLabel.html(customForm.getSelectedTypeName() + ' ' + labelText);
                
                customForm.initFormFullView();
                
                // set ac type 
            }            
            // do we need else case? i.e. if user has set back to "select one", we should undo
            // above settings?
        });        
    },
    
    initAutocomplete: function() {
        // Make cache a property of this so we can access it after removing 
        // an author.
        this.acCache = {};
        this.baseAcUrl = $('.acUrl').attr('id');        
        
        this.acSelector.autocomplete({
            minLength: 3,
            source: function(request, response) {
                if (request.term in customForm.acCache) {
                    // console.log('found term in cache');
                    response(customForm.acCache[request.term]);
                    return;
                }
                // console.log('not getting term from cache');
                
                // If the url query params are too long, we could do a post
                // here instead of a get. Add the exclude uris to the data
                // rather than to the url.
                $.ajax({
                    url: customForm.acUrl,
                    dataType: 'json',
                    data: request,
                    complete: function(xhr) {
                        // Not sure why, but we need an explicit json parse here. jQuery
                        // should parse the response text and return an json object.
                        var results = jQuery.parseJSON(xhr.responseText);
                        customForm.acCache[request.term] = results;  
                        response(results);
                    }

                });
            },
            select: function(event, ui) {
                customForm.showAutocompleteSelection(ui);   
                    
            }
        });

    },        
        
    showAutocompleteSelection: function(ui) {
        
        this.acSelector.hide();
        this.acSelector.attr('disabled', 'disabled');
 
        this.acSelection.find('label').html('Selected ' + this.getSelectedTypeName() + ':');       
        this.acSelection.show();

        this.acReceiver.val(ui.item.uri);
        this.acSelectionInfo.html(ui.item.label);
        
        this.button.val('Add Publication');
        
        this.cancel.unbind('click');
        this.cancel.click(function() {
            // TODO Check out cancel action for authors form. Need to undo/empty some of the stuff above.
            // do we do it in the initfullview method, or here?
            customForm.initFormFullView();
            return false;
        });

    },
    
    getSelectedTypeName: function() {
        return this.typeSelector.find(':selected').html();
    }
    
};

$(document).ready(function() {   
    customForm.onLoad();
});
