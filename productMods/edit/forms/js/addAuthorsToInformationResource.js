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
    	this.removeAuthorshipLinks = $('a.remove');
    	//this.undoLinks = $('a.undo');
    	this.submit = this.form.find(':submit');
        this.cancel = this.form.find('.cancel'); 
        this.labelField = $('#label');
        this.firstNameField = $('#firstName');
        this.middleNameField = $('#middleName');
        this.lastNameField = $('#lastName');
        this.lastNameLabel = $('label[for=lastName]');
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
    	this.removeAuthorshipLinks.show();
    	
    	//this.undoLinks.hide();
    	
    	this.bindEventListeners();
    	
    	this.initAutocomplete();
    	
    	this.initAuthorReordering();
    	
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
    	
    	// When hitting enter in last name field, if not an autocomplete
    	// selection, show first and middle name fields.
    	this.lastNameField.keydown(function(event) {
    		if (event.keyCode === 13) {
    			console.log('in keydown')
    		    addAuthorForm.onLastNameChange();
    			return false;
    		}
    	});
    	
    	this.removeAuthorshipLinks.click(function() {
    		console.log($(this).parents('.authorship'));
    		// RY Upgrade this to a modal window
    		var message = "Are you sure you want to remove this author?";
    		if (!confirm(message)) {
    			return false;
    		}
    		$.ajax({
    			url: $(this).attr('href'),
    			type: 'POST', 
    			data: {
    				deletion: $(this).parents('.authorship').attr('id')
    			},
    			dataType: 'json',
    			context: $(this), // context for callback
    			complete: function(request, status) {
    				var authorship = $(this).parents('.authorship');
//    				var author = $(this).siblings('span.author');
//    				var authorLink = author.children('a.authorLink');
//    				var authorName = authorLink.html();
    				if (status === 'success') {
    					authorship.fadeOut(400, function() {
    						$(this).remove();
    					});
//    					$(this).hide();
//    					$(this).siblings('.undo').show();
//    					author.html(authorName + ' has been removed');
//    					author.css('width', 'auto');
//    					author.effect("highlight", {}, 3000);
    				} else {
    					alert('Error processing request');
    				}
    			}
    		});
    		return false;
    	});
    	
//    	this.undoLinks.click(function() {
//    		$.ajax({
//    			url: $(this).attr('href')
//    		});
//    		return false;    		
//    	});
    	
    },
    
    onLastNameChange: function() {
    	this.showFieldsForNewPerson();
    	this.firstNameField.focus();
    	this.fixNames();
    },
    
    showFieldsForNewPerson: function() { 	
    	this.firstNameWrapper.show();
    	this.middleNameWrapper.show();
    	this.toggleLastNameLabel('Name', 'Last name');
    },
    
    // User may have typed first name as well as last name into last name field.
    // If so, when showing first and middle name fields, move anything after a comma
    // into the first name field.
    fixNames: function() {
    	var lastNameInput = this.lastNameField.val(),
    	    names = lastNameInput.split(','), 
    	    lastName = names[0].replace(/[, ]+$/, ''),
    	    firstName;
 
    	this.lastNameField.val(lastName);
    	
    	if (names.length > 1) {
    		firstName = names[1].replace(/^[, ]+/, '');
        	this.firstNameField.val(firstName);
    	} 
    },
    
    hideFieldsForNewPerson: function() {   
    	this.hideFields(this.firstNameWrapper); 
    	this.hideFields(this.middleNameWrapper); 
    	this.toggleLastNameLabel('Last name', 'Name');
    },
    
    toggleLastNameLabel: function(currentText, newText) {
    	var lastNameLabelText = this.lastNameLabel.html(),
    		newLastNameLabelText = lastNameLabelText.replace(currentText, newText);
    	this.lastNameLabel.html(newLastNameLabelText);	
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
		
		return false;
    },
    
    hideSelectedAuthor: function() {
    	this.selectedAuthor.hide();
    	this.selectedAuthorName.html('');
    	this.personUriField.val('');
    },
    
    initAuthorReordering: function() {
    	$('#authors').sortable({
    		update: function() {
    			addAuthorForm.resetRankings();
    		}
    	});   	
    },
    
    resetRankings: function() {
    	var rankPred = '<' + $('#rankPred').val() + '>',
    	    additions = '',
    	    retractions = '',
    	    rankTypeSuffix = '^^<' + $('#rankType').val() + '>', 
    	    uri,
    	    newRank,
    	    oldRank;
    	$('li.authorship').each(function(index) {
    		// This value does double duty, for removal and reordering
    		uri = $(this).children('.remove').attr('id');
    		oldRank = $(this).children('.rank').attr('id'); // already contains the rankSuffix, if present
    		newRank = index + 1;
    		additions += uri + ' ' + rankPred + ' ' + '"' + newRank + '"' + rankTypeSuffix + ' .';
    		retractions += uri + ' ' + rankPred + ' ' + '"' + oldRank + ' .';
    	});
    	console.log(additions);
    	console.log(retractions);
    	$.ajax({
    		url: $('#reorderUrl').val(),
    		data: {
    			additions: additions,
    			retractions: retractions
    		},
    		dataType: 'json',
    		type: 'POST',
    		success: function(xhr, status, error) {
    			// reset rank in the span
    			// can just do from values we computed, if easier than getting data back from server
    			// 
    		},
    		error: function(data, status, request) {
    			addAuthorForm.restorePreviousRankings();
    			alert('Reordering of author ranks failed.');
    		}
    	});    	
    },
    
    restorePreviousRankings: function() {
    	// restore existing rankings after reordering failure
    	// use span.rank id attr value to determine
    },
    
    initAutocomplete: function() {

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

    	var existingAuthors = $('#authors li'); 
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

