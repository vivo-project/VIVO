/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var addAuthorForm = {

    onLoad: function() {

        this.initObjects();     
        this.adjustForJs();             
        this.initForm();       
    },

    
    // On page load, create references within the customForm scope to DOM elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function() {
    	
    	this.form = $('#addAuthorForm');
    	this.showFormButton = $('#showAddForm');
    	this.removeLinks = $('a.remove');
    },

    // On page load, make changes to the non-Javascript version for the Javascript version.
    // These are features that will NOT CHANGE throughout the workflow of the Javascript version.
    adjustForJs: function() {
    	
    	// Show elements that are hidden by css on load since not used in non-JS version
    	this.showFormButton.show();
    	this.removeLinks.show();
    	
    	this.form.hide();
    },
    
    initForm: function() {
    	
    	this.showFormButton.bind('click', function() {
    		$(this).hide();
    		addAuthorForm.form.show();
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