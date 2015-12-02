/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var addEditorForm = {

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
        
        this.form = $('#addEditorForm');
        this.showFormButtonWrapper = $('#showAddForm');
        this.showFormButton = $('#showAddFormButton');
        this.removeEditorshipLinks = $('a.remove');
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
        this.selectedEditor = $('#selectedEditor');
        this.selectedEditorName = $('#selectedEditorName');
        this.acHelpTextClass = 'acSelectorWithHelpText';
        this.verifyMatch = this.form.find('.verifyMatch');  
        this.personSection = $('section#personFields');  
        this.personLink = $('a#personLink');
        this.returnLink = $('a#returnLink');
    },
    
    // Initial page setup. Called only at page load.
    initPage: function() {

        this.initEditorshipData();
            
        // Show elements hidden by CSS for the non-JavaScript-enabled version.
        // NB The non-JavaScript version of this form is currently not functional.
        this.removeEditorshipLinks.show();
        
        //this.undoLinks.hide();
        
        this.bindEventListeners();
        
        this.initAutocomplete();
        
        this.initElementData();

        this.initEditorDD();
        
        if (this.findValidationErrors()) {
            this.initFormAfterInvalidSubmission();
        } else {
            this.initEditorListOnlyView();
        }
    },
    
    
    /* *** Set up the various page views *** */
   
   // This initialization is done only on page load, not when returning to editor list only view 
   // after hitting 'cancel.'
   initEditorListOnlyView: function() {
       
        if ($('.editorship').length) {  // make sure we have at least one editor
            // Reorder editors on page load so that previously unranked editors get a rank. Otherwise,
            // when we add a new editor, it will get put ahead of any previously unranked editors, instead
            // of at the end of the list. (It is also helpful to normalize the data before we get started.)            
            this.reorderEditors();
        }        
        this.showEditorListOnlyView();       
   },
    
    // This view shows the list of existing editors and hides the form.
    // There is a button to show the form. We do this on page load, and after
    // hitting 'cancel' from full view.
    showEditorListOnlyView: function() {
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

    // Initial view of add editor form. We get here by clicking the show form button,
    // or by cancelling out of an autocomplete selection.
    initFormView: function() {
        
        this.initForm();
        
        // There's a conflict bewteen the last name fields .blur event and the cancel
        // button's click. So display the middle and first names along with the last name tlw72
        //this.hideFieldsForNewPerson();

        // This shouldn't be needed, because calling this.hideFormFields(this.lastNameWrapper)
        // from showSelectedEditor should do it. However, it doesn't work from there,
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

        this.cancel.unbind('click');
        this.cancel.bind('click', function() {
            addEditorForm.showEditorListOnlyView();
            addEditorForm.setEditorType("person");
            return false;
        });
        
        // Reset the last name field. It had been hidden if we selected an editor from
        // the autocomplete field.
        this.lastNameWrapper.show(); 
        this.showFieldsForNewPerson();        

        // Show the form
        this.form.show();                 
        //this.lastNameField.focus();
    },   
    
    hideSelectedPerson: function() {
        this.selectedEditor.hide();
        this.selectedEditorName.html('');
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
        // an editor.
        this.acCache = {};  
        this.setAcFilter();
        var $acField = this.lastNameField;
        var urlString = addEditorForm.acUrl + addEditorForm.personUrl + addEditorForm.tokenize;
        var authType = "person";
        
        $acField.autocomplete({
            minLength: 2,
            source: function(request, response) {
                if (request.term in addEditorForm.acCache) {
                    // console.log('found term in cache');
                    response(addEditorForm.acCache[request.term]);
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
                            filteredResults = addEditorForm.filterAcResults(results);
                        addEditorForm.acCache[request.term] = filteredResults;  
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
                addEditorForm.showSelectedEditor(ui,authType); 
            }
        });

    },

    initElementData: function() {   
        this.verifyMatch.data('baseHref', this.verifyMatch.attr('href'));
    },

    setAcFilter: function() {
        this.acFilter = [];
        
        $('.editorship').each(function() {
            var uri = $(this).data('editorUri');
            addEditorForm.acFilter.push(uri);
         });
    },
    
    removeEditorFromAcFilter: function(editor) {
        var index = $.inArray(editor, this.acFilter);
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
            if ($.inArray(this.uri, addEditorForm.acFilter) == -1) {
                // console.log("adding " + this.label + " to filtered results");
                filteredResults.push(this);
            }
            else {
                // console.log("filtering out " + this.label);
            }
        });
        return filteredResults;
    },
    
    // After removing an editorship, selectively clear matching autocomplete
    // cache entries, else the associated editor will not be included in 
    // subsequent autocomplete suggestions.
    clearAcCacheEntries: function(name) {
        name = name.toLowerCase();
        $.each(this.acCache, function(key, value) {
            if (name.indexOf(key) == 0) {
                delete addEditorForm.acCache[key];
            }
        });
    },
    
    // Action taken after selecting an editor from the autocomplete list
    showSelectedEditor: function(ui,authType) {

        if ( authType == "person" ) {
            this.personUriField.val(ui.item.uri);
            this.selectedEditor.show();

            // Transfer the name from the autocomplete to the selected editor
            // name display, and hide the last name field.
            this.selectedEditorName.html(ui.item.label);
            // NB For some reason this doesn't delete the value from the last name
            // field when the form is redisplayed. Thus it's done explicitly in initFormView.
            this.hideFields(this.lastNameWrapper);
            // These get displayed if the selection was made through an enter keystroke,
            // since the keydown event on the last name field is also triggered (and
            // executes first). So re-hide them here.
            this.hideFieldsForNewPerson(); 
            this.personLink.attr('href', this.verifyMatch.data('baseHref') + ui.item.uri);
        }

        // Cancel restores initial form view
        this.cancel.unbind('click');
        this.cancel.bind('click', function() {
            addEditorForm.initFormView();
            addEditorForm.setEditorType(authType);
            return false;
        });
    },
        
    /* Drag-and-drop */
    initEditorDD: function() {
        
        var editorshipList = $('#dragDropList'),
            editorships = editorshipList.children('li');
        
        if (editorships.length < 2) {
            return;
        }
        
        $('.editorNameWrapper').each(function() {
            $(this).attr('title', addEditorForm.editorNameWrapperTitle);
        });
        
        editorshipList.sortable({
            cursor: 'move',
            update: function(event, ui) {
                addEditorForm.reorderEditors(event, ui);
            }
        });     
    },
    
    // Reorder editors. Called on page load and after editor drag-and-drop and remove.
    // Event and ui parameters are defined only in the case of drag-and-drop.
    reorderEditors: function(event, ui) {
        var editorships = $('li.editorship').map(function(index, el) {
            return $(this).data('editorshipUri');
        }).get();

        $.ajax({
            url: addEditorForm.reorderUrl,
            data: {
                predicate: addEditorForm.rankPredicate,
                individuals: editorships
            },
            traditional: true, // serialize the array of individuals for the server
            dataType: 'json',
            type: 'POST',
            success: function(data, status, request) {
                var pos;
                $('.editorship').each(function(index){
                    pos = index + 1;
                    // Set the new position for this element. The only function of this value 
                    // is so we can reset an element to its original position in case reordering fails.
                    addEditorForm.setPosition(this, pos);                
                });
                // Set the form rank field value.
                $('#rank').val(pos + 1);        
            },
            error: function(request, status, error) {
                // ui is undefined on page load and after an editorship removal.
                if (ui) {
                    // Put the moved item back to its original position.
                    // Seems we need to do this by hand. Can't see any way to do it with jQuery UI. ??
                    var pos = addEditorForm.getPosition(ui.item),                       
                        nextpos = pos + 1, 
                        editorships = $('#dragDropList'), 
                        next = addEditorForm.findEditorship('position', nextpos);
                    
                    if (next.length) {
                        ui.item.insertBefore(next);
                    }
                    else {
                        ui.item.appendTo(editorships);
                    }
                    
                    alert(addEditorForm.reorderEditorsAlert);                                 
                }      
            }
        });           
    },
    
    // On page load, associate data with each editorship element. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // editorships.
    initEditorshipData: function() {
        $('.editorship').each(function(index) {
            $(this).data(editorshipData[index]);    
            
            // RY We might still need position to put back an element after reordering
            // failure. Rank might already have been reset? Check.
            // We also may need position to implement undo links: we want the removed editorship
            // to show up in the list, but it has no rank.
            $(this).data('position', index+1);      
        });
    },

    getPosition: function(editorship) {
        return $(editorship).data('position');
    },
    
    setPosition: function(editorship, pos) {
        $(editorship).data('position', pos);
    },
    
    findEditorship: function(key, value) {
        var matchingEditorship = $(); // if we don't find one, return an empty jQuery set
        
        $('.editorship').each(function() {
            var editorship = $(this);
            if ( editorship.data(key) === value ) {
                matchingEditorship = editorship; 
                return false; // stop the loop
            }
        });
         
        return matchingEditorship;       
    },
    
               
    /* *** Event listeners *** */ 
   
    bindEventListeners: function() {
        
        this.showFormButton.click(function() {
            addEditorForm.initFormView();
            return false;
        });
        
        this.form.submit(function() {
            // NB Important JavaScript scope issue: if we call it this way, this = addEditorForm 
            // in prepareSubmit. If we do this.form.submit(this.prepareSubmit); then
            // this != addEditorForm in prepareSubmit.
            $selectedObj = addEditorForm.form.find('input.acSelector');
            addEditorForm.deleteAcHelpText($selectedObj);
			addEditorForm.prepareSubmit(); 
        });     

        this.lastNameField.blur(function() {
            // Cases where this event should be ignored:
            // 1. personUri field has a value: the autocomplete select event has already fired.
            // 2. The last name field is empty (especially since the field has focus when the form is displayed).
            // 3. Autocomplete suggestions are showing.
            if ( addEditorForm.personUriField.val() || !$(this).val() || $('ul.ui-autocomplete li.ui-menu-item').length ) {
                return;
            }
            addEditorForm.onLastNameChange();
        });

        this.personLink.click(function() {
            window.open($(this).attr('href'), 'verifyMatchWindow', 'width=640,height=640,scrollbars=yes,resizable=yes,status=yes,toolbar=no,menubar=no,location=no');
            return false;
        });   

    	this.acSelector.focus(function() {
        	addEditorForm.deleteAcHelpText(this);
    	});   

    	this.acSelector.blur(function() {
        	addEditorForm.addAcHelpText(this);
    	}); 
                                
        // When hitting enter in last name field, show first and middle name fields.
        // NB This event fires when selecting an autocomplete suggestion with the enter
        // key. Since it fires first, we undo its effects in the ac select event listener.
        this.lastNameField.keydown(function(event) {
            if (event.which === 13) {
                addEditorForm.onLastNameChange();
                return false; // don't submit form
            }
        });
        
        this.removeEditorshipLinks.click(function() {
            addEditorForm.removeEditorship(this);
            return false;
        });
        
    },

    prepareSubmit: function() {
        var firstName,
            middleName,
            lastName,
            name;
        
        // If selecting an existing person, don't submit name fields
        if (this.personUriField.val() != '' ) {
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
         
    removeEditorship: function(link) {
        // RY Upgrade this to a modal window

        editorName = $(link).prev().children().text();

        var removeLast = false,
            message = addEditorForm.removeEditorshipMessage + '\n\n' + editorName + ' ?\n\n';
        if (!confirm(message)) {
            return false;
        }

        if ( addEditorForm.showFormButtonWrapper.is(':visible') ) {
            addEditorForm.returnLink.hide();
            $('img#indicatorOne').removeClass('hidden');
            addEditorForm.showFormButton.addClass('disabledSubmit');
            addEditorForm.showFormButton.attr('disabled','disabled');
        }
        else {
            addEditorForm.cancel.hide();
            $('img#indicatorTwo').removeClass('hidden');            
            addEditorForm.submit.addClass('disabledSubmit');
            addEditorForm.submit.attr('disabled','disabled');
        }
              
        if ($(link)[0] === $('.remove:last')[0]) {
            removeLast = true;
        } 
        
        $.ajax({
            url: $(link).attr('href'),
            type: 'POST', 
            data: {
                deletion: $(link).parents('.editorship').data('editorshipUri')
            },
            dataType: 'json',
            context: link, // context for callback
            complete: function(request, status) {
                var editorship,
                    editorUri;
            
                if (status === 'success') {
                    
                    editorship = $(this).parents('.editorship');
                
                    // Clear autocomplete cache entries matching this editor's name, else
                    // autocomplete will be retrieved from the cache, which excludes the removed editor.
                    addEditorForm.clearAcCacheEntries(editorship.data('editorName'));
                    
                    // Remove this editor from the acFilter so it is included in autocomplete
                    // results again.
                    addEditorForm.removeEditorFromAcFilter(editorship.data('editorUri'));
                    
                    editorship.fadeOut(400, function() {
                        var numEditors;
 
                        // For undo link: add to a deletedEditorships array
                        
                        // Remove from the DOM                       
                        $(this).remove();
                        
                        // Actions that depend on the editor having been removed from the DOM:
                        numEditors = $('.editorship').length; // retrieve the length after removing editorship from the DOM
                        
                        // If removed item not last, reorder to remove any gaps
                        if (numEditors > 0 && ! removeLast) {
                            addEditorForm.reorderEditors();
                        }
                            
                        // If fewer than two editors remaining, disable drag-drop
                        if (numEditors < 2) {
                            addEditorForm.disableEditorDD();
                        }                           

                        if ( $('img#indicatorOne').is(':visible') ) {
                            $('img#indicatorOne').fadeOut(100, function() {
                                $(this).addClass('hidden');
                            });

                            addEditorForm.returnLink.fadeIn(100, function() {
                                $(this).show();
                            });
                            addEditorForm.showFormButton.removeClass('disabledSubmit');
                            addEditorForm.showFormButton.attr('disabled','');
                        }
                        else {
                            $('img#indicatorTwo').fadeOut(100, function() {
                                 $(this).addClass('hidden');
                             });

                             addEditorForm.cancel.fadeIn(100, function() {
                                 $(this).show();
                             });
                             addEditorForm.submit.removeClass('disabledSubmit');
                             addEditorForm.submit.attr('disabled','');
                        }
                    });

                } else {
                    alert(addEditorForm.removeEditorshipAlert);
                    
                }
            }
        });        
    },
    
    // Disable DD and associated cues if only one editor remains
    disableEditorDD: function() {
        var editorships = $('#dragDropList'),
            editorNameWrapper = $('.editorNameWrapper');
            
        editorships.sortable({ disable: true } );
        
        // Use class dd rather than jQuery UI's class ui-sortable, so that we can remove
        // the class if there's fewer than one editor. We don't want to remove the ui-sortable
        // class, in case we want to re-enable DD without a page reload (e.g., if implementing
        // adding an editor via Ajax request). 
        editorships.removeClass('dd');
              
        editorNameWrapper.removeAttr('title');
    },

    // RY To be implemented later.
    toggleRemoveLink: function() {
        // when clicking remove: remove the editor, and change link text to 'undo'
        // when clicking undo: add the editor back, and change link text to 'remove'
    },

	// Set the initial help text in the lastName field and change the class name.
	addAcHelpText: function(selectedObj) {
        var typeText;
        if ( $(selectedObj).attr('id') == "lastName" ) {
            typeText = addEditorForm.editorTypeText;
        }
        
        if (!$(selectedObj).val()) {
			$(selectedObj).val(addEditorForm.helpTextSelect + " " + typeText + " " + addEditorForm.helpTextAdd)
						   .addClass(this.acHelpTextClass);
		}
	},
	
	deleteAcHelpText: function(selectedObj) {
	    if ($(selectedObj).hasClass(this.acHelpTextClass)) {
	            $(selectedObj).val('')
	                          .removeClass(this.acHelpTextClass);
	    }
	},

    // we need to set the correct class names for fields like the acSelector, acSelection, etc.
    // as well as clear and disable fields, call other functions ...
	setEditorType: function(authType) {
        if ( authType == "person" ) {
	        this.personSection.show();
            this.acSelector.addClass("acSelector");
            this.personRadio.attr('checked', true);  // needed for reset when cancel button is clicked
	        this.selectedEditor.addClass("acSelection");
	        this.selectedEditorName.addClass("acSelectionInfo");
	        this.personLink.addClass("verifyMatch");
	        this.acSelector.attr('disabled', '');
	        this.firstNameField.attr('disabled', '');
	        this.middleNameField.attr('disabled', '');
	        this.lastNameField.attr('disabled', '');

	        addEditorForm.addAcHelpText(this.acSelector);
	        addEditorForm.initAutocomplete();
	    }
    }
};

$(document).ready(function() {   
    addEditorForm.onLoad();
}); 
