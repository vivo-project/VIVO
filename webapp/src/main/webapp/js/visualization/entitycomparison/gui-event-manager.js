/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function() {

    $.extend(this, i18nStringsGuiEvents);
	
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
    
    /*
     * When the intra-entity parameters are clicked,
     * update the status accordingly.   
     */         

    $("select.comparisonValues").change(function() {

        var selectedValue = $("select.comparisonValues option:selected").val();
        
        var selectedDataURL;
        
        var oldParameter = currentParameter;
        
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
                	options.bodyContainer.hide();
                	
                	/*
                	 * When we reload data we would be reusing the already generated "error container" div. 
                	 * So below is used to replace text taht is specific to the errors that would have caused
                	 * due to the original parameters.
                	 * */
                	var alternateVisInfo = COMPARISON_PARAMETERS_INFO[currentParameter].value + " " 
                							+ i18nStringsGuiEvents.temporalGraphCapped + "&nbsp;"
                							+ '<span id="noPubsOrGrants-span">|&nbsp;'
                							+ '<a  href="' + COMPARISON_PARAMETERS_INFO[oldParameter].viewLink + '">'
                							+ i18nStringsGuiEvents.viewString + ' ' 
                							+ COMPARISON_PARAMETERS_INFO[oldParameter].pluralName 
                							+ i18nStringsGuiEvents.temporalGraphLower + '</a></span>'
                	
                	options.errorContainer.find("#alternative-vis-info").html(alternateVisInfo);

                	options.errorContainer
                			.find("#comparison-parameter-unavailable-label")
                			.text(COMPARISON_PARAMETERS_INFO[currentParameter].pluralName);
                	
                	options.errorContainer.show();
                	options.responseContainer.unblock();
                } else {
                	
                	options.bodyContainer.show();
                	options.errorContainer.hide();
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
            text: i18nStringsGuiEvents.entityMaxNote
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

$(".filter-option").live('click', function() {
	
	if (!$(this).hasClass('active-filter')) {
		
		if ($(this).attr('id') === 'people-filter') {
			
			$("#organizations-filter").removeClass('active-filter');
      $("#no-filter").removeClass('active-filter');
			temporalGraphProcessor.currentSelectedFilter = "PEOPLE";
			
		} else if ($(this).attr('id') === 'organizations-filter') {
			
			$("#people-filter").removeClass('active-filter');
      $("#no-filter").removeClass('active-filter');
			temporalGraphProcessor.currentSelectedFilter = "ORGANIZATIONS";
		} 
    else if ($(this).attr('id') === 'no-filter') {
			
			$("#people-filter").removeClass('active-filter');
      $("#organizations-filter").removeClass('active-filter');
			temporalGraphProcessor.currentSelectedFilter = "NONE";
		} 
		
		$(this).addClass('active-filter');
		
		temporalGraphProcessor.dataTable.fnDraw();
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
	
	currentEntityLevel: i18nStringsGuiEvents.organizationsCappedString,
	

	/**
	 * This is used to find out the sum of all the counts of a particular entity. This is
	 * especially useful to render the bars below the line graph where it doesnt matter if
	 * a count has any associated year to it or not.
	 * @returns sum{values}.
	 */
	aggregateActivityCounts: function(allActivities) {

		var known = 0;
		var unknown = 0;
		var currentYear = 0;

		$.each(allActivities, function(index, data){
			
			if (this[0] === -1) {
				unknown += this[1];
			} else {
				known += this[1];
				
				if (this[0] === globalDateObject.getFullYear()) {
					currentYear += this[1];
				}
			}
			
		});

		sum = {
			knownYearCount: known,
			unknownYearCount: unknown,
			currentYearCount: currentYear
		};
		
		return sum;
	},
	
	setupGlobals: function(jsonContent, resetFilter) {
		
		var entityLevels = new Array();
		var entityActivityCount = {
			PERSON: 0,
			ORGANIZATION: 0
		};
		
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
		        
		        /*
		         * Setup value to be used to determine whether to show people or organizations filter,
		         * by default.
		         * */
		        var activityCountInfoForEntity = processJSONData.aggregateActivityCounts(val.data);
		        
		        URIToEntityRecord[val.entityURI].activityCount = activityCountInfoForEntity;
		    	
		    	entityActivityCount[val.visMode] += 
		    		activityCountInfoForEntity.knownYearCount 
	    				+ activityCountInfoForEntity.unknownYearCount;
		    	
		    	/*
		    	 * Setup the entity level
		    	 * */
		    	if (val.visMode ===  "PERSON"){
					entityLevels.push(i18nStringsGuiEvents.peopleCappedString);
				} else if (val.visMode ===  "ORGANIZATION"){
					entityLevels.push(i18nStringsGuiEvents.organizationsCappedString);
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
		
		/*
		 * We do not want to change the current filter setting if the user has changed the parameter
		 * from the dropdown box. Only when the temporal vis is loaded directly we want to make sure 
		 * that the group of entites that has the most activity is shown.
		 * */
		if (resetFilter) {
			if (entityActivityCount.ORGANIZATION >= entityActivityCount.PERSON) {
				
				temporalGraphProcessor.currentSelectedFilter = "ORGANIZATIONS";
				
				$("#organizations-filter").addClass("active-filter");
				$("#people-filter").removeClass("active-filter");
				
				
			} else {
				
				temporalGraphProcessor.currentSelectedFilter = "PEOPLE";
				
				$("#people-filter").addClass("active-filter");
				$("#organizations-filter").removeClass("active-filter");
				
			}
		}
		
		if (processJSONData.isParentEntityAvailable) {
			$("#subject-parent-entity").show();
		} else {
			$("#subject-parent-entity").hide();
		}
		
		var uniqueEntityLevels = $.unique(entityLevels);
		
		/*
		 * This case is when organizations & people are mixed because both are directly attached
		 * to the parent organization. 
		 * */
		if (uniqueEntityLevels.length > 1) {
			processJSONData.currentEntityLevel = i18nStringsGuiEvents.organizationsAndPeople;
			$("#people-organizations-filter").show();
		} else if (uniqueEntityLevels.length === 1) {
			processJSONData.currentEntityLevel = uniqueEntityLevels[0];
			$("#people-organizations-filter").hide();
		} else {
			/* To provide graceful degradation set entity level to a default error message.*/
			processJSONData.currentEntityLevel = i18nStringsGuiEvents.levelUndefinedError;
		}
	},	
	
	/* 
	 *  function to populate the labelToEntityRecord object with the
	 *  values from the json file and
	 *  dynamically generate checkboxes
	 */
	loadData: function(jsonData, dataTableParams) {
		
		processJSONData.setupGlobals(jsonData, true);
		
		temporalGraphProcessor.dataTable = prepareTableForDataTablePagination(jsonData, dataTableParams);
	    
	    setEntityLevel(processJSONData.currentEntityLevel);
	    
	    entityCheckboxOperatedOnEventListener();
	},
	
	/* 
	 *  function to populate the labelToEntityRecord object with the
	 *  values from the json file and
	 *  dynamically generate checkboxes
	 */
	reloadData: function(preselectedEntityURIs, jsonData) {
		
		processJSONData.setupGlobals(jsonData);
		
		temporalGraphProcessor.dataTable = reloadDataTablePagination(preselectedEntityURIs, jsonData);
	    
	    setEntityLevel(processJSONData.currentEntityLevel);
	    
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
        
        temporalGraphProcessor.isDefaultSelectionsMaintained = false;
        
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
    $.blockUI.defaults.css.top = '10px';
    
    visContainerDIV.block({
        message: '<div id="loading-data-container"><h3><img id="data-loading-icon" src="' + loadingImageLink 
        			+ '" />&nbsp;' + i18nStringsGuiEvents.loadingDataFor + ' <i>' 
        			+ organizationLabel
        			+ '</i></h3></div>'
    });
    
    clearTimeout(temporalGraphProcessor.loadingScreenTimeout);
    
    temporalGraphProcessor.loadingScreenTimeout = setTimeout(function() {
    	$("#loading-data-container")
    		.html('<h3><img id="refresh-page-icon" src="' 
    				+ refreshPageImageLink 
	    			+ '" />&nbsp;' + i18nStringsGuiEvents.dataForString + ' <i>' + organizationLabel
	    			+ '</i> ' + i18nStringsGuiEvents.refreshingDataMsg 
	    			+ '</h3>')
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
	            	graphBodyDIV.hide();
	            	errorBodyDIV.show();
	            	visContainerDIV.unblock();
	            } else {
	            	
	            	graphBodyDIV.show();
	            	errorBodyDIV.hide();
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
		
		var day = [i18nStringsGuiEvents.sundayString, i18nStringsGuiEvents.mondayString, i18nStringsGuiEvents.tuesdayString, i18nStringsGuiEvents.wednesdayString, i18nStringsGuiEvents.thursdayString, i18nStringsGuiEvents.fridayString, i18nStringsGuiEvents.saturdayString];
		var month = [i18nStringsGuiEvents.januaryString, i18nStringsGuiEvents.februaryString, i18nStringsGuiEvents.marchString, i18nStringsGuiEvents.aprilString, i18nStringsGuiEvents.mayString, i18nStringsGuiEvents.juneString, i18nStringsGuiEvents.julyString, i18nStringsGuiEvents.augustString, i18nStringsGuiEvents.septemberString, i18nStringsGuiEvents.octoberString, i18nStringsGuiEvents.novemberString];
		
		return month[dateObject.getMonth()] + " " + dateObject.getDate() + ", " + dateObject.getFullYear() + ".";
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
		
	isDefaultSelectionsMaintained: true,
		
	loadingScreenTimeout: '',
	
	currentSelectedFilter: 'ORGANIZATIONS',
	
	dataTable: '',
		
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
        	
        	var disclaimerText = i18nStringsGuiEvents.disclaimerTextOne + " " 
				+ COMPARISON_PARAMETERS_INFO[currentParameter].value
				+ " " + i18nStringsGuiEvents.disclaimerTextTwo + " "
				+ lastCachedAtDateTimeParser.getReadableDateString(lastCachedAtDateTimes[0]);
        	
        	$("#incomplete-data-disclaimer").attr(
        			"title", 
        			disclaimerText); 
        }
	},
	
	redoTemporalGraphRenderProcess: function(givenGraphContainer, jsonData) {
		
		var currentSelectedEntityURIs = [];
		
		/*
		 * We want to make sure that the currently selected entities are preserved only if they 
		 * were somehow manipulated by the users.
		 * */
		if (!temporalGraphProcessor.isDefaultSelectionsMaintained) {
			$.each(URIToCheckedEntities, function(index, entity){
				currentSelectedEntityURIs.push(index);
			});	
		}
		
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
        	 * We have to redraw the table so that top 3 entities are selected. fnDraw() triggers sorting of the 
        	 * table and other filters. 
        	 * */
        	temporalGraphProcessor.dataTable.fnDraw();
        	
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
        
        /*
         * Table has to be redrawn now & not later to avoid checkboxes not being selected, which is
         * caused if they are not visible at that point of time. 
         * */
        temporalGraphProcessor.dataTable.fnDraw();
        
        if ($("#incomplete-data-disclaimer").length > 0 && lastCachedAtDateTimes.length > 0) {
        	
        	var disclaimerText = i18nStringsGuiEvents.disclaimerTextOne + " " 
        							+ COMPARISON_PARAMETERS_INFO[currentParameter].value
        							+ i18nStringsGuiEvents.disclaimerTextTwo + " "
        							+ lastCachedAtDateTimeParser.getReadableDateString(lastCachedAtDateTimes[0]);
        	
        	$("#incomplete-data-disclaimer").attr(
        			"title", disclaimerText); 
        }
        $("#copy-vis-viewlink input[type='text']").val(getCurrentParameterVisViewLink());
	}	
}
