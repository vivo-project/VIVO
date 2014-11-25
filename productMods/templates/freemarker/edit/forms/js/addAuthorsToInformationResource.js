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

        // Get the i18n variables from the page
        $.extend(this, i18nStrings);
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
        this.verifyMatch = this.form.find('.verifyMatch');  
        this.personRadio = $('input.person-radio');  
        this.orgRadio = $('input.org-radio');
        this.personSection = $('section#personFields');  
        this.orgSection = $('section#organizationFields');  
        this.orgName = $('input#orgName');
        this.orgNameWrapper = this.orgName.parent();
        this.orgUriField = $('input#orgUri');
        this.selectedOrg = $('div#selectedOrg');
        this.selectedOrgName = $('span#selectedOrgName');
        this.orgLink = $('a#orgLink');
        this.personLink = $('a#personLink');
        this.returnLink = $('a#returnLink');
        
        this.orgSection.hide();
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
        
        this.initElementData();

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
       
        if ($('li.authorship').length) {  // make sure we have at least one author
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
        
        // There's a conflict bewteen the last name fields .blur event and the cancel
        // button's click. So display the middle and first names along with the last name tlw72
        //this.hideFieldsForNewPerson();

        // This shouldn't be needed, because calling this.hideFormFields(this.lastNameWrapper)
        // from showSelectedAuthor should do it. However, it doesn't work from there,
        // or in the cancel action, or if referring to this.lastNameField. None of those work,
        // however.
        $('#lastName').val(''); 
        // Set the initial autocomplete help text in the acSelector field.
        this.addAcHelpText(this.acSelector);
        
        return false; 
        
    },
    
    // Form initialization common to both a 'clean' form view and when
    // returning from an invalid submission.
    initForm: function() {
        
        // Hide the button that shows the form
        this.showFormButtonWrapper.hide(); 

        this.hideSelectedPerson();
        this.hideSelectedOrg();

        this.cancel.unbind('click');
        this.cancel.bind('click', function() {
            addAuthorForm.showAuthorListOnlyView();
            addAuthorForm.setAuthorType("person");
            return false;
        });
        
        // Reset the last name field. It had been hidden if we selected an author from
        // the autocomplete field.
        this.lastNameWrapper.show(); 
        this.showFieldsForNewPerson();        

        // Show the form
        this.form.show();                 
        //this.lastNameField.focus();
    },   
    
    hideSelectedPerson: function() {
        this.selectedAuthor.hide();
        this.selectedAuthorName.html('');
        this.personUriField.val('');
    },

    hideSelectedOrg: function() {
        this.selectedOrg.hide();
        this.selectedOrgName.html('');
        this.orgUriField.val('');
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
        var $acField;
        var urlString;
        var authType;
        
        if  ( this.personRadio.attr("checked") ) {
            $acField = this.lastNameField;
            urlString = addAuthorForm.acUrl + addAuthorForm.personUrl + addAuthorForm.tokenize;
            authType = "person";
        }
        else {
            $acField = this.orgName;
            urlString = addAuthorForm.acUrl + addAuthorForm.orgUrl + addAuthorForm.tokenize;
            authType = "org";
        }  
        $acField.autocomplete({
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
                    url: urlString,
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
                addAuthorForm.showSelectedAuthor(ui,authType); 
            }
        });

    },

    initElementData: function() {   
        this.verifyMatch.data('baseHref', this.verifyMatch.attr('href'));
    },

    setAcFilter: function() {
        this.acFilter = [];
        
        $('li.authorship').each(function() {
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
    showSelectedAuthor: function(ui,authType) {

        if ( authType == "person" ) {
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
            this.personLink.attr('href', this.verifyMatch.data('baseHref') + ui.item.uri);
        }
        else {
            // do the same as above but for the organization fields
            this.orgUriField.val(ui.item.uri); 
            this.selectedOrg.show();

            this.selectedOrgName.html(ui.item.label);

            this.hideFields(this.orgNameWrapper); 

            this.orgLink.attr('href', this.verifyMatch.data('baseHref') + ui.item.uri);
        }

        // Cancel restores initial form view
        this.cancel.unbind('click');
        this.cancel.bind('click', function() {
            addAuthorForm.initFormView();
            addAuthorForm.setAuthorType(authType);
            return false;
        });
    },
        
    /* Drag-and-drop */
    initAuthorDD: function() {
        
        var authorshipList = $('#dragDropList'),
            authorships = authorshipList.children('li');
        
        if (authorships.length < 2) {
            return;
        }
        
        $('.authorNameWrapper').each(function() {
            $(this).attr('title', addAuthorForm.authorNameWrapperTitle);
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
                $('li.authorship').each(function(index){
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
                        authorships = $('#dragDropList'), 
                        next = addAuthorForm.findAuthorship('position', nextpos);
                    
                    if (next.length) {
                        ui.item.insertBefore(next);
                    }
                    else {
                        ui.item.appendTo(authorships);
                    }
                    
                    alert(addAuthorForm.reorderAuthorsAlert);                                 
                }      
            }
        });           
    },
    
    // On page load, associate data with each authorship element. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // authorships.
    initAuthorshipData: function() {
        $('li.authorship').each(function(index) {
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
        
        $('li.authorship').each(function() {
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
        
        this.orgRadio.click(function() {
            addAuthorForm.setAuthorType("org");
        });

        this.personRadio.click(function() {
            addAuthorForm.setAuthorType("person");
        });

        this.form.submit(function() {
            // NB Important JavaScript scope issue: if we call it this way, this = addAuthorForm 
            // in prepareSubmit. If we do this.form.submit(this.prepareSubmit); then
            // this != addAuthorForm in prepareSubmit.
            $selectedObj = addAuthorForm.form.find('input.acSelector');
            addAuthorForm.deleteAcHelpText($selectedObj);
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

        this.personLink.click(function() {
            window.open($(this).attr('href'), 'verifyMatchWindow', 'width=640,height=640,scrollbars=yes,resizable=yes,status=yes,toolbar=no,menubar=no,location=no');
            return false;
        });   

        this.orgLink.click(function() {
            window.open($(this).attr('href'), 'verifyMatchWindow', 'width=640,height=640,scrollbars=yes,resizable=yes,status=yes,toolbar=no,menubar=no,location=no');
            return false;
        });   

    	this.acSelector.focus(function() {
        	addAuthorForm.deleteAcHelpText(this);
    	});   

    	this.acSelector.blur(function() {
        	addAuthorForm.addAcHelpText(this);
    	}); 
                
    	this.orgName.focus(function() {
        	addAuthorForm.deleteAcHelpText(this);
    	});   

    	this.orgName.blur(function() {
        	addAuthorForm.addAcHelpText(this);
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
        
    },

    prepareSubmit: function() {
        var firstName,
            middleName,
            lastName,
            name;
        
        // If selecting an existing person, don't submit name fields
        if (this.personUriField.val() != '' || this.orgUriField.val() != '' || this.orgName.val() != '' ) {
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
		// If user selected org via autocomplete, clear the org name field
		if ( this.orgUriField.val() != '' ) {
			this.orgName.val("");
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

        authorName = $(link).prev().children().text();

        var removeLast = false,
            message = addAuthorForm.removeAuthorshipMessage + '\n\n' + authorName + ' ?\n\n';
        if (!confirm(message)) {
            return false;
        }

        if ( addAuthorForm.showFormButtonWrapper.is(':visible') ) {
            addAuthorForm.returnLink.hide();
            $('img#indicatorOne').removeClass('hidden');
            addAuthorForm.showFormButton.addClass('disabledSubmit');
            addAuthorForm.showFormButton.attr('disabled','disabled');
        }
        else {
            addAuthorForm.cancel.hide();
            $('img#indicatorTwo').removeClass('hidden');            
            addAuthorForm.submit.addClass('disabledSubmit');
            addAuthorForm.submit.attr('disabled','disabled');
        }
              
        if ($(link)[0] === $('.remove:last')[0]) {
            removeLast = true;
        } 
        
        $.ajax({
            url: $(link).attr('href'),
            type: 'POST', 
            data: {
                deletion: $(link).parents('li.authorship').data('authorshipUri')
            },
            dataType: 'json',
            context: link, // context for callback
            complete: function(request, status) {
                var authorship,
                    authorUri;
            
                if (status === 'success') {
                    
                    authorship = $(this).parents('li.authorship');
                
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
                        numAuthors = $('li.authorship').length; // retrieve the length after removing authorship from the DOM
                        
                        // If removed item not last, reorder to remove any gaps
                        if (numAuthors > 0 && ! removeLast) {
                            addAuthorForm.reorderAuthors();
                        }
                            
                        // If fewer than two authors remaining, disable drag-drop
                        if (numAuthors < 2) {
                            addAuthorForm.disableAuthorDD();
                        }                           

                        if ( $('img#indicatorOne').is(':visible') ) {
                            $('img#indicatorOne').fadeOut(100, function() {
                                $(this).addClass('hidden');
                            });

                            addAuthorForm.returnLink.fadeIn(100, function() {
                                $(this).show();
                            });
                            addAuthorForm.showFormButton.removeClass('disabledSubmit');
                            addAuthorForm.showFormButton.attr('disabled','');
                        }
                        else {
                            $('img#indicatorTwo').fadeOut(100, function() {
                                 $(this).addClass('hidden');
                             });

                             addAuthorForm.cancel.fadeIn(100, function() {
                                 $(this).show();
                             });
                             addAuthorForm.submit.removeClass('disabledSubmit');
                             addAuthorForm.submit.attr('disabled','');
                        }
                    });

                } else {
                    alert(addAuthorForm.removeAuthorshipAlert);
                    
                }
            }
        });        
    },
    
    // Disable DD and associated cues if only one author remains
    disableAuthorDD: function() {
        var authorships = $('#dragDropList'),
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
	addAcHelpText: function(selectedObj) {
        var typeText;
        if ( $(selectedObj).attr('id') == "lastName" ) {
            typeText = addAuthorForm.authorTypeText;
        }
        else {
            typeText = addAuthorForm.organizationTypeText;
        }
        
        if (!$(selectedObj).val()) {
			$(selectedObj).val(addAuthorForm.helpTextSelect + " " + typeText + " " + addAuthorForm.helpTextAdd)
						   .addClass(this.acHelpTextClass);
		}
	},
	
	deleteAcHelpText: function(selectedObj) {
	    if ($(selectedObj).hasClass(this.acHelpTextClass)) {
	            $(selectedObj).val('')
	                          .removeClass(this.acHelpTextClass);
	    }
	},

    // Depending on whether the author is a person or an organization,
    // we need to set the correct class names for fields like the acSelector, acSelection, etc.
    // as well as clear and disable fields, call other functions ...
	setAuthorType: function(authType) {
	    if ( authType == "org" ) {
	        this.personSection.hide();
	        this.orgSection.show();
			this.orgNameWrapper.show();
	        // person fields
            this.personRadio.attr('checked', false);  // needed for reset when cancel button is clicked
	        this.acSelector.removeClass("acSelector");
	        this.acSelector.removeClass(this.acHelpTextClass);
	        this.selectedAuthor.removeClass("acSelection");
	        this.selectedAuthorName.removeClass("acSelectionInfo");
	        this.personLink.removeClass("verifyMatch");
	        this.acSelector.attr('disabled', 'disabled');
	        this.firstNameField.attr('disabled', 'disabled');
	        this.middleNameField.attr('disabled', 'disabled');
	        this.lastNameField.attr('disabled', 'disabled');
	        this.acSelector.val('');
	        this.firstNameField.val('');
	        this.middleNameField.val('');
	        this.lastNameField.val('');
	        // org fields
	        this.orgRadio.attr('checked', true); // needed for reset when cancel button is clicked
	        this.orgName.addClass("acSelector");
	        this.selectedOrg.addClass("acSelection");
	        this.selectedOrgName.addClass("acSelectionInfo");
	        this.orgLink.addClass("verifyMatch");
	        this.orgName.attr('disabled', '');
	        this.orgUriField.attr('disabled', '');

	        addAuthorForm.addAcHelpText(this.orgName);
	        addAuthorForm.initAutocomplete();
	        addAuthorForm.hideSelectedPerson();
	    }
	    else if ( authType == "person" ) {
	        this.orgSection.hide();
	        this.personSection.show();
	        // org fields
	        this.orgRadio.attr('checked', false);  // needed for reset when cancel button is clicked
	        this.orgName.removeClass("acSelector");
	        this.orgName.removeClass(this.acHelpTextClass);
	        this.selectedOrg.removeClass("acSelection");
	        this.selectedOrgName.removeClass("acSelectionInfo");
	        this.orgLink.removeClass("verifyMatch");
	        this.orgName.attr('disabled', 'disabled');
	        this.orgUriField.attr('disabled', 'disabled');
	        this.orgName.val('');
	        this.orgUriField.val('');
            // person fields
            this.acSelector.addClass("acSelector");
            this.personRadio.attr('checked', true);  // needed for reset when cancel button is clicked
	        this.selectedAuthor.addClass("acSelection");
	        this.selectedAuthorName.addClass("acSelectionInfo");
	        this.personLink.addClass("verifyMatch");
	        this.acSelector.attr('disabled', '');
	        this.firstNameField.attr('disabled', '');
	        this.middleNameField.attr('disabled', '');
	        this.lastNameField.attr('disabled', '');

	        addAuthorForm.addAcHelpText(this.acSelector);
	        addAuthorForm.initAutocomplete();
	        addAuthorForm.hideSelectedOrg();
	        
	    }
    }
};

$(document).ready(function() {   
    addAuthorForm.onLoad();
}); 
