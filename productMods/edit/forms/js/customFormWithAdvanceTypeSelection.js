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

        // This is the label element for the field with name 'label'
        this.labelFieldLabel = $('label[for=' + $('#label').attr('id') + ']');        
        // Get this on page load, so we can prepend to it. We can't just prepend to the current label text,
        // because it may have already been modified for a previous selection.
        this.baseLabelText = this.labelFieldLabel.html();
        
        this.or = $('span.or');       
        this.cancel = this.form.find('.cancel');
        
        // These are classed rather than id'd in case we want more than one autocomplete on a form.
        this.acSelector = this.form.find('.acSelector');
        this.acSelection = this.form.find('.acSelection'); 
    
    },

    // Set up the form on page load
    initPage: function() {
        
        this.bindEventListeners();
        
        this.initAutocomplete();
        
        if (this.findValidationErrors()) {
            this.initFormFullView();
        } else {
            this.initFormTypeView();
        }
 
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
            customForm.clearFormData(); // clear any input and validation errors
            customForm.initFormTypeView();
            return false;            
        });        
    },
    
    // Bind event listeners that persist over the life of the page.
    bindEventListeners: function() {
        
        this.typeSelector.change(function() {
            var typeVal = $(this).val();
            // Set the type of individual that the autocomplete will search for.
            // We do this even if typeVal is empty, to clear out a previous value.
            customForm.resetAutocomplete(typeVal); 
            if (typeVal.length) {                
                customForm.labelFieldLabel.html(customForm.getSelectedTypeName() + ' ' + customForm.baseLabelText);
                customForm.initFormFullView();              
            } else {
                // If no selection, go back to type view. This prevents problems like trying to run autocomplete
                // or submitting form without a type selection.
                customForm.initFormTypeView();
            }     
        });        
    },
    
    initAutocomplete: function() {
        
        var acFilter = this.getAcFilter();
        
        this.acCache = {};
        this.baseAcUrl = customFormData.acUrl; 
        
        this.acSelector.autocomplete({
            minLength: 3,
            source: function(request, response) {
                if (request.term in customForm.acCache) {
                    // console.log('found term in cache');
                    response(customForm.acCache[request.term]);
                    return;
                }
                // console.log('not getting term from cache');
                
                $.ajax({
                    url: customForm.acUrl,
                    dataType: 'json',
                    data: request,
                    complete: function(xhr, status) {
                        // Not sure why, but we need an explicit json parse here. jQuery
                        // should parse the response text and return a json object.
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
    
    getAcFilter: function() {
        // RY This gets put on the page for now. May want to put into a js file instead.
        var url = $('.sparqlQueryUrl').attr('id');    

        $.ajax({
            url: customFormData.sparqlQueryUrl,
            data: {
                resultFormat: 'RS_JSON',
                query: customFormData.sparqlForAcFilter
            },
            success: function(data, status, xhr) {
                console.log(data);
            }
        })
    },

    // Reset some autocomplete values after type is changed
    resetAutocomplete: function(typeVal) {
        // Append the type parameter to the base autocomplete url
        var glue = this.baseAcUrl.indexOf('?') > -1 ? '&' : '?';
        this.acUrl = this.baseAcUrl + glue + 'type=' + typeVal;
        
        // Flush autocomplete cache when type is reset, since the cached values 
        // are relevant only to the previous type.
        this.acCache = {};
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
