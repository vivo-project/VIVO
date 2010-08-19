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
    	this.submit = this.form.find(':submit');
        this.cancel = this.form.find('.cancel'); 
        this.labelField = $('#label');
        this.firstNameField = $('#firstName');
        this.middleNameField = $('#middleName');
        this.lastNameField = $('#lastName');
        this.personUriField = $('#personUri');
        this.firstNameWrapper = this.firstNameField.parent();
        this.middleNameWrapper = this.middleNameField.parent();
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
    	
    	//this.firstNameWrapper.hide();
    	//this.middleNameWrapper.hide();
    	
    	this.showFormButton.click(function() {
    		addAuthorForm.showFormDiv.hide();
    		addAuthorForm.form.show();
    		return false;
    	});
    	
    	this.submit.click(function() {
    		addAuthorForm.insertLabel(); // might be insertLabelOrPersonUri
    	});
    	
    	this.cancel.click(function() {
    		addAuthorForm.hideFields(addAuthorForm.form);
    		addAuthorForm.showFormDiv.show();
    		return false;
    	});

//    	this.setUpAutocomplete();
    	
    },
    
//    setUpAutocomplete: function() {
//
//    	var names = ["Adams", "Bell", "Smith", "Smile", "Smelt", "Younes", "Young"];
//    	$('#lastName').autocomplete({
//    		source: names
//    	});
//
//    },
    
    insertLabel: function() {
    	var firstName,
    	    middleName,
    	    lastName,
    	    name;
    	
    	if (!this.firstNameField.is(':hidden')) {
    		firstName = this.firstNameField.val();
    		middleName = this.middleNameField.val();
    		lastName = this.lastNameField.val();
    		
    		name = lastName;
    		if (firstName) {
    			name += ", " + firstName;
    		}
    		if (middleName) {
    			name += " " + middleName;
    		}
    		
    		this.labelField.val(name);
    	}
    	else {

    	}
    },
    
    toggleRemoveLink: function() {
    	// when clicking remove: remove the author, and change link text to "undo"
    	// when clicking undo: add the author back, and change link text to "remove"
    }

}

$(document).ready(function() {   
    addAuthorForm.onLoad();
});