/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var addAuthorForm = {

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
    	
    	this.initAuthorDD();
    	
    	if (this.findValidationErrors()) {
    		this.initFormAfterInvalidSubmission();
    	} else {
    		this.initAuthorListOnlyView();
    	}
    },
    
    
    /* *** Set up the various page views *** */
    
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
    
    hideSelectedAuthor: function() {
        this.selectedAuthor.hide();
        this.selectedAuthorName.html('');
        this.personUriField.val('');
    },

    showFieldsForNewPerson: function() {    
        this.firstNameWrapper.show();
        this.middleNameWrapper.show();
        // this.toggleLastNameLabel('Name', 'Last name');
    },

    hideFieldsForNewPerson: function() {   
        this.hideFields(this.firstNameWrapper); 
        this.hideFields(this.middleNameWrapper); 
        // this.toggleLastNameLabel('Last name', 'Name');
    },
        
//    toggleLastNameLabel: function(currentText, newText) {
//        var lastNameLabelText = this.lastNameLabel.html(),
//            newLastNameLabelText = lastNameLabelText.replace(currentText, newText);
//        this.lastNameLabel.html(newLastNameLabelText);  
//    },
    
        
    /* *** Ajax initializations *** */

    /* Autocomplete */
    initAutocomplete: function() {

        // Make cache a property of this so we can access it after removing 
        // an author.
        this.acCache = {};
        this.baseAcUrl = $('.acUrl').attr('id');        
        this.setAcUrl();
        
        $('#lastName').autocomplete({
            minLength: 2,
            source: function(request, response) {
                if (request.term in addAuthorForm.acCache) {
                    // console.log('found term in cache');
                    response(addAuthorForm.acCache[request.term]);
                    return;
                }
                // console.log('not getting term from cache');
                
                // If the url query params are too long, we could do a post
                // here instead of a get. Add the exclude uris to the data
                // rather than to the url.
                $.ajax({
                    url: addAuthorForm.acUrl,
                    dataType: 'json',
                    data: request,
                    complete: function(xhr) {
                        // Not sure why, but we need an explicit json parse here. jQuery
                        // should parse the response text and return an json object.
                        var results = jQuery.parseJSON(xhr.responseText);
                        addAuthorForm.acCache[request.term] = results;  
                        response(results);
                    }

                });
            },
            select: function(event, ui) {
                addAuthorForm.showSelectedAuthor(ui);       
            }
        });

    },

    setAcUrl: function() {
        var url = this.baseAcUrl, 
            existingAuthors = $('#authorships .authorLink'); 
        
        //console.log('in setAcUrl()');
        //console.log('number of existing authors: ' + existingAuthors.length);
        
        existingAuthors.each(function() {
            url += '&excludeUri=' + $(this).attr('id');
        });

        this.acUrl = url;
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
        
    /* Drag-and-drop */
    initAuthorDD: function() {
        
        var authorshipList = $('#authorships'),
            authorships = authorshipList.children();
        
        if (authorships.length < 2) {
            return;
        }
        
        $('.authorLinkWrapper').each(function() {
            $(this).attr('title', 'Drag and drop to reorder authors');
        });
        
        authorshipList.sortable({
            stop: function(event, ui) {
                var predicateUri = '<' + $('.rankPred').attr('id') + '>',
                    rankXsdType = $('.rankXsdType').attr('id'),
                    additions = '',
                    retractions = '',
                    authorships = [];

                $('li.authorship').each(function(index) {
                    var uri = $(this).attr('id'),
                    subjectUri = '<' + uri + '>',
                    oldRankVal = addAuthorForm.getRankVal(this),
                    newRank = index + 1,                            
                    newRankForN3,
                    oldRank,
                    oldRankType,
                    oldRankForN3,
                    rankVals;

                    if (oldRankVal) {
                        // e.g., 1_http://www.w3.org/2001/XMLSchema#int
                        // We handle typeless values formatted as either "1" or "1_".
                        rankVals = oldRankVal.split('_');  
                        oldRank = rankVals[0];
                        oldRankType = rankVals.length > 1 ? rankVals[1] : '';                      
                        oldRankForN3 = addAuthorForm.makeRankDataPropVal(oldRank, oldRankType);                        
                        retractions += subjectUri + ' ' + predicateUri + ' ' + oldRankForN3 + ' .';
                    }

                    newRankForN3 = addAuthorForm.makeRankDataPropVal(newRank, rankXsdType);           
                    additions += subjectUri + ' ' + predicateUri + ' ' + newRankForN3 +  ' .';
                               
                    // This data will be used to modify the page after successful completion
                    // of the Ajax request.
                    authorship = {
                        uri: uri,
                        rankVal: newRank + '_' + rankXsdType
                    };
                    authorships.push(authorship);
                    
                });
        
                // console.log(authorships)
                // console.log('additions: ' + additions);
                // console.log('retractions: ' + retractions);
        
                $.ajax({
                    url: $('.reorderUrl').attr('id'),
                    data: {
                        additions: additions,
                        retractions: retractions
                    },
                    authorships: authorships,
                    processData: 'false',
                    dataType: 'json',
                    type: 'POST',
                    success: function(data, status, request) {
                        $.each(authorships, function(index, obj) {
                            // find the element with this uri as id
                            var el = $('li[id=' + obj.uri + ']'),
                                // because all ranks have been reordered without gaps,
                                // we can get the position from the rank
                                pos = obj.rankVal.split('_')[0];
                            // set the new rank and position for this element 
                            addAuthorForm.setRankVal(el, obj.rankVal);
                            addAuthorForm.setPosition(el, pos);
                        });
                    },
                    error: function(request, status, error) {
                        // Put the moved item back to its original position.
                        // Seems we need to do this by hand. Can't see any way to do it with jQuery UI. ??
                        var pos = addAuthorForm.getPosition(ui.item),
                            //ui.item.children('.position').attr('id'),
                            nextpos = pos + 1,
                            authorships = $('#authorships'),
                            next = authorships.find('.position[id=' + nextpos + ']').parent();
                        
                        if (next.length) {
                            ui.item.insertBefore(next);
                        } else {
                            ui.item.appendTo(authorships);
                        }                                               
                    
                        alert('Reordering of authors failed.');
                    }
                });     
            } // end stop callback
        });     
    },

    getPosition: function(authorship) {
        return parseInt($(authorship).children('.position').attr('id'));
    },
    
    setPosition: function(authorship, pos) {
        $(authorship).children('.position').attr('id', pos);
    },
    
    // Get the authorship rank value, which includes xsd type
    getRankVal: function(authorship) {
        return $(authorship).children('.rank').attr('id');
    },
    
    // Get the integer value from the authorship rank value
    getRank: function(authorship) {
        return this.getRankVal(authorship).split('_')[0];
    },
    
    setRankVal: function(authorship, rank) {
        $(authorship).children('.rank').attr('id', rank);
    },
     
    makeRankDataPropVal: function(rank, xsdType) {
        var rankVal = '"' + rank + '"';
        if (xsdType) {
            rankVal += '^^<' + xsdType + '>'
        }
        return rankVal;
    },
    
               
    /* *** Event listeners *** */ 
   
    bindEventListeners: function() {
    	
    	this.showFormButton.click(function() {
    		addAuthorForm.initFormView();
    		return false;
    	});
    	
    	this.submit.click(function() {
    		// NB Important JavaScript scope issue: if we call it this way, this = addAuthorForm 
    		// in prepareSubmit. If we do this.submit.click(prepareSubmit); then
    		// this != addAuthorForm in prepareSubmit.
    		addAuthorForm.prepareSubmit(); 
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
    		    addAuthorForm.onLastNameChange();
    			return false;
    		}
    	});
    	
    	this.removeAuthorshipLinks.click(function() {
            addAuthorForm.removeAuthorship(this);
            return false;
    	});
    	
//    	this.undoLinks.click(function() {
//    		$.ajax({
//    			url: $(this).attr('href')
//    		});
//    		return false;    		
//    	});
    	
    },

    prepareSubmit: function() {
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
    
    onLastNameChange: function() {
        this.showFieldsForNewPerson();
        this.firstNameField.focus();
        // this.fixNames();
    },
    
    // User may have typed first name as well as last name into last name field.
    // If so, when showing first and middle name fields, move anything after a comma
    // or space into the first name field.
    // RY Space is problematic because they may be entering "<firstname> <lastname>", but
    // comma is a clear case. 
//    fixNames: function() {
//        var lastNameInput = this.lastNameField.val(),
//            names = lastNameInput.split(/[, ]+/), 
//            lastName = names[0];
// 
//        this.lastNameField.val(lastName);
//        
//        if (names.length > 1) {
//            //firstName = names[1].replace(/^[, ]+/, '');
//            this.firstNameField.val(names[1]);
//        } 
//    },
     
    removeAuthorship: function(link) {
        // RY Upgrade this to a modal window
        var message = 'Are you sure you want to remove this author?';
        if (!confirm(message)) {
            return false;
        }
        $.ajax({
            url: $(link).attr('href'),
            type: 'POST', 
            data: {
                deletion: $(link).parents('.authorship').attr('id')
            },
            dataType: 'json',
            context: link, // context for callback
            complete: function(request, status) {
                var authorship = $(this).parents('.authorship'),
                    nextAuthorships = authorship.nextAll(),
                    rank;
//                  author = $(this).siblings('span.author'),
//                  authorLink = author.children('a.authorLink'),
//                  authorName = authorLink.html();
            
                if (status === 'success') {

                    if (nextAuthorships.length) {
                        // Reset the position value of each succeeding authorship
                        nextAuthorships.each(function() {
                            //var pos = parseInt($(this).children('.position').attr('id'));
                            //$(this).children('.position').attr('id', pos-1);
                            var pos = addAuthorForm.getPosition(this);
                            addAuthorForm.setPosition(this, pos-1);                         
                        });
                    } else {
                        // Removed author was last in rank: reset the rank hidden form field
                        rank = addAuthorForm.getRank(authorship); 
                        $('input#rank').val(rank);                          
                    }
                
                    // In future, do this selectively by only clearing terms that match the
                    // deleted author's name
                    addAuthorForm.acCache = {};

                    authorship.fadeOut(400, function() {
                        $(this).remove();
                        // Actions that depend on the author having been removed from the DOM:
                        // If there's just one author remaining, disable drag-drop
                        if ($('.authorship').length == 1) {
                            addAuthorForm.disableAuthorDD();
                        }
                        // Reset the excluded uris in the autocomplete url so that the
                        //author just removed is no longer excluded.
                        addAuthorForm.setAcUrl();
                    });

//                  $(this).hide();
//                  $(this).siblings('.undo').show();
//                  author.html(authorName + ' has been removed');
//                  author.css('width', 'auto');
//                  author.effect('highlight', {}, 3000);
                } else {
                    alert('Error processing request: author not removed');
                }
            }
        });        
    },
    
    // Disable DD and associated cues if only one author remains
    disableAuthorDD: function() {
    	var authorships = $('#authorships'),
            authorship = $('.authorship'),
            authorLinkWrapper = $('.authorLinkWrapper');
            
    	authorships.sortable({ disable: true } );
        authorships.removeClass('dd');
        
    	authorship.css('background', 'none');
    	authorship.css('padding-left', '0');
        
    	authorLinkWrapper.attr('title', '');
    },

    // RY To be implemented later.
    toggleRemoveLink: function() {
    	// when clicking remove: remove the author, and change link text to 'undo'
    	// when clicking undo: add the author back, and change link text to 'remove'
    }

};

$(document).ready(function() {   
    addAuthorForm.onLoad();
});

