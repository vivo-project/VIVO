/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var addAuthorForm = {

    /* *** Initial page setup *** */
   
    onLoad: function() {
        
        if (this.disableFormInUnsupportedBrowsers()) {
            return;
        }        
        this.mixIn();
        this.initObjects();                 
        this.initPage();       
    },

    disableFormInUnsupportedBrowsers: function() {       
        var disableWrapper = $('#ie67DisableWrapper');
        
        // Check for unsupported browsers only if the element exists on the page
        if (disableWrapper.length) {
            if (vitro.browserUtils.isIELessThan8()) {
                disableWrapper.show();
                $('.noIE67').hide();
                return true;
            }
        }            
        return false;      
    },
        
    mixIn: function() {
        // Mix in the custom form utility methods
        $.extend(this, vitro.customFormUtils);
        
        // Get the custom form data from the page
        $.extend(this, customFormData);
    },
    
    // On page load, create references for easy access to form elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function() {
        
        this.form = $('#addAuthorForm');
        this.showFormButtonWrapper = $('#showAddForm');
        this.showFormButton = $('#showAddFormButton');
        this.removeAuthorshipLinks = $('a.remove');
        //this.undoLinks = $('a.undo');
        this.submit = this.form.find(':submit');
        this.cancel = this.form.find('.cancel'); 
        this.acSelector = this.form.find('.acSelector');
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
        this.acHelpTextClass = 'acSelectorWithHelpText';

    },
    
    // Initial page setup. Called only at page load.
    initPage: function() {

        this.initAuthorshipData();
            
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
   
   // This initialization is done only on page load, not when returning to author list only view 
   // after hitting 'cancel.'
   initAuthorListOnlyView: function() {
       
        if ($('.authorship').length) {  // make sure we have at least one author
            // Reorder authors on page load so that previously unranked authors get a rank. Otherwise,
            // when we add a new author, it will get put ahead of any previously unranked authors, instead
            // of at the end of the list. (It is also helpful to normalize the data before we get started.)            
            this.reorderAuthors();
        }        
        this.showAuthorListOnlyView();       
   },
    
    // This view shows the list of existing authors and hides the form.
    // There is a button to show the form. We do this on page load, and after
    // hitting 'cancel' from full view.
    showAuthorListOnlyView: function() {
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
        // Set the initial autocomplete help text in the acSelector field.
        this.addAcHelpText();
        
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
            addAuthorForm.showAuthorListOnlyView();
            return false;
        });
        
        // Reset the last name field. It had been hidden if we selected an author from
        // the autocomplete field.
        this.lastNameWrapper.show();         

        // Show the form
        this.form.show();                 
        //this.lastNameField.focus();
    },   
    
    hideSelectedAuthor: function() {
        this.selectedAuthor.hide();
        this.selectedAuthorName.html('');
        this.personUriField.val('');
    },

    showFieldsForNewPerson: function() {    
        this.firstNameWrapper.show();
        this.middleNameWrapper.show();
    },

    hideFieldsForNewPerson: function() {   
        this.hideFields(this.firstNameWrapper); 
        this.hideFields(this.middleNameWrapper); 
    },
        
    /* *** Ajax initializations *** */

    /* Autocomplete */
    initAutocomplete: function() {

        // Make cache a property of this so we can access it after removing 
        // an author.
        this.acCache = {};  
        this.setAcFilter();    
        
        this.lastNameField.autocomplete({
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
                    data: {
                        term: request.term
                    }, 
                    complete: function(xhr, status) {
                        // Not sure why, but we need an explicit json parse here. jQuery
                        // should parse the response text and return a json object.
                        var results = jQuery.parseJSON(xhr.responseText),
                            filteredResults = addAuthorForm.filterAcResults(results);
                        addAuthorForm.acCache[request.term] = filteredResults;  
                        response(filteredResults);
                    }

                });
            },
            // Select event not triggered in IE6/7 when selecting with enter key rather
            // than mouse. Thus form is disabled in these browsers.
            // jQuery UI bug: when scrolling through the ac suggestions with up/down arrow
            // keys, the input element gets filled with the highlighted text, even though no
            // select event has been triggered. To trigger a select, the user must hit enter
            // or click on the selection with the mouse. This appears to confuse some users.
            select: function(event, ui) {
                addAuthorForm.showSelectedAuthor(ui); 
            }
        });

    },

    setAcFilter: function() {
        this.acFilter = [];
        
        $('.authorship').each(function() {
            var uri = $(this).data('authorUri');
            addAuthorForm.acFilter.push(uri);
         });
    },
    
    removeAuthorFromAcFilter: function(author) {
        var index = $.inArray(author, this.acFilter);
        if (index > -1) { // this should always be true
            this.acFilter.splice(index, 1);
        }   
    },
    
    filterAcResults: function(results) {
        var filteredResults = [];
        if (!this.acFilter.length) {
            return results;
        }
        $.each(results, function() {
            if ($.inArray(this.uri, addAuthorForm.acFilter) == -1) {
                // console.log("adding " + this.label + " to filtered results");
                filteredResults.push(this);
            }
            else {
                // console.log("filtering out " + this.label);
            }
        });
        return filteredResults;
    },
    
    // After removing an authorship, selectively clear matching autocomplete
    // cache entries, else the associated author will not be included in 
    // subsequent autocomplete suggestions.
    clearAcCacheEntries: function(name) {
        name = name.toLowerCase();
        $.each(this.acCache, function(key, value) {
            if (name.indexOf(key) == 0) {
                delete addAuthorForm.acCache[key];
            }
        });
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
        // These get displayed if the selection was made through an enter keystroke,
        // since the keydown event on the last name field is also triggered (and
        // executes first). So re-hide them here.
        this.hideFieldsForNewPerson(); 
        
        // Cancel restores initial form view
        this.cancel.unbind('click');
        this.cancel.bind('click', function() {
            addAuthorForm.initFormView();
            return false;
        });
    },
        
    /* Drag-and-drop */
    initAuthorDD: function() {
        
        var authorshipList = $('#authorships'),
            authorships = authorshipList.children('li');
        
        if (authorships.length < 2) {
            return;
        }
        
        $('.authorNameWrapper').each(function() {
            $(this).attr('title', 'Drag and drop to reorder authors');
        });
        
        authorshipList.sortable({
            cursor: 'move',
            update: function(event, ui) {
                addAuthorForm.reorderAuthors(event, ui);
            }
        });     
    },
    
    // Reorder authors. Called on page load and after author drag-and-drop and remove.
    // Event and ui parameters are defined only in the case of drag-and-drop.
    reorderAuthors: function(event, ui) {
        var authorships = $('li.authorship').map(function(index, el) {
            return $(this).data('authorshipUri');
        }).get();

        $.ajax({
            url: addAuthorForm.reorderUrl,
            data: {
                predicate: addAuthorForm.rankPredicate,
                individuals: authorships
            },
            traditional: true, // serialize the array of individuals for the server
            dataType: 'json',
            type: 'POST',
            success: function(data, status, request) {
                var pos;
                $('.authorship').each(function(index){
                    pos = index + 1;
                    // Set the new position for this element. The only function of this value 
                    // is so we can reset an element to its original position in case reordering fails.
                    addAuthorForm.setPosition(this, pos);                
                });
                // Set the form rank field value.
                $('#rank').val(pos + 1);        
            },
            error: function(request, status, error) {
                // ui is undefined on page load and after an authorship removal.
                if (ui) {
                    // Put the moved item back to its original position.
                    // Seems we need to do this by hand. Can't see any way to do it with jQuery UI. ??
                    var pos = addAuthorForm.getPosition(ui.item),                       
                        nextpos = pos + 1, 
                        authorships = $('#authorships'), 
                        next = addAuthorForm.findAuthorship('position', nextpos);
                    
                    if (next.length) {
                        ui.item.insertBefore(next);
                    }
                    else {
                        ui.item.appendTo(authorships);
                    }
                    
                    alert('Reordering of authors failed.');                                 
                }      
            }
        });           
    },
    
    // On page load, associate data with each authorship element. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // authorships.
    initAuthorshipData: function() {
        $('.authorship').each(function(index) {
            $(this).data(authorshipData[index]);    
            
            // RY We might still need position to put back an element after reordering
            // failure. Rank might already have been reset? Check.
            // We also may need position to implement undo links: we want the removed authorship
            // to show up in the list, but it has no rank.
            $(this).data('position', index+1);      
        });
    },

    getPosition: function(authorship) {
        return $(authorship).data('position');
    },
    
    setPosition: function(authorship, pos) {
        $(authorship).data('position', pos);
    },
    
    findAuthorship: function(key, value) {
        var matchingAuthorship = $(); // if we don't find one, return an empty jQuery set
        
        $('.authorship').each(function() {
            var authorship = $(this);
            if ( authorship.data(key) === value ) {
                matchingAuthorship = authorship; 
                return false; // stop the loop
            }
        });
         
        return matchingAuthorship;       
    },
    
               
    /* *** Event listeners *** */ 
   
    bindEventListeners: function() {
        
        this.showFormButton.click(function() {
            addAuthorForm.initFormView();
            return false;
        });
        
        this.form.submit(function() {
            // NB Important JavaScript scope issue: if we call it this way, this = addAuthorForm 
            // in prepareSubmit. If we do this.form.submit(this.prepareSubmit); then
            // this != addAuthorForm in prepareSubmit.
            addAuthorForm.deleteAcHelpText();
			addAuthorForm.prepareSubmit(); 
        });     

        this.lastNameField.blur(function() {
            // Cases where this event should be ignored:
            // 1. personUri field has a value: the autocomplete select event has already fired.
            // 2. The last name field is empty (especially since the field has focus when the form is displayed).
            // 3. Autocomplete suggestions are showing.
            if ( addAuthorForm.personUriField.val() || !$(this).val() || $('ul.ui-autocomplete li.ui-menu-item').length ) {
                return;
            }
            addAuthorForm.onLastNameChange();
        });

    	this.acSelector.focus(function() {
        	addAuthorForm.deleteAcHelpText();
    	});   

    	this.acSelector.blur(function() {
        	addAuthorForm.addAcHelpText();
    	}); 
                
        // When hitting enter in last name field, show first and middle name fields.
        // NB This event fires when selecting an autocomplete suggestion with the enter
        // key. Since it fires first, we undo its effects in the ac select event listener.
        this.lastNameField.keydown(function(event) {
            if (event.which === 13) {
                addAuthorForm.onLastNameChange();
                return false; // don't submit form
            }
        });
        
        this.removeAuthorshipLinks.click(function() {
            addAuthorForm.removeAuthorship(this);
            return false;
        });
        
//      this.undoLinks.click(function() {
//          $.ajax({
//              url: $(this).attr('href')
//          });
//          return false;           
//      });
        
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
        var removeLast = false,
            message = 'Are you sure you want to remove this author?';
            
        if (!confirm(message)) {
            return false;
        }
        
        if ($(link)[0] === $('.remove:last')[0]) {
            removeLast = true;
        } 
        
        $.ajax({
            url: $(link).attr('href'),
            type: 'POST', 
            data: {
                deletion: $(link).parents('.authorship').data('authorshipUri')
            },
            dataType: 'json',
            context: link, // context for callback
            complete: function(request, status) {
                var authorship,
                    authorUri;
            
                if (status === 'success') {
                    
                    authorship = $(this).parents('.authorship');
                
                    // Clear autocomplete cache entries matching this author's name, else
                    // autocomplete will be retrieved from the cache, which excludes the removed author.
                    addAuthorForm.clearAcCacheEntries(authorship.data('authorName'));
                    
                    // Remove this author from the acFilter so it is included in autocomplete
                    // results again.
                    addAuthorForm.removeAuthorFromAcFilter(authorship.data('authorUri'));
                    
                    authorship.fadeOut(400, function() {
                        var numAuthors;
 
                        // For undo link: add to a deletedAuthorships array
                        
                        // Remove from the DOM                       
                        $(this).remove();
                        
                        // Actions that depend on the author having been removed from the DOM:
                        numAuthors = $('.authorship').length; // retrieve the length after removing authorship from the DOM
                        
                        // If removed item not last, reorder to remove any gaps
                        if (numAuthors > 0 && ! removeLast) {
                            addAuthorForm.reorderAuthors();
                        }
                            
                        // If fewer than two authors remaining, disable drag-drop
                        if (numAuthors < 2) {
                            addAuthorForm.disableAuthorDD();
                        }                           
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
            authorNameWrapper = $('.authorNameWrapper');
            
        authorships.sortable({ disable: true } );
        
        // Use class dd rather than jQuery UI's class ui-sortable, so that we can remove
        // the class if there's fewer than one author. We don't want to remove the ui-sortable
        // class, in case we want to re-enable DD without a page reload (e.g., if implementing
        // adding an author via Ajax request). 
        authorships.removeClass('dd');
              
        authorNameWrapper.removeAttr('title');
    },

    // RY To be implemented later.
    toggleRemoveLink: function() {
        // when clicking remove: remove the author, and change link text to 'undo'
        // when clicking undo: add the author back, and change link text to 'remove'
    },

	// Set the initial help text in the lastName field and change the class name.
	addAcHelpText: function() {
        var typeText;

        if (!this.acSelector.val()) {
			this.acSelector.val("Select an existing Author or add a new one.")
						   .addClass(this.acHelpTextClass);
		}
	},
	
	deleteAcHelpText: function() {
	    if (this.acSelector.hasClass(this.acHelpTextClass)) {
	            this.acSelector.val('')
	                           .removeClass(this.acHelpTextClass);
	        }
	    }
};

$(document).ready(function() {   
    addAuthorForm.onLoad();
}); 
