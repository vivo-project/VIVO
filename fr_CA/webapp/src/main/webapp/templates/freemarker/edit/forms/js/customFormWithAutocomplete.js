/* $This file is distributed under the terms of the license in LICENSE$ */

var customForm = {

    /* *** Initial page setup *** */
   //Setting the default Concept class here
	//This would need to change if we update the ontology, etc.
	conceptClassURI: "http://www.w3.org/2004/02/skos/core#Concept",
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
        $.extend(this, i18nStrings);
    },

    // On page load, create references for easy access to form elements.
    // NB These must be assigned after the elements have been loaded onto the page.
    initObjects: function(){

        this.form = $('form.customForm');
        this.fullViewOnly = $('.fullViewOnly');
        this.button = $('#submit');
        this.requiredLegend = $('#requiredLegend');
        this.typeSelector = this.form.find('select#typeSelector');
        this.typeSelectorInput = this.form.find('input#typeSelectorInput');
        this.typeSelectorSpan = this.form.find('span#typeSelectorSpan');
        this.or = $('span.or');
        this.cancel = this.form.find('.cancel');
        this.acHelpTextClass = 'acSelectorWithHelpText';
        // this.verifyMatch is referenced in bindEventListeners to size and open
        // the verify popup window. Although there could be multiple verifyMatch objects
        // selecting one and binding the event works for all of them
        this.verifyMatch = this.form.find('.verifyMatch');
        this.defaultAcType = ""; // will be set in setType() first time through
        this.templateDefinedAcTypes = false;
        if ( this.acTypes != undefined ) {
            this.templateDefinedAcTypes = true;
        }

        // find all the acSelector input elements
        this.acSelectors = [] ;

        this.form.find('.acSelector').each(function() {
            customForm.acSelectors.push($(this));
        });

        // find all the acSelection div elements
        this.acSelections = new Object();

        this.form.find('.acSelection').each(function() {
            var groupName  = $(this).attr('acGroupName');
            customForm.acSelections[groupName] = $(this);
        });

        // 2-stage forms with only one ac field will not have the acTypes defined
        // so create an object for when the user selects a type via the typeSelector
        if ( this.acTypes == undefined || this.acTypes == null ) {
            this.acTypes = new Object();
        }

        // forms with multi ac fields will have this defined in customFormData
        // this is helpful when the type to display is not a single word, like "Subject Area"
        this.hasMultipleTypeNames = false;
        if ( this.multipleTypeNames != undefined || this.multipleTypeNames != null ) {
            this.hasMultipleTypeNames = true;
        }
        // Used with the cancel link. If the user cancels after a type selection, this check
        // ensures that any a/c fields (besides the one associated with the type) will be reset
        this.clearAcSelections = false;

    },

    // Set up the form on page load
    initPage: function() {

        if (!this.editMode) {
            this.editMode = 'add'; // edit vs add: default to add
        }

        //Flag to clear label of selected object from autocomplete on submission
        //This is used in the case where the label field is submitted only when a new object is being created
        if(!this.flagClearLabelForExisting) {
        	this.flagClearLabelForExisting = null;
        }

        if (!this.formSteps) { // Don't override formSteps specified in form data
            if ( !this.fullViewOnly.length || this.editMode === 'edit' || this.editMode === 'repair' ) {
                this.formSteps = 1;
            // there may also be a 3-step form - look for this.subTypeSelector
            }
            else {
                this.formSteps = 2;
            }
        }

        if(!this.doNotRemoveOriginalObject) {
        	this.doNotRemoveOriginalObject = false;
        }

        this.bindEventListeners();

       $.each(this.acSelectors, function() {
            customForm.initAutocomplete($(this));
        });

        this.initElementData();

        this.initFormView();

        // Set the initial autocomplete help text in the acSelector fields.
        $.each(this.acSelectors, function() {
                customForm.addAcHelpText($(this));
        });
    },

    initFormView: function() {

        var typeVal = this.typeSelector.val();

        // Put this case first, because in edit mode with
        // validation errors we just want initFormFullView.
//        if ((!this.supportEdit) && (this.editMode == 'edit' || this.editMode == 'repair')) {
        if (this.editMode == 'edit' || this.editMode == 'repair' || this.editMode == 'error') {
            this.initFormWithValidationErrors();
        }
        else if (this.findValidationErrors()) {
            this.initFormWithValidationErrors();
        }

        // If type is already selected when the page loads (Firefox retains value
        // on a refresh), go directly to full view. Otherwise user has to reselect
        // twice to get to full view.
        else if ( this.formSteps == 1 || typeVal.length ) {
            this.initFormFullView();
        }
        else {
            this.initFormTypeView();
        }
    },

    initFormTypeView: function() {

        this.setType(); // empty any previous values (perhaps not needed)
        this.hideFields(this.fullViewOnly);
        this.button.hide();
        this.or.hide();
        this.requiredLegend.hide();

        this.cancel.unbind('click');
    },

    initFormFullView: function() {

        this.setType();
        this.fullViewOnly.show();
        this.or.show();
        this.requiredLegend.show();
        this.button.show();
        this.setLabels();

        // Set the initial autocomplete help text in the acSelector fields.
        $.each(this.acSelectors, function() {
                customForm.addAcHelpText($(this));
        });

        this.cancel.unbind('click');
        if (this.formSteps > 1) {
            this.cancel.click(function() {
                customForm.clearFormData(); // clear any input and validation errors
                customForm.initFormTypeView();
                customForm.clearAcSelections = true;
                return false;
            });
        // In one-step forms, if there is a type selection field, but no value is selected,
        // hide the acSelector field. The type selection must be made first so that the
        // autocomplete type can be determined. If a type selection has been made,
        // unhide the acSelector field.
        } else if (this.typeSelector.length) {
            this.typeSelector.val() ? this.fullViewOnly.show() : this.hideFields(this.fullViewOnly);
        }
        if ( this.acSelectOnly ) {
            this.disableSubmit();
        }
    },

    initFormWithValidationErrors: function() {
        // Call initFormFullView first, because showAutocompleteSelection needs
        // acType, which is set in initFormFullView.
        this.initFormFullView();

        $.each(this.acSelectors, function() {
            var $acSelection = customForm.acSelections[$(this).attr('acGroupName')];
            var uri   = $acSelection.find('input.acUriReceiver').val(),
                label = $(this).val();
            if (uri && uri != ">SUBMITTED VALUE WAS BLANK<") {
                customForm.showAutocompleteSelection(label, uri, $(this));
            }
        });

    },

    // Bind event listeners that persist over the life of the page. Event listeners
    // that depend on the view should be initialized in the view setup method.
    bindEventListeners: function() {

        this.typeSelector.change(function() {
            var typeVal = $(this).val();
            this.acCache = {};

            // If an autocomplete selection has been made, undo it.
            // NEED TO LINK THE TYPE SELECTOR TO THE ACSELECTOR IT'S ASSOCIATED WITH
            // BECAUSE THERE COULD BE MORE THAN ONE AC FIELD. ASSOCIATION IS MADE VIA
            // THE SPECIAL "acGroupName" ATTRIBUTE WHICH IS SHARED AMONG THE SELECT AND
            // THE INPUT AND THE AC SELECTION DIV.
            if (customForm.editMode != "edit") {
                customForm.undoAutocompleteSelection($(this));
            }
            // Reinitialize view. If no type selection in a two-step form, go back to type view;
            // otherwise, reinitialize full view.
			if (!typeVal.length && customForm.formSteps > 1) {
				customForm.initFormTypeView();
			}
			else {
				customForm.initFormFullView();
			}
        });

        this.verifyMatch.click(function() {
            window.open($(this).attr('href'), 'verifyMatchWindow', 'width=640,height=640,scrollbars=yes,resizable=yes,status=yes,toolbar=no,menubar=no,location=no');
            return false;
        });

        // loop through all the acSelectors
        $.each(this.acSelectors, function() {
            $(this).focus(function() {
                customForm.deleteAcHelpText($(this));
            });
            $(this).blur(function() {
                customForm.addAcHelpText($(this));
            });
        });

        this.form.submit(function() {
        	//TODO: update the following
        	//custom submission for edit mode in case where existing object should not remove original object
        	//if edit mode and custom flag and original uri not equivalent to new uri, then
        	//clear out label field entirely
        	//originally checked edit mode but want to add to work the same way in case an existing object
        	//is selected since the n3 now governs how labels
        	if(customForm.flagClearLabelForExisting != null) {
        		//Find the elements that have had autocomplete executed, tagged by class "userSelected"
        		customForm.form.find('.acSelection.userSelected').each(function() {
        			var groupName = $(this).attr("acGroupName");
        			var inputs = $(this).find("input.acUriReceiver");
        			//if user selected, then clear out the label since we only
        			//want to submit the label as value on form if it's a new label
        			if(inputs.length && $(inputs.eq(0)).attr(customForm.flagClearLabelForExisting)) {
        			    var $selectorInput = $("input.acSelector[acGroupName='" + groupName + "']");
        			    var $displayInput = $("input.display[acGroupName='" + groupName + "']");
    					$displayInput.val($selectorInput.val());
    					$selectorInput.val('');
        			}
                });
        	}

            customForm.deleteAcHelpText();
        });
    },

    initAutocomplete: function(selectedObj) {
        this.getAcFilter();
        //If specific individuals are to be filtered out, add them here
        //to the filtering list
        this.getAcFilterForIndividuals();
        this.acCache = {};

        $(selectedObj).autocomplete({
            minLength: 3,
            source: function(request, response) {
        		//Reset the URI of the input to one that says new uri required
        		//That will be overwritten if value selected from autocomplete
        		//We do this everytime the user types anything in the autocomplete box
        		customForm.initDefaultBlankURI(selectedObj);
                if (request.term in customForm.acCache) {
                    // console.log('found term in cache');
                    response(customForm.acCache[request.term]);
                    return;
                }
                // console.log('not getting term from cache');
                $.ajax({
                    url: customForm.acUrl,
                    dataType: 'json',
                    data: {
                        term: request.term,
                        type: customForm.acTypes[$(selectedObj).attr('acGroupName')],
                        multipleTypes:(customForm.acMultipleTypes == undefined || customForm.acMultipleTypes == null)? null: customForm.acMultipleTypes
                    },
                    complete: function(xhr, status) {
                        // Not sure why, but we need an explicit json parse here.
                        var results = $.parseJSON(xhr.responseText);
                        var filteredResults = customForm.filterAcResults(results);
                        /*
                        if ( customForm.acTypes[$(selectedObj).attr('acGroupName')] == customForm.conceptClassURI ) {
                            filteredResults = customForm.removeConceptSubclasses(filteredResults);
                        }*/
                        if(customForm.doRemoveConceptSubclasses()) {
                        	filteredResults = customForm.removeConceptSubclasses(filteredResults);
                        }

                        customForm.acCache[request.term] = filteredResults;
                        response(filteredResults);
                    }
                });
            },
            select: function(event, ui) {
                customForm.showAutocompleteSelection(ui.item.label, ui.item.uri, $(selectedObj));
                if ( $(selectedObj).attr('acGroupName') == customForm.typeSelector.attr('acGroupName') ) {
                    customForm.typeSelector.val(ui.item.msType);
                }
            }
        });
    },

    //Method to check whether we need to filter to individuals with a most specific type = Concept or other allowed subclasses
    doRemoveConceptSubclasses:function() {
    	//if this array of allowable subclasses was declared annd there is at least one element in it
    	if(customForm.limitToConceptClasses && customForm.limitToConceptClasses.length) {
    		return true;
    	}
    	return false;
    },

    // Store original or base text with elements that will have text substitutions.
    // Generally the substitution cannot be made on the current value, since that value
    // may have changed from the original. So we store the original text with the element to
    // use as a base for substitutions.
    initElementData: function() {

        this.placeholderText = '###';
        this.labelsWithPlaceholders = this.form.find('label, .label').filter(function() {
            return $(this).html().match(customForm.placeholderText);
        });
        this.labelsWithPlaceholders.each(function(){
            $(this).data('baseText', $(this).html());
        });

        this.button.data('baseText', this.button.val());

    },
    //get autocomplete filter with sparql query
    getAcFilter: function() {

        if (!this.sparqlForAcFilter) {
            //console.log('autocomplete filtering turned off');
            this.acFilter = null;
            return;
        }

        //console.log("sparql for autocomplete filter: " + this.sparqlForAcFilter);

        // Define this.acFilter here, so in case the sparql query fails
        // we don't get an error when referencing it later.
        this.acFilter = [];
        $.ajax({
            url: customForm.sparqlQueryUrl,
            dataType: "json",
            data: {
                query: customForm.sparqlForAcFilter
            },
            success: function(data, status, xhr) {
                customForm.setAcFilter(data);
            }
        });
    },

    setAcFilter: function(data) {

        var key = data.head.vars[0];

        $.each(data.results.bindings, function() {
            customForm.acFilter.push(this[key].value);
        });
    },

    filterAcResults: function(results) {
        var filteredResults;

        if (!this.acFilter || !this.acFilter.length) {
            //console.log('no autocomplete filtering applied');
            return results;
        }

        filteredResults = [];
        $.each(results, function() {
            if ($.inArray(this.uri, customForm.acFilter) == -1) {
                //console.log('adding ' + this.label + ' to filtered results');
                filteredResults.push(this);
            }
            else {
                //console.log('filtering out ' + this.label);
            }
        });
        return filteredResults;
    },
    //To filter out specific individuals, not part of a query
    //Pass in list of individuals to be filtered out
    getAcFilterForIndividuals: function() {

        if (!this.acFilterForIndividuals || !this.acFilterForIndividuals.length) {
            this.acFilterForIndividuals = null;
            return;
        }
       //add this list to the ac filter list
        customForm.acFilter = customForm.acFilter.concat(this.acFilterForIndividuals);

    },

    //Updating this code to utilize an array to
    removeConceptSubclasses: function(array) {
    	//Using map because the resulting array might be different from the original
    	array = jQuery.map(array, function(arrayValue, i) {
    	   var allMsTypes = arrayValue["allMsTypes"];
      	   var removeElement = false;
      	 if(allMsTypes.length == 1 && !customForm.isAllowedConceptSubclass(arrayValue["msType"])) {
             //Remove from array
             removeElement = true;
         }  else if(allMsTypes.length > 1) {
       	  //If there are multiple most specific types returned, check if none of them equals concept
       	  removeElement = true;
       	  var j;

       	  for(j = 0; j < allMsTypes.length; j++) {
       		  //this refers to the element itself
       		  if(customForm.isAllowedConceptSubclass(allMsTypes[j])) {
       			 //don't remove this element if one of the most specific types is a concept
       			 removeElement = false;
       			 break;
       		 }
       	  }
         }

         if(removeElement)
        	 return null;
         else
        	 return arrayValue;
    	});


       return array;
    },
    isAllowedConceptSubclass:function(classURI) {
    	if(customForm.limitToConceptClasses && customForm.limitToConceptClasses.length) {
    		var len = customForm.limitToConceptClasses.length;
    		var i;
    		for(i = 0; i < len; i++) {
    			if(classURI == customForm.limitToConceptClasses[i]) {
    				return true;
    			}
    		}
    	}
    	return false;
    },

    showAutocompleteSelection: function(label, uri, selectedObj) {
        // hide the acSelector field and set it's value to the selected ac item
        this.hideFields($(selectedObj).parent());
        $(selectedObj).val(label);

        var $acDiv = this.acSelections[$(selectedObj).attr('acGroupName')];

        // provides a way to monitor selection in other js files, e.g. to hide fields upon selection
        $acDiv.addClass("userSelected");

        // If the form has a type selector, add type name to label in add mode. In edit mode,
        // use typeSelectorSpan html. The second case is an "else if" and not an else because
        // the template may not be passing the label to the acSelection macro or it may not be
        // using the macro at all and the label is hard-coded in the html.
        // ** With release 1.6 and display of all fields, more labels are hard-coded in html.
        // ** So check if there's a label before doing anything else.

        if ( $acDiv.find('label').html().length === 0 ) {

            if ( this.typeSelector.length && ($acDiv.attr('acGroupName') == this.typeSelector.attr('acGroupName')) ) {
                $acDiv.find('label').html('Selected ' + this.typeName + ':');
            }
            else if ( this.typeSelectorSpan.html() && ($acDiv.attr('acGroupName') == this.typeSelectorInput.attr('acGroupName')) ) {
                $acDiv.find('label').html('Selected ' + this.typeSelectorSpan.html() + ':');
            }
            else if ( $acDiv.find('label').html() == '' ) {
                $acDiv.find('label').html('Selected ' + this.multipleTypeNames[$(selectedObj).attr('acGroupName')] + ':');
            }
        }

        $acDiv.show();
        $acDiv.find("input").val(uri);
        $acDiv.find("span").html(label);
        $acDiv.find("a.verifyMatch").attr('href', this.baseHref + uri);

        $changeLink = $acDiv.find('a.changeSelection');
        $changeLink.click(function() {
            customForm.undoAutocompleteSelection($acDiv);
        });

        if ( this.acSelectOnly ) {
        	//On initialization in this mode, submit button is disabled
        	this.enableSubmit();
        }
    },

    undoAutocompleteSelection: function(selectedObj) {
        // The test is not just for efficiency: undoAutocompleteSelection empties the acSelector value,
        // which we don't want to do if user has manually entered a value, since he may intend to
        // change the type but keep the value. If no new value has been selected, form initialization
        // below will correctly empty the value anyway.

        var $acSelectionObj = null;
        var $acSelector = null;

        // Check to see if the parameter is the typeSelector. If it is, we need to get the acSelection div
        // that is associated with it.  Also, when the type is changed, we need to determine whether the user
        // has selected an existing individual in the corresponding name field or typed the label for a new
        // individual. If the latter, we do not want to clear the value on type change. The clearAcSelectorVal
        // boolean controls whether the acSelector value gets cleared.

        var clearAcSelectorVal = true;

        if ( $(selectedObj).attr('id') == "typeSelector" ) {
            $acSelectionObj = customForm.acSelections[$(selectedObj).attr('acGroupName')];
            if ( $acSelectionObj.is(':hidden') ) {
                clearAcSelectorVal = false;
            }
            // if the type is being changed after a cancel, any additional a/c fields that may have been set
            // by the user should be "undone". Only loop through these if this is not the initial type selection
            if ( customForm.clearAcSelections ) {
                $.each(customForm.acSelections, function(i, acS) {
                    var $checkSelection = customForm.acSelections[i];
                    if ( $checkSelection.is(':hidden') && $checkSelection.attr('acGroupName') != $acSelectionObj.attr('acGroupName') ) {
                        customForm.resetAcSelection($checkSelection);
                        $acSelector = customForm.getAcSelector($checkSelection);
                        $acSelector.parent('p').show();
                    }
                });
            }
        }
        else {
            $acSelectionObj = $(selectedObj);
            customForm.typeSelector.val('');
        }

        $acSelector = this.getAcSelector($acSelectionObj);
        $acSelector.parent('p').show();
        this.resetAcSelection($acSelectionObj);
        if ( clearAcSelectorVal == true ) {
            $acSelector.val('');
            $("input.display[acGroupName='" + $acSelectionObj.attr('acGroupName') + "']").val("");
        }
        customForm.addAcHelpText($acSelector);

        //Resetting so disable submit button again for object property autocomplete
        if ( this.acSelectOnly ) {
        	this.disableSubmit();
        }
        this.clearAcSelections = false;
    },

    // this is essentially a subtask of undoAutocompleteSelection
    resetAcSelection: function(selectedObj) {
        this.hideFields($(selectedObj));
        $(selectedObj).removeClass('userSelected');
        $(selectedObj).find("input.acUriReceiver").val(this.blankSentinel);
        $(selectedObj).find("span").text('');
        $(selectedObj).find("a.verifyMatch").attr('href', this.baseHref);
    },

    // loops through the array of acSelector fields and returns the one
    // associated with the selected object
    getAcSelector: function(selectedObj){
        var $selector = null
        $.each(this.acSelectors, function() {
            if ( $(this).attr('acGroupName') == $(selectedObj).attr('acGroupName') ) {
                $selector = $(this);
            }
        });
        return $selector;
    },

    // Set type uri for autocomplete, and type name for labels and button text.
    // Note: we still need this in edit mode, to set the text values.
    setType: function() {
        var selectedType;
        // If there's no type selector, these values have been specified in customFormData,
        // and will not change over the life of the form.
        if (!this.typeSelector.length) {
            if ( this.editMode == 'edit' && (this.typeSelectorSpan.html() != null && this.typeSelectorInput.val() != null) ) {
                this.typeName = this.typeSelectorSpan.html();
                this.acTypes[this.typeSelectorInput.attr('acGroupName')] = this.typeSelectorInput.val();
            }
            return;
        }

        selectedType = this.typeSelector.find(':selected');
        var acTypeKey = this.typeSelector.attr('acGroupName');

        if ( this.templateDefinedAcTypes && !this.defaultAcType.length ) {
            this.defaultAcType = this.acTypes[acTypeKey];
        }
        var selectedTypeLength = selectedType.val().length;
        if ( selectedTypeLength !== 'undefined') {
            this.acTypes[acTypeKey] = selectedType.val();
            this.typeName = selectedType.html();
            if ( this.editMode == 'edit' ) {
                var $acSelect = this.acSelections[acTypeKey];
                $acSelect.find('label').html( customForm.selectedString + ' ' + this.typeName + ':');
            }
        }
        // reset to empty values;
        else {
            if ( this.templateDefinedAcTypes ) {
                this.acTypes[acTypeKey] = this.defaultAcType;
            }
            else {
                this.acTypes = new Object();
            }
            this.typeName = this.defaultTypeName;
        }
    },

    // Set field labels based on type selection. Although these won't change in edit
    // mode, it's easier to specify the text here than in the ftl.
    setLabels: function() {
        var typeName = this.getTypeNameForLabels();

        this.labelsWithPlaceholders.each(function() {
            var newLabel = $(this).data('baseText').replace(customForm.placeholderText, typeName);
            $(this).html(newLabel);
        });

    },

    getTypeNameForLabels: function(selectedObj) {
        // If this.acType is empty, we are either in a one-step form with no type yet selected,
        // or in repair mode in a two-step form with no type selected. Use the default type
        // name specified in the form data.
        if ( !selectedObj || !this.hasMultipleTypeNames ) {
            if ( this.acTypes && this.typeName ) {
                return this.typeName;
            }
            else {
                return this.capitalize(this.defaultTypeName);
            }
        }
        else if ( selectedObj && ( $(selectedObj).attr('acGroupName') == this.typeSelector.attr('acGroupName') ) ) {
            if ( this.acTypes && this.typeName ) {
                return this.typeName;
            }
            else {
                return this.capitalize(this.defaultTypeName);
            }
        }
        else {
            var name = customForm.multipleTypeNames[$(selectedObj).attr('id')];
            return this.capitalize(name);
        }
    },

    // Set the initial help text that appears in the autocomplete field and change the class name
    addAcHelpText: function(selectedObj) {
        var typeText;
        // First case applies on page load; second case applies when the type gets changed. With multiple
        // ac fields there are cases where we also have to check if the help text is already there
        if (!$(selectedObj).val() || $(selectedObj).hasClass(this.acHelpTextClass) || $(selectedObj).val().substring(0, 18) == customForm.selectAnExisting ) {
        	typeText = this.getTypeNameForLabels($(selectedObj));
        	var helpText = customForm.selectAnExisting + " " + typeText + " " + customForm.orCreateNewOne ;
        	//Different for object property autocomplete
        	if ( this.acSelectOnly ) {
        		helpText = customForm.selectAnExisting + " " + typeText;
        	}
    		$(selectedObj).val(helpText)
    	               .addClass(this.acHelpTextClass);
    	}
    },


    deleteAcHelpText: function(selectedObj) {
        // on submit, no selectedObj gets passed, so we need to check for this
        if ( selectedObj ) {
            if ($(selectedObj).hasClass(this.acHelpTextClass)) {
        		$(selectedObj).val('')
        	                  .removeClass(this.acHelpTextClass);
        	}
        }
        else {
            $.each(this.acSelectors, function() {
                if ($(this).hasClass(customForm.acHelpTextClass)) {
    		        $(this).val('')
    	                   .removeClass(customForm.acHelpTextClass);
    	        }
    	    });
    	}
    },
    disableSubmit: function() {
		 //Disable submit button until selection made
      this.button.attr('disabled', 'disabled');
      this.button.addClass('disabledSubmit');
	},
	enableSubmit:function() {
		this.button.removeAttr('disabled');
		this.button.removeClass('disabledSubmit');
	},
	initDefaultBlankURI:function(selectedObj) {
		//get uri input for selected object and set to value specified as "blank sentinel"
		//If blank sentinel is neither null nor an empty string, this means if the user edits an
		//existing relationship to an object and does not select anything from autocomplete
		//from that object, the old relationship will be removed in n3 processing
        var $acDiv = this.acSelections[$(selectedObj).attr('acGroupName')];
        $acDiv.find("input").val(customForm.blankSentinel);
	}

};

$(document).ready(function() {
	/*
	 * UQAM
	 * Catch typeError error event ... and do nothing
	 * to avoid crashing the page construction 
	 */
	try {
	     customForm.onLoad();
		}
		catch(err) {
		}

});
