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
        //remove links
        this.removeConceptLinks = $('a.remove');
        this.errors = $('#errors');
        this.createOwn = $('#createOwn');
        this.orSpan = $('span.or')
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
    },
    initForm: function() {
        // Hide the button that shows the form
        this.showFormButtonWrapper.hide(); 
        this.clearSearchResults();
        // Hide the create own link, add selected button and "or"" span
        this.orSpan.hide();
        this.createOwn.hide();
        this.submit.hide();
        //Also clear the search input
        this.searchTerm.val("");
        this.cancel.unbind('click');

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
    },
    clearErrors:function() {
    	addConceptForm.errors.empty();
    },
    showHiddenElements:function() {
        this.orSpan.show();
        this.createOwn.show();
        this.submit.show();
    },
    showSpanAndCreateOption:function() {
        this.orSpan.show();
        this.createOwn.show();
    },
    showConceptListOnlyView: function() {
        this.hideForm();
        this.showFormButtonWrapper.show();
    },
    submitSearchTerm: function() {
    	//Get value of search term
    	var searchValue = this.searchTerm.val();
    	var checkedVocabSource = $('input:radio[name="source"]:checked');
    	var createLink = this.createOwn;
    	if(!checkedVocabSource.length) {
    		addConceptForm.showUncheckedSourceError();
    		return;
    	}
    	var vocabSourceValue = checkedVocabSource.val();
    	var dataServiceUrl = addConceptForm.dataServiceUrl + "?searchTerm=" + encodeURIComponent(searchValue) + "&source=" + encodeURIComponent(vocabSourceValue);
        $.getJSON(dataServiceUrl, function(results) {
            var htmlAdd = "";

            if ( results== null || results.array == null || results.array.length == 0 ) {
            	htmlAdd = "<p>No search results found.</p>";
            } else {
            	//array is an array of objects representing concept information
            	//loop through and find all the best matches
            	
            	var bestMatchResults = addConceptForm.parseResults(results.array);
                var numberMatches = bestMatchResults.length;
                var i;
                //For each result, display
                if(numberMatches > 0) {
                	htmlAdd = "<ul class='dd' id='concepts' name='concepts'>";
                	htmlAdd+= addConceptForm.addResultsHeader();
	                for(i = 0; i < numberMatches; i++) {
	                	var conceptResult = bestMatchResults[i];
	                	var conceptId = conceptResult.conceptId;
	                	var label = conceptResult.label;
	                	var definition = conceptResult.definition;
	                	var definedBy = conceptResult.definedBy;
	                	var type = conceptResult.type;
	                	var uri = conceptResult.uri;
	                	htmlAdd+= addConceptForm.generateIndividualConceptDisplay(uri, label, definition, type, definedBy);
	                }
	                htmlAdd+= "</ul>";
                } else {
                	htmlAdd+= "<p>No search results found.</p>";
                }
            	
            }
            if(htmlAdd.length) {
            	$('#selectedConcept').html(htmlAdd);
            	if (htmlAdd.indexOf("No search results") >= 0) {
            	    addConceptForm.showSpanAndCreateOption();
            	}
            	else {
                   addConceptForm.showHiddenElements();
                }
            }
          });
        return true;
    },
    parseResults:function(resultsArray) {
    	//Loop through array and check if this is the best match
    	var arrayLen = resultsArray.length;
    	var bestMatchResults = new Array();
    	var i;
    	for(i = 0; i < arrayLen; i++) {
    		var concept = resultsArray[i];
    		if(concept.bestMatch != "false") {
    			bestMatchResults.push(concept);
    		}
    	}
    	return bestMatchResults;
    },
    addResultsHeader:function() {
    	var htmlAdd = "<li class='concepts'><div class='row'><span class='column conceptLabel'>Label (Type) </span><span class='column conceptDefinition'>Definition</span></div></li>";
    	return htmlAdd;
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
    	var checkedConcept, checkedConceptElement, conceptLabel, conceptSource;
    	var conceptNodes = [];
    	var conceptLabels = [];
    	var conceptSources = [];
    	
    	checkedElements.each(function() {
    		checkedConceptElement = $(this);
    		checkedConcept = checkedConceptElement.val();
    		conceptLabel = checkedConceptElement.attr("label");
    		conceptSource = checkedConceptElement.attr("conceptDefinedBy");
    		conceptNodes.push(checkedConcept);
    		conceptLabels.push(conceptLabel);
    		conceptSources.push(conceptSource);
    	});
    	this.externalConceptURI.val(conceptNodes);
    	this.externalConceptLabel.val(conceptLabels);
    	this.externalConceptSource.val(conceptSources);
    	return true;
    }, 
    generateIndividualConceptDisplay: function(cuiURI, label, definition, type, definedBy) {
    	var htmlAdd = "<li class='concepts'>" + 
    	"<div class='row'>" + 
    	"<span class='column conceptLabel'>" +
    	addConceptForm.generateIndividualCUIInput(cuiURI, label, type, definedBy) +  
    	label + addConceptForm.generateIndividualTypeDisplay(type) + "</span>" + 
    	addConceptForm.generateIndividualDefinitionDisplay(definition) + 
    	"</div>" +  
    	"</li>";	
    	return htmlAdd;
    }, 
    generateIndividualCUIInput:function(cuiURI, label, type, definedBy) {
    	return 	"<input type='checkbox'  name='CUI' value='" + cuiURI + "' label='" + label + "' conceptType='" + type + "' conceptDefinedBy='" + definedBy + "'/>";
    },
    generateIndividualTypeDisplay:function(type) {
    	if(type != null && type.length > 0) {
    		return " (" + type + ")";
    	}
    	return "";
    },
    generateIndividualDefinitionDisplay:function(definition) {
    	return "<span class='column conceptDefinition'>" + definition + "</span>";
    },
    validateConceptSelection:function(checkedElements) {
    	var numberElements = checkedElements.length;
    	if(numberElements < 1) {
    		addConceptForm.errors.html("<p class='validationError'>Please select at least one term from the search search results.</p>");
    		return false;
    	}
    	return true;
    }, 
    showUncheckedSourceError:function() {
		addConceptForm.errors.html("<p class='validationError'>Please select at least one external vocabulary source to search.</p>");
    },
    removeExistingConcept: function(link) {
        var removeLast = false,
            message = 'Are you sure you want to remove this term?';
            
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
                deletion: $(link).parents('.existingConcept').data('conceptNodeUri')
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
                    alert('Error processing request: term not removed');
                }
            }
        });        
    }
};

$(document).ready(function() {   
    addConceptForm.onLoad();
}); 
