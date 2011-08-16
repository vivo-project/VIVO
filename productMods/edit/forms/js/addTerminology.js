/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var addTerminologyForm = {

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
        
        this.form = $('#addTerminologyForm');
        this.showFormButtonWrapper = $('#showAddForm');
        this.submit = this.form.find(':submit');
        this.cancel = this.form.find('.cancel'); 
        //Add term
        this.addTermButton = $('#showAddFormButton');
        //section where results should be displayed
        this.selectedTerm = $('#selectedTerm');
        //input for search term form
        this.searchTerm = $('#searchTerm');
        this.searchSubmit = $('#searchButton');
        //Hidden inputs for eventual submission
        this.referencedTerm = $('#referencedTerm');
        this.entryTerm = $('#entryTerm');
        this.termLabel = $('#termLabel');
        this.termType = $('#termType');
        this.removeTermLinks = $('a.remove');
        this.errors = $('#errors');
    },
    
    initPage: function() {
    	this.initTermData();
        this.bindEventListeners();
              
    },
    bindEventListeners: function() {
    	this.searchSubmit.click(function() {
            addTerminologyForm.submitSearchTerm();
            addTerminologyForm.clearErrors();
            return false;
         });
    	
    	this.form.submit(function() {
			return addTerminologyForm.prepareSubmit(); 
        });     
    	
    	this.addTermButton.click(function() {
    		addTerminologyForm.initForm();
    		
    	});
    	 this.removeTermLinks.click(function() {
             addTerminologyForm.removeExistingTerm(this);
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
            addTerminologyForm.showTermListOnlyView();
            return false;
        });  

        // Show the form
        this.form.show();                 
    },   
 // On page load, associate data with each existing term  element. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // authorships.
    initTermData: function() {
        $('.existingTerm').each(function(index) {
            $(this).data(existingTermsData[index]);    
            $(this).data('position', index+1);      
        });
    },
    clearSearchResults:function() {
    	$('#selectedTerm').empty();
    },
    clearErrors:function() {
    	addTerminologyForm.errors.empty();
    },
    showTermListOnlyView: function() {
        this.hideForm();
        this.showFormButtonWrapper.show();
    },
    submitSearchTerm: function() {
    	//Get value of search term
    	var searchValue = this.searchTerm.val();
    	this.entryTerm.val(searchValue);
    	var dataServiceUrl = addTerminologyForm.dataServiceUrl + "?searchTerm=" + encodeURIComponent(searchValue);
        $.getJSON(dataServiceUrl, function(results) {
            if ( results.All.length == 0 ) {
            } else {
                //update existing content type with correct class group name and hide class group select again
                var bestMatchResults = results["Best Match"];
                var numberMatches = bestMatchResults.length;
                var i;
                //For each result, display
                var htmlAdd = "";
                if(numberMatches > 0) {
                	htmlAdd = "<ul class='dd' id='terms' name='terms'>";
                	htmlAdd+= addTerminologyForm.addResultsHeader();
	                for(i = 0; i < numberMatches; i++) {
	                	var termResult = bestMatchResults[i];
	                	var CUI = termResult.CUI;
	                	var label = termResult.label;
	                	var definition = termResult.definition;
	                	var type = termResult.type;
	                	var cuiURI = addTerminologyForm.UMLSCUIURL + CUI;
	                	htmlAdd+= addTerminologyForm.generateIndividualTermDisplay(cuiURI, label, definition, type);
	                }
	                htmlAdd+= "</ul>";
                } else {
                	htmlAdd+= "<p>No search results found.</p>";
                }
            	$('#selectedTerm').html(htmlAdd);
            }
   
          });
    },
    addResultsHeader:function() {
    	var htmlAdd = "<li class='terminology'><div class='row'><span class='column termLabel'>Label (Type) </span><span class='column termDefinition'>Definition</span></div></li>";
    	return htmlAdd;
    },
    hideSearchResults:function() {
    	this.selectedTerm.hide();
    },
    prepareSubmit:function() {
    	var checkedElements = $("#CUI:checked");
    	if(!addTerminologyForm.validateTermSelection(checkedElements)) {
    		return false;
    	}
    	var i;
    	var len = checkedElements.length;
    	var checkedTerm, checkedTermElement, termLabel, termType;
    	var referencedTerms = [];
    	var termLabels = [];
    	var termTypes = [];
    	
    	checkedElements.each(function() {
    		checkedTermElement = $(this);
    		checkedTerm = checkedTermElement.val();
    		termType = checkedTermElement.attr("termType");
    		termLabel = checkedTermElement.attr("label");
    		referencedTerms.push(checkedTerm);
    		termLabels.push(termLabel);
    		termTypes.push(termType);
    	});
    	this.referencedTerm.val(referencedTerms);
    	this.termLabel.val(termLabels);
    	this.termType.val(termTypes);
    	return true;
    }, 
    generateIndividualTermDisplay: function(cuiURI, label, definition, type) {
    	var htmlAdd = "<li class='terminology'>" + 
    	"<div class='row'>" + 
    	"<span class='column termLabel'>" +
    	"<input type='checkbox'  id='CUI' name='CUI' value='" + cuiURI + "' label='" + label + "' termType='" + type + "'/>" + 
    	label + " (" + type + ")</span>" + 
    	"<span class='column termDefinition'>" + definition + "</span>" + 
    	"</div>" +  
    	"</li>";	
    	return htmlAdd;
    }, validateTermSelection:function(checkedElements) {
    	var numberElements = checkedElements.length;
    	if(numberElements < 1) {
    		addTerminologyForm.errors.html("<p class='validationError'>Please select at least one term from search results to add or click cancel.</p>");
    		return false;
    	}
    	return true;
    }, removeExistingTerm: function(link) {
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
                deletion: $(link).parents('.existingTerm').data('termNodeUri')
            },
            dataType: 'json',
            context: link, // context for callback
            complete: function(request, status) {
                var existingTerm,
                    termNodeUri;
            
                if (status === 'success') {
                    
                    existingTerm = $(this).parents('.existingTerm');
                    existingTerm.fadeOut(400, function() {
                        var numTerms;
                        // For undo link: add to a deletedAuthorships array
                        // Remove from the DOM                       
                        $(this).remove();
                        // Actions that depend on the author having been removed from the DOM:
                        numTerms = $('.existingTerm').length; // retrieve the length after removing authorship from the DOM        
                    });

                } else {
                    alert('Error processing request: term not removed');
                }
            }
        });        
    }
};

$(document).ready(function() {   
    addTerminologyForm.onLoad();
}); 
