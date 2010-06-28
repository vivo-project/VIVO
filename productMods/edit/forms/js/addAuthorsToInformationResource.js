/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var addAuthorForm = {

    onLoad: function() {

		this.mixIn();
        this.initObjects();                 
        this.initPage();       
    },

    mixIn: function() {
    	// Mix in the custom form utility methods
    	vitro.utils.borrowMethods(vitro.customFormUtils, this);
    },
    
    // On page load, create references within the addAuthorForm scope to DOM elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function() {
    	
    	this.form = $('#addAuthorForm');
    	this.showFormButtonWrapper = $('#showAddForm');
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
        this.lastNameWrapper = this.lastNameField.parent();
        this.selectedAuthor = $('#selectedAuthor');
        this.selectedAuthorName = $('#selectedAuthorName');

    },
    
    // Initial page setup. Called only at page load.
    initPage: function() {

    	// Show elements hidden by CSS for the non-JavaScript-enabled version.
    	// NB The non-JavaScript version of this form is currently not functional.
    	this.removeLinks.show();
    	
    	this.bindEventListeners();
    	
    	this.setUpAutocomplete();
    	
    	if (this.findValidationErrors()) {
    		this.initFormAfterInvalidSubmission();
    	} else {
    		this.initAuthorListOnlyView();
    	}
    },
    
    bindEventListeners: function() {
    	
    	this.showFormButton.click(function() {
    		addAuthorForm.initFormView();
    		return false;
    	});
    	
    	this.submit.click(function() {
    		// NB Important JavaScript scope issue: if we call it this way, this = addAuthorForm 
    		// in prepareFieldValuesForSubmit. If we do
    		// this.submit.click(this.prepareFieldValuesForSubmit); then
    		// this != addAuthorForm in prepareFieldValuesForSubmit.
    		addAuthorForm.prepareFieldValuesForSubmit(); 
    	});   	

    	this.lastNameField.blur(function() {
    		addAuthorForm.onLastNameChange();
    	});
    	
    	// This is somewhat questionable. If we return to the last name
    	// field to look again for an existing person, we'd want to hide those
    	// fields. If we return to it to correct spelling of last name, say,
    	// we wouldn't. 
    	this.lastNameField.focus(function() {
    		addAuthorForm.hideFieldsForNewPerson();
    	});
    	
    	// Prevent form submission when hitting enter in last name field
    	this.lastNameField.keydown(function(event) {
    		if (event.keyCode === 13) {
    			console.log('in keydown')
    		    addAuthorForm.onLastNameChange();
    			return false;
    		}
    	});
    },
    
    onLastNameChange: function() {
    	this.showFieldsForNewPerson();
    	this.firstNameField.focus();
    	// This is persisting and showing old results in some cases unless we
    	// explicitly wipe it out.
    	$('ul.ui-autocomplete li').remove();
    	$('ul.ui-autocomplete').hide();
    },
    
    showFieldsForNewPerson: function() {
    	this.firstNameWrapper.show();
    	this.middleNameWrapper.show();
    },
    
    hideFieldsForNewPerson: function() {   
    	// Hide form fields that shouldn't display on first view.
    	// Includes clearing their contents.
    	this.hideFields(this.firstNameWrapper); 
    	this.hideFields(this.middleNameWrapper); 
    },
    
    // This view shows the list of existing authors and hides the form.
    // There is a button to show the form.
    initAuthorListOnlyView: function() {
    	this.hideForm();
    	this.showFormButtonWrapper.show();
    },
    
    
    // View of form after returning from an invalid submission. On this form,
    // validation errors entail that we were entering a new person, so we show
    // all the fields straightaway.
    initFormAfterInvalidSubmission: function() {
    	this.initForm();
    	this.showFieldsForNewPerson();
    },

    // Initial view of add author form. We get here by clicking the show form button,
    // or by cancelling out of an autocomplete selection.
    initFormView: function() {
    	
    	this.initForm();
    	
    	this.hideFieldsForNewPerson();

    	// This shouldn't be needed, because calling this.hideFormFields(this.lastNameWrapper)
    	// from showSelectedAuthor should do it. However, it doesn't work from there,
    	// or in the cancel action, or if referring to this.lastNameField. None of those work,
    	// however.
    	$('#lastName').val(''); 
    	
    	return false; 
    	
    },
    
    // Form initialization common to both a 'clean' form view and when
    // returning from an invalid submission.
    initForm: function() {
    	
    	// Hide the button that shows the form
		this.showFormButtonWrapper.hide(); 

    	this.hideSelectedAuthor();

    	this.cancel.unbind('click'); 
    	this.cancel.bind('click', function() {
    		addAuthorForm.initAuthorListOnlyView();
    		return false;
    	});
    	
    	// Reset the last name field. It had been hidden if we selected an author from
    	// the autocomplete field.
    	this.lastNameWrapper.show(); 
   	
		// Show the form
		this.form.show(); 
    },
    
    // Action taken after selecting an author from the autocomplete list
    showSelectedAuthor: function(ui) {

		this.personUriField.val(ui.item.uri);
		this.selectedAuthor.show();

		// Transfer the name from the autocomplete to the selected author
		// name display, and hide the last name field.
		this.selectedAuthorName.html(ui.item.label);
		// NB For some reason this doesn't delete the value from the last name
		// field when the form is redisplayed. Thus it's done explicitly in initFormView.
		this.hideFields(this.lastNameWrapper);
		// This shouldn't be needed, because they are already hidden, but for some reason on select 
		// these fields get displayed, even before entering the select event listener.
		this.hideFields(this.firstNameWrapper);
		this.hideFields(this.middleNameWrapper);
		
		// Cancel restores form to initial state
		this.cancel.unbind('click');
		this.cancel.bind('click', function() {
			addAuthorForm.initFormView();
			return false;
		});
    },
    
    hideSelectedAuthor: function() {
    	this.selectedAuthor.hide();
    	this.selectedAuthorName.html('');
    	this.personUriField.val('');
    },
    
    setUpAutocomplete: function() {

    	var cache = {};
    	var url = $('#acUrl').val();
    	var existingAuthorUris = addAuthorForm.getExistingAuthorUris();

    	jQuery.each(existingAuthorUris, function(index, element) {
    		url += '&excludeUri=' + element;
    	});
    	
    	$('#lastName').autocomplete({
    		minLength: 2,
    		source: url,
// RY For now, not using cache because there are complex interactions between filtering and caching.
// We want to filter out existingAuthors from autocomplete results, and this seems easiest to do
// server-side. But if an author gets removed, we need to put them back in the results. If results
// are cached, the cache needs to be cleared on a remove. Not sure if we have access to it there.
// Lots of complexity involved, so for now let's try without the cache.
//    		source: function(request, response) {
//    			if (request.term in cache) {
//    				//console.log("found term in cache");
//    				response(cache[request.term]);
//    				return;
//    			}
//    			
//    			$.ajax({
//    				url: url,
//    				dataType: 'json',
//    				data: request,
//    				complete: function(data) {
//    					cache[request.term] = data;
//    					console.log(data);
//    					//console.log("not getting term from cache");
//    					response(data);
//    				}
//
//    			});
//    		},
		    select: function(event, ui) {
    			addAuthorForm.showSelectedAuthor(ui);		
			}
    	});

    },
    
    getExistingAuthorUris: function() {

    	var existingAuthors = $('#authors .existingAuthor'); 
    	return existingAuthors.map(function() {
    		return $(this).attr('id');
    	});
    	
    },
    
    prepareFieldValuesForSubmit: function() {
    	var firstName,
    	    middleName,
    	    lastName,
    	    name;
    	
    	// If selecting an existing person, don't submit name fields
    	if (this.personUriField.val() != '') {
    		this.firstNameField.attr('disabled', 'disabled');
    		this.middleNameField.attr('disabled', 'disabled');
    		this.lastNameField.attr('disabled', 'disabled');
    	} 
    	else {
    		firstName = this.firstNameField.val();
    		middleName = this.middleNameField.val();
    		lastName = this.lastNameField.val();
    		
    		name = lastName;
    		if (firstName) {
    			name += ', ' + firstName;
    		}
    		if (middleName) {
    			name += ' ' + middleName;
    		}
    		
    		this.labelField.val(name);
    	}

    },
    
    // RY To be implemented later.
    toggleRemoveLink: function() {
    	// when clicking remove: remove the author, and change link text to "undo"
    	// when clicking undo: add the author back, and change link text to "remove"
    }

}

$(document).ready(function() {   
    addAuthorForm.onLoad();
});

