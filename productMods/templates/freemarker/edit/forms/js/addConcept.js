/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var addConceptForm = {

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
    initObjects: function() {
        
        this.form = $('#addConceptForm');
        this.showFormButtonWrapper = $('#showAddForm');
        this.submit = this.form.find(':submit');
        this.cancel = this.form.find('.cancel'); 
        //Add term
        this.addConceptButton = $('#showAddFormButton');
        //section where results should be displayed
        this.selectedConcept = $('#selectedConcept');
        //input for search term form
        this.searchTerm = $('#searchTerm');
        this.searchSubmit = $('#searchButton');
        //Hidden inputs for eventual submission
        this.externalConceptURI = $('#conceptNode');
        this.externalConceptLabel = $('#conceptLabel');
        this.externalConceptSource = $('#conceptSource');
        this.externalConceptSemanticTypeLabel = $("#conceptSemanticTypeLabel");
        this.externalConceptBroaderUris = $("#conceptBroaderURI");
        this.externalConceptNarrowerUris = $("#conceptNarrowerURI");
        //remove links
        this.removeConceptLinks = $('a.remove');
        this.errors = $('#errors');
        this.createOwn1 = $('#createOwnOne');
        this.createOwn2 = $('#createOwnTwo');
        this.orSpan = $('span.or')
        this.loadingIndicator = $("#indicator");
        this.showHideSearchResults = $("#showHideResults");
        //Value we are setting to cut off length of alternate labels string
        this.maxNumberAlternateLabels = 4;
        this.numberOfMaxInitialSearchResults = 7;
    },
    
    initPage: function() {
    	this.initConceptData();
        this.bindEventListeners();
              
    },
    bindEventListeners: function() {
    	this.searchSubmit.click(function() {
    		addConceptForm.clearErrors();
            addConceptForm.submitSearchTerm();
            return false;
         });
    	
    	this.form.submit(function() {
			return addConceptForm.prepareSubmit(); 
        });     
    	
    	this.addConceptButton.click(function() {
    		addConceptForm.initForm();
    		
    	});
    	 this.removeConceptLinks.click(function() {
             addConceptForm.removeExistingConcept(this);
             return false;
         });
    	 this.showHideSearchResults.find("a#showHideLink").click(function() {
    		 addConceptForm.showHideMultipleSearchResults(this);
    		 return false;
    	 });
    },
    initForm: function() {
        // Hide the button that shows the form
        this.showFormButtonWrapper.hide(); 
        this.clearSearchResults();
        // Hide the create own link, add selected button and "or"" span
        this.orSpan.hide();
        this.createOwn2.hide();
        this.submit.hide();
        //Also clear the search input
        this.searchTerm.val("");
        this.cancel.unbind('click');
        //make sure results loading indicator is hidden
        this.loadingIndicator.addClass("hidden");
        this.showHideSearchResults.hide();
        // Show the form
        this.form.show();                 
    },   
 // On page load, associate data with each existing term  element. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // authorships.
    initConceptData: function() {
        $('.existingConcept').each(function(index) {
            $(this).data(existingConceptsData[index]);    
            $(this).data('position', index+1);      
        });
    },
    clearSearchResults:function() {
    	$('#selectedConcept').empty();
    	//Hide the indicator icon if still there
    	$("#indicator").addClass("hidden");
    },
    clearErrors:function() {
    	addConceptForm.errors.empty();
    },
    showHiddenElements:function(results) {
        this.createOwn1.hide();
        if ( results ) {
            this.orSpan.show();
            this.createOwn2.show();
            this.submit.show();
        }
        else {
            this.orSpan.show();
            this.createOwn2.show();
        }
    },
    showConceptListOnlyView: function() {
        this.hideForm();
        this.showFormButtonWrapper.show();
    },
    showHideMultipleSearchResults: function(link) {
    	if($(link).hasClass("showmore")) {
    		//if clicking and already says show more then need to show the rest of the results
    		$("li.concepts").show(); //show everything
    		$(link).html(addConceptForm.displayLess);
    		$(link).removeClass("showmore");
    	} else {
    		//if clicking and does not say show  more than need to show less
    		$("li.concepts").slice(addConceptForm.numberOfMaxInitialSearchResults).hide();
    		$(link).html(addConceptForm.displayMoreEllipsis);
    		$(link).addClass("showmore");
    	}
    },
    //reset this to default, which is hidden with show more link
    resetShowHideMultipleSearchResults: function() {
    	addConceptForm.showHideSearchResults.hide();
    	addConceptForm.showHideSearchResults.find("a#showHideLink").html("Show more results");
    	addConceptForm.showHideSearchResults.find("a#showHideLink").addClass("showmore");
    },
    submitSearchTerm: function() {
    	//Get value of search term
    	var searchValue = this.searchTerm.val();
    	var checkedVocabSource = $('input:radio[name="source"]:checked');
    	var hasResults = false;
    	if(!checkedVocabSource.length) {
    		addConceptForm.showUncheckedSourceError();
    		return;
    	}
    	var vocabSourceValue = checkedVocabSource.val();
    	var dataServiceUrl = addConceptForm.dataServiceUrl + "?searchTerm=" + encodeURIComponent(searchValue) + "&source=" + encodeURIComponent(vocabSourceValue);
        //Show the loading icon until the results appear
    	addConceptForm.loadingIndicator.removeClass("hidden");
    	//remove the old search results if there are any
    	$("#selectedConcept").empty();
    	//Hide and reset the show more button
    	addConceptForm.resetShowHideMultipleSearchResults();
    	//This should return an object including the concept list or any errors if there are any
    	$.getJSON(dataServiceUrl, function(results) {
            var htmlAdd = "";
            var vocabUnavailable = "<p>" + addConceptForm.vocServiceUnavailable + "</p>";
            if ( results== null  || results.semanticServicesError != null || results.conceptList == null) {
            	htmlAdd = vocabUnavailable;
            }
            else {
            	//array is an array of objects representing concept information
            	//loop through and find all the best matches
            	var allResults = addConceptForm.parseResults(results.conceptList);
            	var bestMatchResults = allResults["bestMatch"];
            	var alternateResults = allResults["alternate"];
                var numberBestMatches = bestMatchResults.length;
                var numberAlternateMatches = alternateResults.length;
            	var numberTotalMatches = numberBestMatches + numberAlternateMatches;

                var i;
                //For each result, display
                if(numberTotalMatches > 0) {
                	htmlAdd = "<ul class='dd' id='concepts' name='concepts'>";
                	htmlAdd+= addConceptForm.addResultsHeader(vocabSourceValue);
                	//Show best matches first
	                for(i = 0; i < numberBestMatches; i++) {
	                	var conceptResult = bestMatchResults[i];
	                	htmlAdd+= addConceptForm.displayConceptSearchResult(conceptResult, true);
	                }
	                //If any alternate matches, show that next
	                for(i = 0; i < numberAlternateMatches; i++) {
	                	var conceptResult = alternateResults[i];
	                	htmlAdd+= addConceptForm.displayConceptSearchResult(conceptResult, false);
	                }
	                htmlAdd+= "</ul>";
                } else {
                	htmlAdd+= "<p>" + addConceptForm.noResultsFound + "</p>";
                }
            	
            }
            if(htmlAdd.length) {
            	//hide the loading icon again
            	addConceptForm.loadingIndicator.addClass("hidden");
            	$('#selectedConcept').html(htmlAdd);
            	if (htmlAdd.indexOf("No search results") >= 0) {
            	    addConceptForm.showHiddenElements(hasResults);
            	}
            	else {
            	   hasResults = true;
                   addConceptForm.showHiddenElements(hasResults);
                   //Here, tweak the display based on the number of results
                   addConceptForm.displayUptoMaxResults();
                }
            }
          });
        return true;
    },
    displayConceptSearchResult:function(conceptResult, isBestMatch) {
    	var conceptId = conceptResult.conceptId;
    	var label = conceptResult.label;
    	var definition = conceptResult.definition;
    	var definedBy = conceptResult.definedBy;
    	var type = conceptResult.type;
    	var uri = conceptResult.uri;
    	//also adding broader and narrower uris wherever they exist
    	var broaderUris = conceptResult.broaderURIList;
    	var narrowerUris = conceptResult.narrowerURIList;
    	//this will be null if there are no alternate labels
    	var altLabels = conceptResult.altLabelList;
    	return addConceptForm.generateIndividualConceptDisplay(uri, label, altLabels, definition, type, definedBy, isBestMatch, broaderUris, narrowerUris);
    },
    //This should now return all best matches in one array and other results in another
    parseResults:function(resultsArray) {
    	//Loop through array and check if this is the best match
    	var arrayLen = resultsArray.length;
    	var bestMatchResults = new Array();
    	//this is for results that are not best match results
    	var alternateResults = new Array();
    	var i;
    	for(i = 0; i < arrayLen; i++) {
    		var concept = resultsArray[i];
    		if(concept.bestMatch != "false") {
    			bestMatchResults.push(concept);
    		} else {
    			alternateResults.push(concept);
    		}
    	}
    	return {"bestMatch":bestMatchResults, "alternate":alternateResults};
    },
    addResultsHeader:function(vocabSourceValue) {
    	var htmlAdd = "<li class='concepts'><div class='row'><span class='column conceptLabel'>" + 
    	addConceptForm.getVocabSpecificColumnLabel(vocabSourceValue) + " </span><span class='column conceptDefinition'>" + addConceptForm.definitionString + "</span><span class='column'>" + addConceptForm.bestMatchString + "</span></div></li>";
    	return htmlAdd;
    },
    //currently just the first column label depends on which service has been utilized
    getVocabSpecificColumnLabel: function(vocabSourceValue) {
    	var columnLabel = addConceptForm.vocabSpecificLabels[vocabSourceValue];
    	if(columnLabel == undefined) {
    		columnLabel = addConceptForm.defaultLabelTypeString;
    	}
    	return columnLabel;
    },
    hideSearchResults:function() {
    	this.selectedConcept.hide();
    },
    prepareSubmit:function() {
    	var checkedElements = $("input[name='CUI']:checked");
    	if(!addConceptForm.validateConceptSelection(checkedElements)) {
    		return false;
    	}
    	var i;
    	var len = checkedElements.length;
    	var checkedConcept, checkedConceptElement, conceptLabel, conceptSource, conceptSemanticType,
    	conceptBroaderUri, conceptNarrowerUri;
    	var conceptNodes = [];
    	var conceptLabels = [];
    	var conceptSources = [];
    	var conceptSemanticTypes = [];
    	var conceptBroaderUris = []; //each array element can be a string which is comma delimited for multiple uris
    	var conceptNarrowerUris = [];//same as above
    	
    	checkedElements.each(function() {
    		checkedConceptElement = $(this);
    		checkedConcept = checkedConceptElement.val();
    		conceptLabel = checkedConceptElement.attr("label");
    		conceptSource = checkedConceptElement.attr("conceptDefinedBy");
    		conceptSemanticType = checkedConceptElement.attr("conceptType");
    		conceptBroaderUri = checkedConceptElement.attr("broaderUris");
    		conceptNarrowerUri = checkedConceptElement.attr("narrowerUris");
    		conceptNodes.push(checkedConcept);
    		conceptLabels.push(conceptLabel);
    		conceptSources.push(conceptSource);
    		conceptSemanticTypes.push(conceptSemanticType);
    		conceptBroaderUris.push(conceptBroaderUri);
    		conceptNarrowerUris.push(conceptNarrowerUri);
    	});
    	this.externalConceptURI.val(conceptNodes);
    	this.externalConceptLabel.val(conceptLabels);
    	this.externalConceptSource.val(conceptSources);
    	this.externalConceptSemanticTypeLabel.val(conceptSemanticTypes);
    	//Using JSON here because there may be multiple broader and narrower uris per concept
    	//and using a regular string representation does not differentiate between which set of uris
    	//would belong to which concept
    	this.externalConceptBroaderUris.val(JSON.stringify(conceptBroaderUris));
    	this.externalConceptNarrowerUris.val(JSON.stringify(conceptNarrowerUris));
    	
    	return true;
    }, 
    generateIndividualConceptDisplay: function(cuiURI, label, altLabels, definition, type, definedBy, isBestMatch, broaderUris, narrowerUris) {
    	var htmlAdd = "<li class='concepts'>" + 
    	"<div class='row'>" + 
    	"<div class='column conceptLabel'>" +
    	addConceptForm.generateIndividualCUIInput(cuiURI, label, type, definedBy, broaderUris, narrowerUris) +  
    	addConceptForm.generateIndividualLabelsDisplay(label, altLabels) + addConceptForm.generateIndividualTypeDisplay(type) + "</div>" + 
    	addConceptForm.generateIndividualDefinitionDisplay(definition) + 
    	addConceptForm.generateBestOrAlternate(isBestMatch) +
    	"</div>" +  
    	"</li>";	
    	return htmlAdd;
    }, 
    generateIndividualCUIInput:function(cuiURI, label, type, definedBy, broaderUris, narrowerUris) {
    	return 	"<input type='checkbox'  name='CUI' value='" + cuiURI + "' label='" + 
    		label + "' conceptType='" + type + "' conceptDefinedBy='" + definedBy + "' " +
    		"broaderUris='" + broaderUris + "' narrowerUris='" + narrowerUris + "'/>";
    },
    //In case there are multiple labels display those
    generateIndividualLabelsDisplay:function(label, altLabels) {
    	var labelDisplay = label;
    	var displayAltLabels = altLabels;
    	if(altLabels != null && altLabels.length > 0) {
    		//Certain vocabulary services might return a long list of alternate labels, in which case we will show fewer 
    		//display only upto a certain number of alternate labels and use an ellipsis to signify there
    		//are additional terms
    		if(altLabels.length > addConceptForm.maxNumberAlternateLabels) {
    			displayAltLabels = altLabels.slice(0, addConceptForm.maxNumberAlternateLabels) + ",...";
    		}
    		labelDisplay += "<br> (" + displayAltLabels + ")";
    	}
    	return labelDisplay;
    },
    generateIndividualTypeDisplay:function(type) {
    	if(type != null && type.length > 0) {
    		return " (" + type + ")";
    	}
    	return "";
    },
    generateIndividualDefinitionDisplay:function(definition) {
    	//The definition in some cases may be an empty string, so to prevent the div
    	//from not appearing, we are replacing with 
    	if(definition == null || definition.length == 0) {
    		//definition = "&nbsp;";
    		definition = "No definition provided.";
    	}
    	return "<div class='column conceptDefinition'>" + definition + "</div>";
    },
    //adds another div with "best match" next to it if best match
    generateBestOrAlternate:function(isBestMatch) {
    	var className = "emptyColumn";
    	if(isBestMatch) {
    		className = "bestMatchFlag";
    	}
    	return "<div class='column'><div class='" + className + "'>&nbsp;</div></div>";	
    },
    //Certain vocabulary services return a great number of results, we would like the ability to show more or less of those results
    displayUptoMaxResults:function() {
    	var numberConcepts = $("li.concepts").length;
    	if(numberConcepts > addConceptForm.numberOfMaxInitialSearchResults) {
    		$("li.concepts").slice(addConceptForm.numberOfMaxInitialSearchResults).hide();
    		 //Hide the link for showing/hiding search results
            addConceptForm.showHideSearchResults.show();
            addConceptForm.showHideSearchResults.find("a#showHideLink").html(addConceptForm.displayMoreEllipsis);
            addConceptForm.showHideSearchResults.find("a#showHideLink").addClass("showmore");
    	}
    	
    },
    validateConceptSelection:function(checkedElements) {
    	var numberElements = checkedElements.length;
    	if(numberElements < 1) {
    		addConceptForm.errors.html("<p class='validationError'>" + addConceptForm.selectTermFromResults + "</p>");
    		return false;
    	}
    	return true;
    }, 
    showUncheckedSourceError:function() {
		addConceptForm.errors.html("<p class='validationError'>" + addConceptForm.selectVocSource + "</p>");
    },
    removeExistingConcept: function(link) {
        var removeLast = false,
            message = addConceptForm.confirmTermDelete;
            
        if (!confirm(message)) {
            return false;
        }
        
        if ($(link)[0] === $('.remove:last')[0]) {
            removeLast = true;
        } 
        //Using primitive rdf edit which expects an n3 string for deletion
        $.ajax({
            url: $(link).attr('href'),
            type: 'POST', 
            data: {
        		additions: '', 
                retractions: addConceptForm.generateDeletionN3($(link).parents('.existingConcept').data('conceptNodeUri'))
            },
            dataType: 'json',
            context: link, // context for callback
            complete: function(request, status) {
                var existingConcept,
                    conceptNodeUri;
            
                if (status === 'success') {
                    
                    existingConcept = $(this).parents('.existingConcept');
                    existingConcept.fadeOut(400, function() {
                        var numConcepts;
                        // For undo link: add to a deletedAuthorships array
                        // Remove from the DOM                       
                        $(this).remove();
                        // Actions that depend on the author having been removed from the DOM:
                        numConcepts = $('.existingConcept').length; // retrieve the length after removing authorship from the DOM        
                    });

                } else {
                    alert(addConceptForm.errorTernNotRemoved);
                }
            }
        });        
    },
    generateDeletionN3: function(conceptNodeUri) {
    	var n3String = "<" + addConceptForm.subjectUri + "> <" + addConceptForm.predicateUri + "> <" + conceptNodeUri + "> .";
    	//add inverse string to also be removed
    	if(addConceptForm.inversePredicateUri.length > 0) {
    		n3String += "<" + conceptNodeUri + "> <" + addConceptForm.inversePredicateUri + "> <" + addConceptForm.subjectUri + "> .";
    	}
    	return n3String;
    }
};

$(document).ready(function() {   
    addConceptForm.onLoad();
}); 
