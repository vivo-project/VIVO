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
    	
    	// On this form, validation errors entail that a new person was being entered.
    	if (this.findValidationErrors()) {
    		this.initFormView();
    		this.showFieldsForNewPerson();
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
    		console.log("in blur")
    		addAuthorForm.onLastNameChange();
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
    },
    
    showFieldsForNewPerson: function() {
    	this.firstNameWrapper.show();
    	this.middleNameWrapper.show();
    },

    // This view shows the list of existing authors and hides the form.
    // There is a button to show the form.
    initAuthorListOnlyView: function() {
    	this.hideForm();
    	this.showFormButtonWrapper.show();
    },
    
    // Initial view of add author form
    initFormView: function() {

    	// Hide the button that shows the form
		this.showFormButtonWrapper.hide(); 

		// Hide form fields that shouldn't display on first view.
		// Includes clearing their contents.
		this.hideFields(this.firstNameWrapper);
		this.hideFields(this.middleNameWrapper);
    	this.hideSelectedAuthor();
    	
    	this.cancel.unbind('click');
    	this.cancel.bind('click', function() {
    		addAuthorForm.initAuthorListOnlyView();
    		return false;
    	});
    	
    	// Reset the last name field. It had been hidden if we selected an author from
    	// the autocomplete field.
    	this.lastNameWrapper.show();
    	// This shouldn't be needed, because calling this.hideFormFields(this.lastNameWrapper)
    	// from showSelectedAuthor should do it. However, it doesn't work from there,
    	// or in the cancel action, or if referring to this.lastNameField. None of those work,
    	// however.
    	$('#lastName').val('');
    	
		// Show the form
		this.form.show();

		return false;
    },
    
    // Action taken after selecting an author from the autocomplete list
    showSelectedAuthor: function(ui) {

		this.personUriField.val(ui.item.uri);
		this.selectedAuthor.show();

		// Transfer the name from the autocomplete field to the selected author
		// name display, and hide the last name field.
		this.selectedAuthorName.html(this.lastNameField.val());
		// NB For some reason this doesn't delete the value from the last name
		// field when the form is redisplayed. Need to do in initFormView.
		this.hideFields(this.lastNameWrapper);
		
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
    		url += '&filter=' + element;
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
//    				success: function(data) {
//    					cache[request.term] = data;
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
    	var authorUris = $('span.existingAuthorUri'); 
    	return authorUris.map(function() {
    		return $(this).html();
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

/* "value" : uri
 * label: gets displayed in form field = rdfs:label
 * 
 * on select: put the uri into the person uri form field
 */
/*[ { "id": "Somateria mollissima", "label": "Common Eider", "value": "Common Eider" }, 
{ "id": "Crex crex", "label": "Corncrake", "value": "Corncrake" }, 
{ "id": "Grus grus", "label": "Common Crane", "value": "Common Crane" }, 
{ "id": "Charadrius hiaticula", "label": "Common Ringed Plover", "value": "Common Ringed Plover" }, 
{ "id": "Gallinago gallinago", "label": "Common Snipe", "value": "Common Snipe" },
{ "id": "Tringa totanus", "label": "Common Redshank", "value": "Common Redshank" }, 
{ "id": "Sterna hirundo", "label": "Common Tern", "value": "Common Tern" }, 
{ "id": "Alcedo atthis", "label": "Common Kingfisher", "value": "Common Kingfisher" }, 
{ "id": "Corvus corax", "label": "Common Raven", "value": "Common Raven" }, 
{ "id": "Emberiza calandra", "label": "Corn Bunting", "value": "Corn Bunting" }, 
{ "id": "Phalacrocorax carbo", "label": "Great Cormorant", "value": "Great Cormorant" }, 
{ "id": "Tadorna tadorna", "label": "Common Shelduck", "value": "Common Shelduck" } ]*/