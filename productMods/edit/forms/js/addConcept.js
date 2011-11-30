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
    },
    
    initPage: function() {
    	this.initConceptData();
        this.bindEventListeners();
              
    },
    bindEventListeners: function() {
    	this.searchSubmit.click(function() {
            addConceptForm.submitSearchTerm();
            addConceptForm.clearErrors();
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

        this.cancel.unbind('click');
        this.cancel.bind('click', function() {
        	//show only list of existing terms and hide adding term form
            addConceptForm.showConceptListOnlyView();
            return false;
        });  

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
    showConceptListOnlyView: function() {
        this.hideForm();
        this.showFormButtonWrapper.show();
    },
    submitSearchTerm: function() {
    	//Get value of search term
    	var searchValue = this.searchTerm.val();
    	var vocabSourceValue = $('input:radio[name="source"]:checked').val();
    	var dataServiceUrl = addConceptForm.dataServiceUrl + "?searchTerm=" + encodeURIComponent(searchValue) + "&source=" + encodeURIComponent(vocabSourceValue);
        $.getJSON(dataServiceUrl, function(results) {
            var htmlAdd = "";

            if ( results== null || results.array == null || results.array.length == 0 ) {
            	htmlAdd = "<p>No search results</p>";
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
            	$('#selectedConcept').html(htmlAdd);
            }
   
          });
    },
    parseResults:function(resultsArray) {
    	//Loop through array and check if this is the best match
    	var arrayLen = resultsArray.length;
    	var bestMatchResults = new Array();
    	var i;
    	for(i = 0; i < arrayLen; i++) {
    		var concept = resultsArray[i];
    		if(concept.bestMatch == "true") {
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
    	var checkedElements = $("#CUI:checked");
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
    	"<input type='checkbox'  id='CUI' name='CUI' value='" + cuiURI + "' label='" + label + "' conceptType='" + type + "' conceptDefinedBy='" + definedBy + "'/>" + 
    	label + " (" + type + ")</span>" + 
    	"<span class='column conceptDefinition'>" + definition + "</span>" + 
    	"</div>" +  
    	"</li>";	
    	return htmlAdd;
    }, validateConceptSelection:function(checkedElements) {
    	var numberElements = checkedElements.length;
    	if(numberElements < 1) {
    		addConceptForm.errors.html("<p class='validationError'>Please select at least one term from search results to add or click cancel.</p>");
    		return false;
    	}
    	return true;
    }, removeExistingConcept: function(link) {
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
