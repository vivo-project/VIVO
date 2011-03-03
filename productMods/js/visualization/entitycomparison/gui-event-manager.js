/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function() {

	/*
	 * This will set intitial values of the constants present in constants.js
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

    $("select.comparisonValues").change(function(){

        var selectedValue = $("select.comparisonValues option:selected").val();
        
        var selectedParameter;
        
        $.each(COMPARISON_PARAMETERS_INFO, function(index, parameter) {
        	
            if (parameter.value === selectedValue) {
            	selectedParameter = parameter;
                window.location = parameter.viewLink;
            }
        	
        });
        
        //$("#body").empty().html("<div id='loading-comparisons'>Loading " + selectedValue + "&nbsp;&nbsp;<img src='" + loadingImageLink + "' /></div>");
        
        /*
         * This piece of code is not executed at all because the redirect happens before there is a chance 
         * to render the below contents.
         * */
        
        /*
        
        $("#comparisonParameter").text("Total Number of " + selectedValue);
        $('#yaxislabel').html("Number of " + selectedValue).mbFlipText(false);
        $('#yaxislabel').css("color", "#595B5B");
        $('#comparisonHeader').html(selectedValue).css('font-weight', 'bold');
        
        
        */

    });
    
});
        
//click event handler for clear button
$("a.clear-selected-entities").live('click', function(){
    clearRenderedObjects();
}); 

$("input[type=checkbox].easyDeselectCheckbox").live('click', function(){
    
    var checkbox = $(this);
    var checkboxValue = $(this).attr("value");
    var linkedCheckbox = labelToCheckedEntities[checkboxValue];
    var entityToBeRemoved = labelToEntityRecord[checkboxValue];

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
    labelToCheckedEntities[checkboxValue] = checkbox;
    labelToCheckedEntities[checkboxValue].entity = entity;
    
//            console.log(labelToCheckedEntities[checkboxValue], entity);
    
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

/* 
 *  function to populate the labelToEntityRecord object with the
 *  values from the json file and
 *  dynamically generate checkboxes
 */
function loadData(jsonData, dataTableParams) {

    $.each(jsonData, function (index, val) {
        setOfLabels.push(val.label);
        labelToEntityRecord[val.label] = val;
    });
    
    prepareTableForDataTablePagination(jsonData, dataTableParams);
    setEntityLevel(getEntityVisMode(jsonData));
    
    entityCheckboxOperatedOnEventListener();
    
}

function entityCheckboxOperatedOnEventListener() {
	
    /*
     * When the elements in the paginated div
     * are clicked this event handler is called
     */
    $("input." + entityCheckboxSelectorDOMClass).live('click', function () {

        var checkbox = $(this);
        var checkboxValue = $(this).attr("value");
        var entity = labelToEntityRecord[checkboxValue];
        
        if (checkbox.is(':checked')) {
        
            performEntityCheckboxSelectedActions(entity, checkboxValue, checkbox);
        
        } else {
        
            performEntityCheckboxUnselectedActions(entity, checkboxValue, checkbox);        

        }
        
        performEntityCheckboxClickedRedrawActions();

    });
	
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
    $.blockUI.defaults.css.top = '15%';
	
    visContainerDIV.block({
        message: '<h3><img src="' + loadingImageLink 
        			+ '" />&nbsp;Loading data for <i>' 
        			+ organizationLabel
        			+ '</i></h3>'
    });
	
}

/*
 * This function gets json data for temporal graph & after rendering removes the
 * loading message. It will also display the error container in case of any error.
 * */
function getTemporalGraphData(temporalGraphDataURL, 
										graphBodyDIV, 
										errorBodyDIV, 
										visContainerDIV) {
	
	$.ajax({
        url: temporalGraphDataURL,
        dataType: "json",
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
        }
    });
	
}

temporalGraphProcessor = {
		
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
        loadData(jsonData, this.dataTableParams);
        
        /*
         * This will make sure that top 3 entities are selected by default when the page loads.
        */      
        $.each($("input." + entityCheckboxSelectorDOMClass), function(index, checkbox) {
                
                    if (index > 2) {
                        return false;
                    }
                
                    $(this).attr('checked', true);
                    
                    var checkboxValue = $(this).attr("value");
                    var entity = labelToEntityRecord[checkboxValue];
                    
                    performEntityCheckboxSelectedActions(entity, checkboxValue, $(this));
                    
                    performEntityCheckboxClickedRedrawActions();
                    
                });
		
	}	
		
}