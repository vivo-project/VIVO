/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function() {

	/*
	 * This will set initial values of the constants present in constants.js
	 * */
	initConstants();
	
	/* This is used to cache the current state whether the user is allowed to select more entities from 
    the datatable or not. Once Max number of entity selection is reached the user can no longer select 
    more & this variable will be set to false. */
    $("#datatable").data("isEntitySelectionAllowed", true);
    
    $notificationContainer = $("#notification-container").notify();
    
    graphContainer = $("#graphContainer");
    tableDiv = $('#paginatedTable');
    
    //temporalGraphProcessor.initiateTemporalGraphRenderProcess(graphContainer, jsonString);
    
    /*
     * When the intra-entity parameters are clicked,
     * update the status accordingly.   
     */         

    $("select.comparisonValues").change(function() {

        var selectedValue = $("select.comparisonValues option:selected").val();
        
        var selectedDataURL;
        
        $.each(COMPARISON_PARAMETERS_INFO, function(index, parameter) {
        	
            if (parameter.value === selectedValue) {
            	
            	currentParameter = parameter.name;
            	selectedDataURL = parameter.dataLink;
            	temporalGraphCommonURL = parameter.viewBaseLink;
            	csvDownloadURL = parameter.csvLink;
            }
        	
        });
        
        options = {
    			responseContainer: $("div#temporal-graph-response"),
    			bodyContainer: $("#body"),
    			errorContainer: $("#error-container"),
    			dataURL: selectedDataURL	
    		};
    	
    	setupLoadingScreen(options.responseContainer);
    		
    	$.ajax({
            url: options.dataURL,
            dataType: "json",
            timeout: 5 * 60 * 1000,
            success: function (data) {
    		
                if (data.error) {
                	options.bodyContainer.remove();
                	options.errorContainer.show();
                	options.responseContainer.unblock();
                } else {
                	options.bodyContainer.show();
                	options.errorContainer.remove();
                    temporalGraphProcessor.redoTemporalGraphRenderProcess(graphContainer, data);
                    options.responseContainer.unblock();
                }
            }
        });
    });
    
});
        
//click event handler for clear button
$("a.clear-selected-entities").live('click', function(){
    clearRenderedObjects();
}); 

$("input[type=checkbox].easyDeselectCheckbox").live('click', function(){
    
    var checkbox = $(this);
    var checkboxValue = $(this).attr("value");
    var linkedCheckbox = URIToCheckedEntities[checkboxValue];
    
    var entityToBeRemoved = URIToEntityRecord[checkboxValue];

    if(!checkbox.is(':checked')){
        //console.log("Easy deselect checkbox is unclicked!");
        updateRowHighlighter(linkedCheckbox);
        removeUsedColor(entityToBeRemoved);
        removeEntityUnChecked(renderedObjects, entityToBeRemoved);                          
        removeLegendRow(linkedCheckbox);
        removeCheckBoxFromGlobalSet(linkedCheckbox);
        $(linkedCheckbox).attr('checked', false);
        checkIfColorLimitIsReached();
        displayLineGraphs();
        updateCounter();                
    }
});

$(".disabled-checkbox-event-receiver").live("click", function () {
    
    if ($(this).next().is(':disabled')) {

        createNotification("warning-notification", {
            title: 'Error',
            text: 'A Maximum 10 entities can be compared. Please remove some & try again.'
        }, {
            custom: true,
            expires: false
        });
    }
});

$("#copy-vis-viewlink-icon").live('click', function() {
	
	if ($("#copy-vis-viewlink").is(':visible')) {
		
		$("#copy-vis-viewlink").hide();
		
	} else {
		
		$("#copy-vis-viewlink").show();
		
		var linkTextBox = $("#copy-vis-viewlink input[type='text']");
		
		linkTextBox.val(getCurrentParameterVisViewLink());
		
		linkTextBox.select();
	}
	
});

function getCurrentParameterVisViewLink() {
	return location.protocol + "//" + location.host + COMPARISON_PARAMETERS_INFO[currentParameter].viewLink;
}

function performEntityCheckboxUnselectedActions(entity, checkboxValue, checkbox) {
    
    removeUsedColor(entity);
    removeEntityUnChecked(renderedObjects, entity);
    removeLegendRow(checkbox);
    removeCheckBoxFromGlobalSet(checkbox);
    
    checkbox.closest("tr").removeClass('datatablerowhighlight');

}

function performEntityCheckboxSelectedActions(entity, checkboxValue, checkbox) {
            
    getNextFreeColor(entity);

    //Generate the bar, checkbox and label for the legend.
    createLegendRow(entity, $("#bottom"));

    renderLineGraph(renderedObjects, entity);
    
    URIToCheckedEntities[checkboxValue] = checkbox;
    URIToCheckedEntities[checkboxValue].entity = entity;
    
    
    /*
     * To highlight the rows belonging to selected entities. 
     * */
    checkbox.closest("tr").addClass('datatablerowhighlight');
            
}

function performEntityCheckboxClickedRedrawActions() {

    setTickSizeOfAxes();
    checkIfColorLimitIsReached();
    displayLineGraphs();
    updateCounter();

}

var processJSONData = {
		
	isParentEntityAvailable: false,	
		
	setupGlobals: function(jsonContent) {
		$.each(jsonContent, function (index, val) {
	        
	    	/*
	    	 * We are checking if the "label" attribute is present, because that pertains to 
	    	 * data used for linegraph visualization.
	    	 * */
	    	if (val.label) {
	    		
		    	setOfLabels.push(val.label);
		        URIToEntityRecord[val.entityURI] = val;
		        if (val.lastCachedAtDateTime) {
		        	lastCachedAtDateTimes[lastCachedAtDateTimes.length] = val.lastCachedAtDateTime;
		        }
	    	} else if (val.subjectEntityLabel) {
	
	    		/*
	    		 * This is to set the drill-up visualization URLs.
	    		 * */
	    		$.each(val.parentURIToLabel, function(index, value) {
	    			
	    			$("a#subject-parent-entity-temporal-url").attr("href", getTemporalVisURL(index));
	    			
	    			$("a#subject-parent-entity-profile-url").attr("href", getVIVOURL(index));
	    			$("a#subject-parent-entity-profile-url").text(value);
	    			
	    			processJSONData.isParentEntityAvailable = true;
	    		});
	    	}
	    });
		
		if (processJSONData.isParentEntityAvailable) {
			$("#subject-parent-entity").show();
		} else {
			$("#subject-parent-entity").hide();
		}
	},	
	
	/* 
	 *  function to populate the labelToEntityRecord object with the
	 *  values from the json file and
	 *  dynamically generate checkboxes
	 */
	loadData: function(jsonData, dataTableParams) {
	    
		processJSONData.setupGlobals(jsonData);
		
	    prepareTableForDataTablePagination(jsonData, dataTableParams);
	    setEntityLevel(getEntityVisMode(jsonData));
	    
	    entityCheckboxOperatedOnEventListener();
	},
	
	/* 
	 *  function to populate the labelToEntityRecord object with the
	 *  values from the json file and
	 *  dynamically generate checkboxes
	 */
	reloadData: function(preselectedEntityURIs, jsonData) {
	    
		processJSONData.setupGlobals(jsonData);
		
	    reloadDataTablePagination(preselectedEntityURIs, jsonData);
	    setEntityLevel(getEntityVisMode(jsonData));
	    
	    $("a#csv").attr("href", csvDownloadURL);
	}
		
};

function entityCheckboxOperatedOnEventListener() {
	
    /*
     * When the elements in the paginated div
     * are clicked this event handler is called
     */
    $("input." + entityCheckboxSelectorDOMClass).live('click', function () {

        var checkbox = $(this);
        var checkboxValue = $(this).attr("value");
        var entity = URIToEntityRecord[checkboxValue];
        
        if (checkbox.is(':checked')) {
        
            performEntityCheckboxSelectedActions(entity, checkboxValue, checkbox);
        
        } else {
        
            performEntityCheckboxUnselectedActions(entity, checkboxValue, checkbox);        
        }
        
        performEntityCheckboxClickedRedrawActions();
    });
}

function renderTemporalGraphVisualization(parameters) {
	
    setupLoadingScreen(parameters.responseContainer);

    getTemporalGraphData(parameters.dataURL,
    					 parameters.bodyContainer,
                         parameters.errorContainer,
                         parameters.responseContainer);
}

/*
 * This method will setup the options for loading screen & then activate the 
 * loading screen.
 * */
function setupLoadingScreen(visContainerDIV) {
	
    $.blockUI.defaults.overlayCSS = { 
            backgroundColor: '#fff', 
            opacity:         1.0 
        };
        
    $.blockUI.defaults.css.width = '500px';
    $.blockUI.defaults.css.border = '0px';
    $.blockUI.defaults.css.top = '1%';
	
    visContainerDIV.block({
        message: '<div id="loading-data-container"><h3><img id="data-loading-icon" src="' + loadingImageLink 
        			+ '" />&nbsp;Loading data for <i>' 
        			+ organizationLabel
        			+ '</i></h3></div>'
    });
    
    clearTimeout(temporalGraphProcessor.loadingScreenTimeout);
    
    temporalGraphProcessor.loadingScreenTimeout = setTimeout(function() {
    	$("#loading-data-container")
    		.html('<h3><img id="refresh-page-icon" src="' 
    				+ refreshPageImageLink 
	    			+ '" />&nbsp;Data for <i>' + organizationLabel
	    			+ '</i> is now being refreshed. The visualization will load as soon as we are done computing, ' 
	    			+ 'or you can come back in a few minutes.</h3>')
	    	.css({'cursor': 'pointer'});
    	
    }, 10 * 1000);
}

/*
 * This function gets json data for temporal graph & after rendering removes the
 * loading message. It will also display the error container in case of any error.
 * */
function getTemporalGraphData(temporalGraphDataURL, 
										graphBodyDIV, 
										errorBodyDIV, 
										visContainerDIV) {
	
	if (!isDataRequestSentViaAJAX) {
		
		isDataRequestSentViaAJAX = true;
		
		$.ajax({
	        url: temporalGraphDataURL,
	        dataType: "json",
	        timeout: 5 * 60 * 1000,
	        success: function (data) {
	
	            if (data.error) {
	            	graphBodyDIV.remove();
	            	errorBodyDIV.show();
	            	visContainerDIV.unblock();
	            } else {
	            	graphBodyDIV.show();
	            	errorBodyDIV.remove();
	                temporalGraphProcessor.initiateTemporalGraphRenderProcess(graphContainer, data);
	                visContainerDIV.unblock();
	            }
	        },
	       complete: function() {
	        	isDataRequestSentViaAJAX = false;
	        }
	    });
	
	}
	
}

var lastCachedAtDateTimeParser = {
		
	getDateObjectFromRawXSDateTimeString: function(rawDateTimeString) {
	
		var dateTime = rawDateTimeString.split("T", 2);
		var date = dateTime[0].split("-");
		var time = dateTime[1].split(":");
		
		return new Date(date[0], parseInt(date[1], 10) -1, date[2], time[0], time[1], 0);
	},
	
	getReadableDateString: function(dateObject) {
		
		if (typeof dateObject === "string") {
			dateObject = this.getDateObjectFromRawXSDateTimeString(dateObject);
		}
		
		var day = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
		var month = ['January','February','March','April','May','June','July','August','September','October','November'];
		
		return day[dateObject.getDay()] + ", " + month[dateObject.getMonth()] + " " + dateObject.getDate();
	},
	
	ascendingDateSorter: function(rawDateStringA, rawDateStringB) {
		
		var dateA = lastCachedAtDateTimeParser.getDateObjectFromRawXSDateTimeString(rawDateStringA); 
		var dateB = lastCachedAtDateTimeParser.getDateObjectFromRawXSDateTimeString(rawDateStringB);
		return dateA-dateB; //sort by date ascending
   }
		
};

var entitySelector = {
		
	manuallyTriggerSelectOnDataTableCheckbox: function(checkbox) {
	
		checkbox.attr('checked', true);
	    
	    var checkboxValue = checkbox.attr("value");
	    var entity = URIToEntityRecord[checkboxValue];
	    
	    performEntityCheckboxSelectedActions(entity, checkboxValue, checkbox);
	    performEntityCheckboxClickedRedrawActions();
	
	}
}

temporalGraphProcessor = {
		
	loadingScreenTimeout: '',
		
	initiateTemporalGraphRenderProcess: function(givenGraphContainer, jsonData) {
		
		this.dataTableParams = {
			searchBarParentContainerDIVClass : "searchbar",
			paginationContainerDIVClass : "paginatedtabs"
		};
		
		
		/*
         * initial display of the grid when the page loads 
         * */ 
        init(givenGraphContainer);

        /*
         * render the temporal graph per the sent content. 
         * */
        processJSONData.loadData(jsonData, this.dataTableParams);
        
        lastCachedAtDateTimes.sort(lastCachedAtDateTimeParser.ascendingDateSorter);
        
        /*
         * This will make sure that top 3 entities are selected by default when the page loads.
        */      
        $.each($("input." + entityCheckboxSelectorDOMClass), function(index, checkbox) {
                
            if (index > 2) {
                return false;
            }
        
            entitySelector.manuallyTriggerSelectOnDataTableCheckbox($(this));
                    
        });

        if ($("#incomplete-data-disclaimer").length > 0 && lastCachedAtDateTimes.length > 0) {
        	$("#incomplete-data-disclaimer").attr(
        			"title", 
        			$("#incomplete-data-disclaimer").attr("title") + " as of " 
        			+ lastCachedAtDateTimeParser.getReadableDateString(lastCachedAtDateTimes[0])); 
        }
	},
	
	redoTemporalGraphRenderProcess: function(givenGraphContainer, jsonData) {
		
		var currentSelectedEntityURIs = [];
		
		$.each(URIToCheckedEntities, function(index, entity){
			currentSelectedEntityURIs.push(index);
		});
		
		clearRenderedObjects();
		initConstants();
		
		/*
         * initial display of the grid when the page loads 
         * */ 
        init(givenGraphContainer);

        /*
         * render the temporal graph per the sent content. 
         * */
        processJSONData.reloadData(currentSelectedEntityURIs, jsonData);
        
        lastCachedAtDateTimes.sort(lastCachedAtDateTimeParser.ascendingDateSorter);

        if (currentSelectedEntityURIs.length > 0) {

        	$.each(currentSelectedEntityURIs, function(index, uri) {

        		var targetPrevSelectedCheckbox = $('input.' + entityCheckboxSelectorDOMClass + '[value="' + uri + '"]');
        		
        		if (targetPrevSelectedCheckbox.length > 0) {

        			entitySelector.manuallyTriggerSelectOnDataTableCheckbox(targetPrevSelectedCheckbox);
        		}
        	});
        } else {
	        /*
	         * This will make sure that top 3 entities are selected by default when the page loads.
	        */      
	        $.each($("input." + entityCheckboxSelectorDOMClass), function(index, checkbox) {
	                
                if (index > 2) {
                    return false;
                }
                entitySelector.manuallyTriggerSelectOnDataTableCheckbox($(this));
	        });
        }
        
        if ($("#incomplete-data-disclaimer").length > 0 && lastCachedAtDateTimes.length > 0) {
        	
        	var disclaimerText = "This information is based solely on " 
        							+ COMPARISON_PARAMETERS_INFO[currentParameter].value
        							+ " which have been loaded into the VIVO system"
        							+ " as of " + lastCachedAtDateTimeParser.getReadableDateString(lastCachedAtDateTimes[0]);
        	
        	$("#incomplete-data-disclaimer").attr(
        			"title", disclaimerText); 
        }
        $("#copy-vis-viewlink input[type='text']").val(getCurrentParameterVisViewLink());
	}	
}