/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var addAuthorForm = {

    onLoad: function() {

		this.mixIn();
        this.initObjects();     
        this.adjustForJs();             
        this.initForm();       
    },

    mixIn: function() {
    	// Mix in the custom form utility methods
    	vitro.utils.borrowMethods(vitro.customFormUtils, this);
    },
    
    // On page load, create references within the customForm scope to DOM elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function() {
    	
    	this.form = $('#addAuthorForm');
    	this.showFormDiv = $('#showAddForm');
    	this.showFormButton = $('#showAddFormButton');
    	this.removeLinks = $('a.remove');
        this.cancel = this.form.find('.cancel'); 
    },

    // On page load, make changes to the non-Javascript version for the Javascript version.
    // These are features that will NOT CHANGE throughout the workflow of the Javascript version.
    adjustForJs: function() {
    	
    	// Show elements that are hidden by css on load since not used in non-JS version
    	this.showFormDiv.show();
    	this.removeLinks.show();
    	
    	this.form.hide();
    },
    
    initForm: function() {
    	

    	
    	this.showFormButton.click(function() {
    		addAuthorForm.showFormDiv.hide();
    		addAuthorForm.form.show();
    		return false;
    	});
    	
    	this.cancel.click(function() {
    		addAuthorForm.hideFields(addAuthorForm.form);
    		addAuthorForm.showFormDiv.show();
    		return false;
    	});
    },
    
    toggleRemoveLink: function() {
    	// when clicking remove: remove the author, and change link text to "undo"
    	// when clicking undo: add the author back, and change link text to "remove"
    }

}

$(document).ready(function() {   
    addAuthorForm.onLoad();
});