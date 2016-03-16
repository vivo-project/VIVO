/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$.extend(this, i18nStringsUtil);

(function ($) {

    $.fn.dataTableExt.oPagination.gmail_style = {

    		
        "fnInit": function (oSettings, nPaging, fnCallbackDraw) {
            //var nInfo = document.createElement( 'div' );
            var nFirst = document.createElement('span');
            var nPrevious = document.createElement('span');
            var nNext = document.createElement('span');
            var nLast = document.createElement('span');

/*
			nFirst.innerHTML = oSettings.oLanguage.oPaginate.sFirst;
			nPrevious.innerHTML = oSettings.oLanguage.oPaginate.sPrevious;
			nNext.innerHTML = oSettings.oLanguage.oPaginate.sNext;
			nLast.innerHTML = oSettings.oLanguage.oPaginate.sLast;
			*/

            nFirst.innerHTML = "<span class='small-arrows'>&laquo;</span> <span class='paginate-nav-text'>" 
                                + i18nStringsUtil.firstString + "</span>";
            nPrevious.innerHTML = "<span class='small-arrows'>&lsaquo;</span> <span class='paginate-nav-text'>"
                                + i18nStringsUtil.previousString + "</span>";
            nNext.innerHTML = "<span class='paginate-nav-text'>" + i18nStringsUtil.nextString 
                                + "</span><span class='small-arrows'>&rsaquo;</span>";
            nLast.innerHTML = "<span class='paginate-nav-text'>" + i18nStringsUtil.lastString 
                                + "</span><span class='small-arrows'>&raquo;</span>";

            var oClasses = oSettings.oClasses;
            nFirst.className = oClasses.sPageButton + " " + oClasses.sPageFirst;
            nPrevious.className = oClasses.sPageButton + " " + oClasses.sPagePrevious;
            nNext.className = oClasses.sPageButton + " " + oClasses.sPageNext;
            nLast.className = oClasses.sPageButton + " " + oClasses.sPageLast;

            //nPaging.appendChild( nInfo );
            nPaging.appendChild(nFirst);
            nPaging.appendChild(nPrevious);
            nPaging.appendChild(nNext);
            nPaging.appendChild(nLast);

            $(nFirst).click(function () {
                if (oSettings.oApi._fnPageChange(oSettings, "first")) {
                    fnCallbackDraw(oSettings);
                }
            });

            $(nPrevious).click(function () {
                if (oSettings.oApi._fnPageChange(oSettings, "previous")) {
                    fnCallbackDraw(oSettings);
                }
            });

            $(nNext).click(function () {
                if (oSettings.oApi._fnPageChange(oSettings, "next")) {
                    fnCallbackDraw(oSettings);
                }
            });

            $(nLast).click(function () {
                if (oSettings.oApi._fnPageChange(oSettings, "last")) {
                    fnCallbackDraw(oSettings);
                }
            });

            /* Take the brutal approach to cancelling text selection */
            $('span', nPaging).bind('mousedown', function () {
                return false;
            }).bind('selectstart', function () {
                return false;
            });

            /* ID the first elements only */
            if (oSettings.sTableId !== '' && typeof oSettings.aanFeatures.p == "undefined") {
                nPaging.setAttribute('id', oSettings.sTableId + '_paginate');
                nFirst.setAttribute('id', oSettings.sTableId + '_first');
                nPrevious.setAttribute('id', oSettings.sTableId + '_previous');
                //nInfo.setAttribute( 'id', 'infoContainer' );
                nNext.setAttribute('id', oSettings.sTableId + '_next');
                nLast.setAttribute('id', oSettings.sTableId + '_last');
            }
        },

/*
		 * Function: oPagination.full_numbers.fnUpdate
		 * Purpose:  Update the list of page buttons shows
		 * Returns:  -
 		 * Inputs:   object:oSettings - dataTables settings object
		 *           function:fnCallbackDraw - draw function to call on page change
		 */
        "fnUpdate": function (oSettings, fnCallbackDraw) {
            if (!oSettings.aanFeatures.p) {
                return;
            }

            var iPageCount = 5;
            var iPageCountHalf = Math.floor(iPageCount / 2);
            var iPages = Math.ceil((oSettings.fnRecordsDisplay()) / oSettings._iDisplayLength);
            var iCurrentPage = Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength) + 1;
            var iStartButton, iEndButton, i, iLen;
            var oClasses = oSettings.oClasses;
            
            /* Pages calculation */
            if (iPages < iPageCount) {
                iStartButton = 1;
                iEndButton = iPages;
            } else {
                if (iCurrentPage <= iPageCountHalf) {
                    iStartButton = 1;
                    iEndButton = iPageCount;
                } else {
                    if (iCurrentPage >= (iPages - iPageCountHalf)) {
                        iStartButton = iPages - iPageCount + 1;
                        iEndButton = iPages;
                    } else {
                        iStartButton = iCurrentPage - Math.ceil(iPageCount / 2) + 1;
                        iEndButton = iStartButton + iPageCount - 1;
                    }
                }
            }

            /* Loop over each instance of the pager */
            var an = oSettings.aanFeatures.p;
            var anButtons, anStatic, nPaginateList;
            var fnClick = function () { /* Use the information in the element to jump to the required page */
                var iTarget = (this.innerHTML * 1) - 1;
                oSettings._iDisplayStart = iTarget * oSettings._iDisplayLength;
                fnCallbackDraw(oSettings);
                return false;
            };
            var fnFalse = function () {
                return false;
            };

            for (i = 0, iLen = an.length; i < iLen; i++) {
                if (an[i].childNodes.length === 0) {
                    continue;
                }

                /* Update the 'premanent botton's classes */
                anButtons = an[i].getElementsByTagName('span');
                anStatic = [
                anButtons[0], anButtons[1], anButtons[anButtons.length - 2], anButtons[anButtons.length - 1]];
                $(anStatic).removeClass(oClasses.sPageButton + " " + oClasses.sPageButtonActive + " " + oClasses.sPageButtonStaticDisabled);
                if (iCurrentPage == 1) {
                    anStatic[0].className += " " + oClasses.sPageButtonStaticDisabled;
                    anStatic[1].className += " " + oClasses.sPageButtonStaticDisabled;
                } else {
                    anStatic[0].className += " " + oClasses.sPageButton;
                    anStatic[1].className += " " + oClasses.sPageButton;
                }

                if (iPages === 0 || iCurrentPage == iPages || oSettings._iDisplayLength == -1) {
                    anStatic[2].className += " " + oClasses.sPageButtonStaticDisabled;
                    anStatic[3].className += " " + oClasses.sPageButtonStaticDisabled;
                } else {
                    anStatic[2].className += " " + oClasses.sPageButton;
                    anStatic[3].className += " " + oClasses.sPageButton;
                }
            }
            
            
            if (iPages <= 1) {
            	$("." + temporalGraphProcessor.dataTableParams.paginationContainerDIVClass).hide();
            } else {
            	$("." + temporalGraphProcessor.dataTableParams.paginationContainerDIVClass).show();
            }
        }
    };
    
    $.fn.ellipsis = function () {
        return this.each(function () {
            var el = $(this);

            if (el.css("overflow") == "hidden") {

                var text = el.html();

                var multiline = el.hasClass('multiline');
                var t = $(this.cloneNode(true)).hide().css('position', 'absolute').css('overflow', 'visible').width(multiline ? el.width() : 'auto').height(multiline ? 'auto' : el.height());

                el.after(t);

                function height() {
                    return t.height() > el.height();
                };

                function width() {
                    return t.width() > el.width();
                };

                var func = multiline ? height : width;


                while (text.length > 0 && func()) {
                    text = text.substr(0, text.length - 1);
                    t.html(text + "...");
                }

                el.html(t.html());
                t.remove();
            }
        });
    };
})(jQuery);


var DatatableCustomFilters = {
	
	peopleOrOrganizations: function(oSettings, aData, iDataIndex) {

		/*
		 * We know for a fact that the unique identifier for each row is the value for the checkbox, 
		 * that is found in the first column for each row.
		 * */
		var row_data = aData[0];
		var entityURI = $(row_data).filter("input[type=checkbox]").val();
		
		var currentEntityVisMode = URIToEntityRecord[entityURI].visMode;

    if (temporalGraphProcessor.currentSelectedFilter === "NONE") {
      return true;
    }
    else if (currentEntityVisMode === "ORGANIZATION" 
				&& temporalGraphProcessor.currentSelectedFilter === "ORGANIZATIONS") {
			return true;
		} else if (currentEntityVisMode === "PERSON" 
			&& temporalGraphProcessor.currentSelectedFilter === "PEOPLE") {
			return true;
		} else {
//			console.log(entityURI);
			return false;
		}
		
		return true;
	}	
		
};

/**

 * init sets some initial options for the default graph. i.e for when the page
 * is initially loaded or when its refreshed or when all the checkboxes on the
 * page are unchecked.
 * 
 * @param graphContainer
 *            is the div that contains the main graph.
 */
function init(graphContainer) {
	
	var optionSelected = $("select.comparisonValues option:selected").val();
	// TODO: make use of the id on the select field instead of a generic one.
	$("#comparisonParameter").text(i18nStringsUtil.totalNumberOf + " " + optionSelected);
	$('#yaxislabel').html(i18nStringsUtil.numberOf + " " + optionSelected).mbFlipText(false);
	$('#comparisonHeader').html(optionSelected).css('font-weight', 'bold');
	$('#legend-unknown-bar-text').text(toTitleCase(COMPARISON_PARAMETERS_INFO[currentParameter].name) + " " + i18nStringsUtil.withUnknownYear);
	$('#legend-known-bar-text').text(toTitleCase(COMPARISON_PARAMETERS_INFO[currentParameter].name) + " " + i18nStringsUtil.withKnownYear);
	$('#legend-current-year-bar-text').text(toTitleCase(COMPARISON_PARAMETERS_INFO[currentParameter].name) + " " + i18nStringsUtil.fromIncompleteYear);
	
	var defaultFlotOptions = {
			xaxis : {
				min : globalDateObject.getFullYear() - 10,
				max : globalDateObject.getFullYear() - 1,
				tickDecimals : 0,
				tickSize : 2
			},
			yaxis: {
				tickDecimals : 0,
				min : 0,
				max: 5
			},
			grid: {
				borderColor : "#D9D9D9"
			}
			
	};

	/*
	 * [[]] is an empty 2D array object. $.plot is passed this for the default
	 * behavior. Ex.When the page initially loads, or when no graphs are present
	 * on the webpage.
	 */

	var initialDataObject = [ [] ];
	$.plot(graphContainer, initialDataObject, defaultFlotOptions);
}

/**
 * unStuffZerosFromLineGraphs removes the previously stuffed zero values. r is
 * the current data object. s is the current min and max {year} values. All the
 * datapoints < curr_min{year} && > > curr_max{year} are removed, so that they
 * don't show up on the graph
 * 
 * @param {Object}
 *            jsonObject
 * @param {Object}
 *            arrayOfMinAndMaxYears
 * @returns jsonObject with modified data points.
 */

//TODO: side-effect year.
function unStuffZerosFromLineGraphs(jsonObject, year) {

	calcZeroLessMinAndMax(jsonObject, year);
	var currentMinYear = year.globalMin, currentMaxYear = year.globalMax;
	
	var normalizedYearRange = getNormalizedYearRange();

	$.each(jsonObject,
			function(key, val) {
				var i = 0;
				for (i = 0; i < val.data.length; i++) {
					if (((val.data[i][0] < normalizedYearRange.normalizedMinYear) || (val.data[i][0] > normalizedYearRange.normalizedMaxYear))
							&& val.data[i][1] == 0) {

						val.data.splice(i, 1);
						i--;
					} else {
						continue;
					}
				}
			});
}

/**
 * while unStuffZerosFromLineGraphs is for a group of data objects,
 * unStuffZerosFromLineGraph is for a single data object. It removes zeroes from
 * the single object passed as parameter.
 * 
 * @param {Object}
 *            jsonObject
 */
function unStuffZerosFromLineGraph(jsonObject) {
	var i = 0;
	for (i = 0; i < jsonObject.data.length; i++) {
		if (jsonObject.data[i][1] == 0) {
			jsonObject.data.splice(i, 1);
			i--;
		}
	}
}


/**
 * This is used to normalize the year range for the currently selected entities to always 
 * display the last 10 years worth of data points. 
 * 
 */
function getNormalizedYearRange() {
	
	/*
	 * This is done to make sure that at least last 10 years worth of data points 
	 * can be displayed.
	 * */
	if (globalDateObject.getFullYear() < year.globalMax) {
		
		inferredMaxYear = year.globalMax;
		
	} else {
		
		inferredMaxYear = globalDateObject.getFullYear();
	}
	
	if (globalDateObject.getFullYear() - 9 > year.globalMin) {
		
		inferredMinYear = year.globalMin;
		
	} else {
		
		inferredMinYear = globalDateObject.getFullYear() - 9;
	}
	
	return {
		normalizedMinYear: inferredMinYear,
		normalizedMaxYear: inferredMaxYear,
		normalizedRange: inferredMaxYear - inferredMinYear 
	};
}

/**
 * stuffZerosIntoLineGraphs is used to fill discontinuities in data points. For
 * example, if a linegraph has the following data points [1990,
 * 2],[1992,3],[1994, 5],[1996,5],[2000,4],[2001,1]. stuffZerosIntoLineGraphs
 * inserts [1991,0],[1993,0],1995,0]..and so on. It also inserts zeroes at the
 * beginning and the end if the max and min{year} of the current linegraph fall
 * in between the global max and min{year}
 * 
 * @param {Object}
 *            jsonObject
 * @param {Object}
 *            arrayOfMinAndMaxYears
 * @returns jsonObject with stuffed data points.
 */
function stuffZerosIntoLineGraphs(jsonObject, year) {
	
	calcZeroLessMinAndMax(jsonObject, year);

	var normalizedYearRange = getNormalizedYearRange();
	
	$.each(jsonObject,
			function(key, val) {
		var position = normalizedYearRange.normalizedMinYear, i = 0;

		for (i = 0; i < normalizedYearRange.normalizedRange + 1; i++) {

			if (val.data[i]) {

				if (val.data[i][0] != position
						&& position <= normalizedYearRange.normalizedMaxYear) {
					val.data.splice(i, 0, [ position, 0 ]);
				}
			}
			else {
				val.data.push( [ position, 0 ]);
			}
			position++;
		}
	});
}
/**
 * During runtime, when the user checks/unchecks a checkbox, the zeroes have to
 * be inserted and removed dynamically. This function calculates the max{year}
 * and min{year} among all the linegraphs present on the graph at a particular
 * instance in time .
 * 
 * @param {Object}
 *            jsonObject
 * @returns an array of current min and max years.
 */
function calcZeroLessMinAndMax(jsonObject, year) {

	var validYearsInData = new Array();

	$.each(jsonObject, function(key, val) {

		for (i = 0; i < val.data.length; i++) {
			
			/*
			 * TO make sure that,
			 * 		1. Not to consider years that dont have any counts attached to it.
			 * 		2. Not to consider unknown years indicated by "-1". 
			 * */
			if (val.data[i][1] != 0 && val.data[i][0] != -1) {
				validYearsInData.push(val.data[i][0]);
			}
		}
		
	});

	year.globalMin = Math.min.apply(Math, validYearsInData);
	year.globalMax = Math.max.apply(Math, validYearsInData);
	
}

/**
 * z is an an object with two properties label and data. data is of the form
 * [year,value] This function returns the min and max values of all such years.
 * 
 * @param {Object}
 *            jsonObject
 * @returns [minYear, maxYear]
 */
function calcMinandMaxYears(jsonObject, year) {
	
	var validYearsInData = new Array();

	$.each(jsonObject, function(key, val) {

		for (i = 0; i < val.data.length; i++) {
			
			/*
			 * TO make sure that,
			 * 		1. Not to consider years that dont have any counts attached to it.
			 * 		2. Not to consider unknown years indicated by "-1". 
			 * */
			if (val.data[i][1] != 0 && val.data[i][0] != -1) {
				validYearsInData.push(val.data[i][0]);
			}
		}
		
	});
	year.min = Math.min.apply(Math, validYearsInData);
	year.max = Math.max.apply(Math, validYearsInData);
}

/**
 * This function returns the max from the counts of all the entities. Mainly used to 
 * normalize the width of bar below the line graph, also known as legend row.

 * @returns maxCount
 */
function calcMaxOfComparisonParameter(allEntities) {
	
	var validCountsInData = new Array();
	
	$.each(allEntities, function(key, currentEntity) {
		
		combinedCount = currentEntity.activityCount;
		
		validCountsInData.push(combinedCount.knownYearCount + combinedCount.unknownYearCount);
	});

	return Math.max.apply(Math, validCountsInData);
}

function calcMaxWithinComparisonParameter(jsonObject){
	
	var validCountsInData = new Array();

	$.each(jsonObject, function(key, val) {

		for (i = 0; i < val.data.length; i++) {
			
			/*
			 * TO make sure that,
			 * 		1. Not to consider years that dont have any counts attached to it.
			 * 		2. Not to consider unknown years indicated by "-1". 
			 * */
			if (val.data[i][1] != 0 && val.data[i][0] != -1) {
				validCountsInData.push(val.data[i][1]);
			}
		}
		
	});
	
	return Math.max.apply(Math, validCountsInData);
}

/**
 * A simple function to see if the passed
 * 
 * @param {array}
 *            objectArray
 * @param {Object}
 *            object
 * @returns a flag - 0/1 - indicating whether a contains b.
 */
function contains(objectArray, object) {
	var i = 0, flag = 0;
	for (i = 0; i < objectArray.length; i++) {
		if (objectArray[i] == object) {
			flag = i;
		}
			
	}
	return flag;
}

var LineWidth = {

	getLineWidth: function(tickSize) {
		if (tickSize >= 0 && tickSize < 10) {
			return 3;
		} else if (tickSize >= 10 && tickSize < 50) {
			return 2;
		} else {
			return 1;
		}
	}
		
};

var TickSize = {
		
	maxValue: 0.0,	
	
	maxTicks: {
		yAxis: 12.0,
		xAxis: 12.0
	},
	
	tickSizeUnits: {
		yAxis: [1.0, 2.5, 5.0],
		xAxis: [1.0, 2, 5.0]
	},
	
	getApproximateTickSize: function(allowedMaxTicks) {
		return Math.max(Math.ceil(parseFloat(this.maxValue) / allowedMaxTicks), 1.0);
	},
	
	getFinalTickSize: function(unitTickSizeGenerator) {
		tickSizeMultiplier = 1.0;
		finalTickSize = 1.0;
		approximateTickSize = this.getApproximateTickSize(this.maxTicks.yAxis);
		
		while (true) {
			if (approximateTickSize <= (unitTickSizeGenerator[0] * tickSizeMultiplier)) {
				finalTickSize = unitTickSizeGenerator[0] * tickSizeMultiplier;
				break;
			}
			if (approximateTickSize <= (unitTickSizeGenerator[1] * tickSizeMultiplier)) {
				finalTickSize = unitTickSizeGenerator[1] * tickSizeMultiplier;
				break;
			}
			if (approximateTickSize <= (unitTickSizeGenerator[2] * tickSizeMultiplier)) {
				finalTickSize = unitTickSizeGenerator[2] * tickSizeMultiplier;
				break;
			}
			tickSizeMultiplier *= 10.0;
		}
		return finalTickSize;
	},
	
	getTickSize: function(value, onAxis) {
		this.maxValue = value;
		if (onAxis.trim().toLowerCase() === 'y') {
			return this.getFinalTickSize(this.tickSizeUnits.yAxis);
		} else if (onAxis.trim().toLowerCase() === 'x') {
			return this.getFinalTickSize(this.tickSizeUnits.xAxis);
		}
	} 
};

/**
 * Create a div that represents the rectangular bar A hidden input class that is
 * used to pass the value and a label beside the checkbox.
 * 
 * @param {Object}
 *            entityLabel
 */

function createLegendRow(entity, bottomDiv) {

    var parentP = $('<p>');
    parentP.attr('id', slugify(entity.entityURI));

    var labelDiv = $('<div>');
    labelDiv.attr('class', 'easy-deselect-label');
    labelDiv.html('<div class="entity-label-url ellipsis"></div>');
    
    /*
     * We should display a further drill-down option only when available. In case of people
     * there is no drill-down possible, so don't diaply the temporal graph icon.
     * */
    if (entity.visMode !== "PERSON") {
    	labelDiv.append('<a class="temporal-vis-url" href="' + getTemporalVisURL(entity.entityURI) + '"><img src = "' + temporalGraphDrillDownIcon + '"/></a>');	
    }
    

    var checkbox = $('<input>');
    checkbox.attr('type', 'checkbox');
    checkbox.attr('checked', true);
    checkbox.attr('id', 'checkbox');
    checkbox.attr('class', 'easyDeselectCheckbox');
    checkbox.attr('value', entity.entityURI);

    var hiddenLabel = $('<label>');
    hiddenLabel.attr('type', 'hidden');
    hiddenLabel.attr('value', entity.label);

    var barDiv = $('<div>');
    barDiv.attr('id', 'bar');
    
    var knownBar = $('<span>');
    knownBar.attr('class', 'known-bar');
    
    var currentYearBar = $('<span>');
    currentYearBar.attr('class', 'current-year-bar');

    var unknownBar = $('<span>');
    unknownBar.attr('class', 'unknown-bar');
    
    var unknownBarInnerSpan = $('<span>');
    unknownBarInnerSpan.attr('class', 'unknown-inner-bar');
    unknownBarInnerSpan.html('&nbsp;');
    
    unknownBar.append(unknownBarInnerSpan);
    
    barDiv.append(unknownBar);
    barDiv.append(knownBar);
    barDiv.append(currentYearBar);
    
    
    var numAttributeText = $('<span>');
    numAttributeText.attr('class', 'bar-count-text');

    parentP.append(checkbox);
    parentP.append(labelDiv);
    parentP.append(hiddenLabel);
    parentP.append(barDiv);
    parentP.append(numAttributeText);
    
    if (bottomDiv.children('p.displayCounter').nextAll().last().length > 0) {
    	bottomDiv.children('p.displayCounter').nextAll().last().after(parentP);
    } else {
    	bottomDiv.children('p.displayCounter').after(parentP);
    }
    
    

    renderBarAndLabel(entity, barDiv, labelDiv, numAttributeText);
}

/**
 * generate the corresponding bar (representing area under the linegraph)
 * and label of the entity clicked
 */

function renderBarAndLabel(entity, divBar, divLabel, spanElement) {

	var combinedCount = entity.activityCount;
	
	var sum = combinedCount.knownYearCount + combinedCount.unknownYearCount;
	
    var normalizedWidth = getNormalizedWidth(entity, sum);
    
    divBar.css("width", normalizedWidth + 5);
    
    if (combinedCount.knownYearCount - combinedCount.currentYearCount) {
    
    	var knownNormalizedWidth = getNormalizedWidth(entity, combinedCount.knownYearCount - combinedCount.currentYearCount);
        
        var countExplanation = (combinedCount.knownYearCount - combinedCount.currentYearCount) 
                                + ' ' + i18nStringsUtil.ofString + ' ' + sum 
    	                        + ' ' + i18nStringsUtil.wereString + ' ' 
    	                        + COMPARISON_PARAMETERS_INFO[currentParameter].verbName 
    	                        + ' ' + i18nStringsUtil.inCompletedYear;

        divBar.children(".known-bar").attr("title", countExplanation);
        
        divBar.children(".known-bar").html("&nbsp;").css("background-color", colorToAssign).css("width", knownNormalizedWidth);
    
    } else {
    
    	divBar.children(".known-bar").hide();
    }
    
    
    if (combinedCount.unknownYearCount) {
    	var unknownNormalizedWidth = getNormalizedWidth(entity, combinedCount.unknownYearCount);
    	
        var countExplanation = combinedCount.unknownYearCount + ' ' + i18nStringsUtil.ofString + ' ' 
                                + sum + ' ' + i18nStringsUtil.haveAnUnknown + ' '
                                + COMPARISON_PARAMETERS_INFO[currentParameter].name 
                                + ' ' + i18nStringsUtil.yearNotChartered;
        
        divBar.children(".unknown-bar").attr("title", countExplanation);
        
        divBar.children(".unknown-bar").children(".unknown-inner-bar").html("&nbsp;").css("background-color", colorToAssign).css("width", unknownNormalizedWidth);
        
    } else {
    	
    	divBar.children(".unknown-bar").hide();
    }
    
    if (combinedCount.currentYearCount) {
    	var currentNormalizedWidth = getNormalizedWidth(entity, combinedCount.currentYearCount);
    	
        var countExplanation = combinedCount.currentYearCount + ' ' + i18nStringsUtil.ofString + ' ' + sum 
                                + ' ' + i18nStringsUtil.wereString + ' ' 
                                + COMPARISON_PARAMETERS_INFO[currentParameter].verbName + ' ' 
                                + i18nStringsUtil.inIncompleteYear;
        
        divBar.children(".current-year-bar").attr("title", countExplanation);
        
        divBar.children(".current-year-bar").html("&nbsp;").css("background-color", colorToAssign).css("width", currentNormalizedWidth);
    	
    } else {
    	
    	divBar.children(".current-year-bar").hide();
    }
    
    var entityLabelForLegend = divLabel.find(".entity-label-url");
    entityLabelForLegend.html(entity.label);
    entityLabelForLegend.ellipsis();
    entityLabelForLegend.wrap("<a class='entity-url' title='" + entity.label + "' href='" + getVIVOURL(entity.entityURI) + "'></a>");

    spanElement.text(sum).css("font-size", "0.8em").css("color", "#595B5B");
}

function getVIVOURL(entityURI){
	
	return subOrganizationVivoProfileURL + "uri=" + entityURI;
	
}

function getTemporalVisURL(entityURI) {
	if (vivoDefaultNamespace) {
		
		/*
		 * This means that the URI of the entity is made up of default namespace so lets make the 
		 * short url, shorter!
		 * */
		if (entityURI.search(vivoDefaultNamespace) === 0) {
			return temporalGraphCommonURL + entityURI.substring(vivoDefaultNamespace.length);			
		}
	} 
	
	/*
	 * Default short url template involves using long uri, this in case the entity is not based off 
	 * of default namespace.
	 * */
	return temporalGraphCommonURL + "?uri=" + entityURI ;
}

function getVIVOProfileURL(given_uri) {
	
	finalURL = $.ajax({
		url: contextPath + "/visualization",
		data: ({vis: "utilities", vis_mode: "PROFILE_URL", uri: given_uri}),
		dataType: "text",
		async: false,
		success:function(data){
	}
	}).responseText;

	return finalURL;
	
}

function slugify(textToBeSlugified) {
	return textToBeSlugified.replace(/\s+/g, '-').replace(/[^a-zA-Z0-9\-]/g, '').toLowerCase();
}

/**
 * remove the bar, label and checkbox during runtime.
 * 
 * @param {Object}
 *            label
 * @param {Object}
 *            bar
 * @param {Object}
 *            checkbox
 * @param {Object}
 *            span
 */
function removeLegendRow(checkbox) {
	
	//console.log("removeLegendRow is called for "+$(checkbox).attr("value"));
	var pToBeRemovedIdentifier = $(checkbox).attr("value");
	$('p#' + slugify(pToBeRemovedIdentifier)).remove();
	
    $(checkbox).next('a').css("font-weight", "normal");
}

/**
 * These are the options passed to by $.pagination(). Basically they define the
 * items visible per page, number of navigation tabs, and number of edge
 * navigation tabs.
 * 
 * @param object
 * @param itemsPerPage
 * @param numberOfDisplayEntries
 * @param numOfEdgeEntries
 */
function setOptionsForPagination(object, itemsPerPage, numberOfDisplayEntries,
		numOfEdgeEntries) {

	object = {
			items_per_page : itemsPerPage,
			num_display_entries : numberOfDisplayEntries,
			num_edge_entries : numOfEdgeEntries,
			prev_text : i18nStringsUtil.previousString,
			next_text : i18nStringsUtil.nextString
	};
}

/**
 * function for removing "unknown" values (-1) just before data plotting.
 * 
 * @jsonRecords the set of entities from which the unknowns have to be removed.
 */

function removeUnknowns(jsonRecords) {
	
	var i = 0, j = 0;

	while (j < jsonRecords.length) {

		jsonRecords[j].unknownsValue = -1;

		for (i = 0; i < jsonRecords[j].data.length; i++) {
			if (jsonRecords[j].data[i][0] == -1) {
				jsonRecords[j].unknownsValue = jsonRecords[j].data[i][1];
				jsonRecords[j].data.splice(i, 1);
				i--;
			}
		}
		j++;
	}
	
}

function insertBackUnknowns(jsonRecords) {
	
	var i = 0, j = 0;

	while (j < jsonRecords.length) {
		if (jsonRecords[j].unknownsValue != -1) {
			jsonRecords[j].data.push( [ -1, jsonRecords[j].unknownsValue ]);
		}
		j++;
	}
}

/**
 * function to get the next free color in the queue
 * side-effecting entity here.
 */
function getNextFreeColor(entity){
   
	/* check freeColors is not empty and
     * Remove the first element out of freeColors
     */

    if (contains(freeColors, prevColor[entity.label])) {
        var index = contains(freeColors, prevColor[entity.label]);
        colorToAssign = freeColors[index];
        freeColors.splice(index, 1);
    } else {
        colorToAssign = freeColors.shift();
    }    
    /*
     * use colorToAssign to plot the current linegraph
     * also store it in colors
     */
    entity.color = colorToAssign;
    colors[entity.label] = colorToAssign;
}

function getNormalizedWidth(entity, sum){
	
	 var maxValueOfComparisonParameter = calcMaxOfComparisonParameter(URIToEntityRecord);
	 var normalizedWidth = 0;
	 
	 normalizedWidth = Math.floor(225 * (sum / maxValueOfComparisonParameter));

	 /*
	  * This will make sure that the entites that have very low <parameter> count have at least
	  * 1 pixel width bar. This happens when the highest count organization has a very high count
	  * compared to the lowest count organization.
	  * */
	 return normalizedWidth === 0 ? 1 : normalizedWidth;
}

function renderLineGraph(renderedObjects, entity){
	
    renderedObjects.push(entity);
    stuffZerosIntoLineGraphs(renderedObjects, year);
    
}


function removeUsedColor(entity){
	
    if (colors[entity.label]) {
        colorToRemove = colors[entity.label];
        prevColor[entity.label] = colorToRemove;
        entity.color = "";
    }
    
	//Insert it at the end of freeColors
    freeColors.push(colorToRemove);
}

function removeEntityUnChecked(renderedObjects, entity){
	
	//remove the entity that is unchecked
    var ii = 0;
    while (ii < renderedObjects.length) {
    	
        if (renderedObjects[ii].label == entity.label) {
            unStuffZerosFromLineGraph(renderedObjects[ii]);
            renderedObjects.splice(ii, 1);
        } else {
        	ii++;
        }             
    }
    unStuffZerosFromLineGraphs(renderedObjects, year);
    
}


function generateCheckBoxes(label, checkedFlag, fontFlag){
	
	var parentP = $('<p>');
	
	var li = $('<li>');
	
	var checkbox = $('<input>');
	checkbox.attr('type','checkbox');
	checkbox.attr('class', entityCheckboxSelectorDOMClass);
	checkbox.attr('value', label);
	if(checkedFlag == 1){
		checkbox.attr('checked');
	}
	
	var a = $('<a/>');
	if(fontFlag == 1){
		a.css("font-weight", "bold");
	}
	a.html(label);
	
	parentP.append(li);
	parentP.append(checkbox);
	parentP.append(a);
	
    return parentP;
}

function clearRenderedObjects(){
	
	$.each(URIToCheckedEntities, function(index, val){
		if($(val).is(':checked')){
			$(val).attr("checked", false);
			updateRowHighlighter(val);
			removeUsedColor(URIToEntityRecord[$(val).attr("value")]);
			removeEntityUnChecked(renderedObjects, URIToEntityRecord[$(val).attr("value")]);
			removeLegendRow(val);
			displayLineGraphs();
		}
	});
	
	URIToCheckedEntities = {};
	
	checkIfColorLimitIsReached();
	updateCounter();

}

function createNotification( template, vars, opts ){
	return $notificationContainer.notify("create", template, vars, opts);
}

function updateCounter(){
	//notification about the max items that can be clicked
	$("#counter").text(renderedObjects.length);
}

function displayLineGraphs(){
	
	//plot all we got
    if (renderedObjects.length == 0) {
    	
    	init(graphContainer);
    	
    } else {
    	
    	removeUnknowns(renderedObjects);
        $.plot(graphContainer, renderedObjects, FlotOptions);
        insertBackUnknowns(renderedObjects);

    }
}

function removeCheckBoxFromGlobalSet(checkbox){
    //remove checkbox object from the globals
	var value = $(checkbox).attr("value");
	/*if (labelToCheckedEntities[value]) {
		delete labelToCheckedEntities[value];
	}*/
	
	if (URIToCheckedEntities[value]) {
		delete URIToCheckedEntities[value];
	}
	
}

/*
 * function to create a table to be 
 * used by jquery.dataTables. The JsonObject 
 * returned is used to populate the pagination table.
 */	
function prepareTableForDataTablePagination(jsonData, dataTableParams){
	
//	console.log(processJSONData.currentEntityLevel);
	
	if (processJSONData.currentEntityLevel.toUpperCase() === "ORGANIZATIONS AND PEOPLE") {
		$.fn.dataTableExt.afnFiltering.push(DatatableCustomFilters.peopleOrOrganizations);
	}
		
	
	var table = $('<table>');
	table.attr('cellpadding', '0');
	table.attr('cellspacing', '0');
	table.attr('border', '0');
	table.attr('id', 'datatable');
	table.css('font-size', '0.9em');
	table.css('width', '100%');
	
	var thead = $('<thead>');
	var tr = $('<tr>');
	
	var checkboxTH = $('<th>');
	checkboxTH.html(' ');
	
	var entityLabelTH = $('<th>');
	entityLabelTH.html(i18nStringsUtil.entityLabel);
	
	var activityCountTH = $('<th>');
	if ($("select.comparisonValues option:selected").text() === i18nStringsUtil.byPublications) {
		activityCountTH.html(i18nStringsUtil.publicationCount);
	} else {
		activityCountTH.html(i18nStringsUtil.grantCount);		
	}
	activityCountTH.attr("id", "activity-count-column");

	var entityTypeTH = $('<th>');
	entityTypeTH.html(i18nStringsUtil.entityType);

	tr.append(checkboxTH);
	tr.append(entityLabelTH);
	tr.append(activityCountTH);
	tr.append(entityTypeTH);
	
	thead.append(tr);
	
	table.append(thead);
	
	var tbody = $('<tbody>');
	var checkboxCount = 0;
	
	$.each(URIToEntityRecord, function(index, val) {
		var entityTypesWithoutStopWords = removeStopWords(val);
		var row = $('<tr>'); 
		
		var checkboxTD = $('<td>');
		checkboxTD.html('<div class="disabled-checkbox-event-receiver">&nbsp;</div><input type="checkbox" class="' 
								+ entityCheckboxSelectorDOMClass + '" value="' 
								+ val.entityURI + '"'+'/>');
		
		var labelTD =  $('<td>');
		labelTD.css("width", "100px");
		labelTD.html(val.label);
		
		var publicationCountTD =  $('<td>');
		
		var combinedCount = val.activityCount;
		
		publicationCountTD.html(combinedCount.knownYearCount + combinedCount.unknownYearCount);
		
		var entityTypeTD =  $('<td>');
		entityTypeTD.html(entityTypesWithoutStopWords);
		
		row.append(checkboxTD);
		row.append(labelTD);
		row.append(publicationCountTD);
		row.append(entityTypeTD);
		
		tbody.append(row);
		checkboxCount++;
	});
	
	table.append(tbody);
	tableDiv.append(table);
	
	var entityListTable = $('#datatable').dataTable({
	    "sDom": '<"' + dataTableParams.searchBarParentContainerDIVClass + '"f><"filterInfo"i><"' + dataTableParams.paginationContainerDIVClass + '"p><"table-separator"><"datatablewrapper"t>',
	    "aaSorting": [
	        [2, "desc"], [1,'asc']
	    ],
	    "asStripClasses": [],
	    "iDisplayLength": 10,
	    "bInfo": true,
	    "oLanguage": {
			"sInfo": "Records _START_ - _END_ of _TOTAL_",
			"sInfoEmpty": i18nStringsUtil.noMatchingEntities,
			"sInfoFiltered": ""
		},
	    "sPaginationType": "gmail_style",
	    "fnDrawCallback": function () {
	    	
	        /* We check whether max number of allowed comparisions (currently 10) is reached
	         * here as well becasue the only function that is guaranteed to be called during 
	         * page navigation is this. No need to bind it to the nav-buttons becuase 1. It is over-ridden
	         * by built-in navigation events & this is much cleaner.
	         * */
	        checkIfColorLimitIsReached();
	    }
	});
	
	
	var searchInputBox = $("." + dataTableParams.searchBarParentContainerDIVClass).find("input[type=text]");
	
	searchInputBox.after("<span id='reset-search' title='" + i18nStringsUtil.clerSearchQuery + "'>X</span>");
	
	$("#reset-search").live('click', function() {
		entityListTable.fnFilter("");
	});
	
	return entityListTable;
}


/*
 * function to create a table to be 
 * used by jquery.dataTables. The JsonObject 
 * returned is used to populate the pagination table.
 */	
function reloadDataTablePagination(preselectedEntityURIs, jsonData){
	
	if (processJSONData.currentEntityLevel.toUpperCase() === "ORGANIZATIONS AND PEOPLE") {
		
		/*
		 * This will make sure that duplicate filters are not added.
		 * */
		if($.inArray(DatatableCustomFilters.peopleOrOrganizations, $.fn.dataTableExt.afnFiltering) < 0) {
			$.fn.dataTableExt.afnFiltering.push(DatatableCustomFilters.peopleOrOrganizations);
		}
	} else {
		
		var indexOfPeopleOrOrganizationFilter = $.inArray(DatatableCustomFilters.peopleOrOrganizations, $.fn.dataTableExt.afnFiltering);
		
		if (indexOfPeopleOrOrganizationFilter >= 0) {
			$.fn.dataTableExt.afnFiltering.splice(indexOfPeopleOrOrganizationFilter, 1);
		}
	}
	
	var currentDataTable = $('#datatable').dataTable();
	
	currentDataTable.fnClearTable();
	
	if ($("select.comparisonValues option:selected").text() === "i18nStringsUtil.byPublications") {
		$("#activity-count-column").html(i18nStringsUtil.publicationCount);
	} else {
		$("#activity-count-column").html(i18nStringsUtil.grantCount);		
	}
	
	function addNewRowAfterReload(entity) {
		
		var checkboxTD = '<div class="disabled-checkbox-event-receiver">&nbsp;</div><input type="checkbox" class="' 
			+ entityCheckboxSelectorDOMClass 
			+ '" value="' 
			+ entity.entityURI + '"' +'/>';

		var labelTD =  entity.label;
		
		var combinedCount = entity.activityCount;
		
		var publicationCountTD = combinedCount.knownYearCount + combinedCount.unknownYearCount;
		
		var entityTypeTD =  removeStopWords(entity);
		
		var newRow = [checkboxTD,
		  labelTD,
		  publicationCountTD,
		  entityTypeTD];
		
		/*
		 * Dont redraw the table, so no sorting, no filtering.
		 * */
		currentDataTable.fnAddData(newRow, false);
	}
	
	/*
	 * This will ensure that currently selected entities are added first in the table,
	 * to make sure that they are "visible" in the DOM. This so that our manual trigger
	 * for selecting this checkboxes on page load, actually works.  
	 * */
	$.each(preselectedEntityURIs, function(index, uri) {
		if (URIToEntityRecord[uri]) {
			addNewRowAfterReload(URIToEntityRecord[uri]);
		}
	});
	
	
	$.each(URIToEntityRecord, function(index, val) {
		
		/*
		 * Don't consider already added pre-selected entities. 
		 * */
		if ($.inArray(index, preselectedEntityURIs) < 0) {
			addNewRowAfterReload(val);
		}
	});
	
	/*
	 * We should change to the first page so that checkboxes are selectable.
	 * */
	currentDataTable.fnPageChange('first');
	
	return currentDataTable;
}

function updateRowHighlighter(linkedCheckBox){
	linkedCheckBox.closest("tr").removeClass('datatablerowhighlight');
}

function removeStopWords(val) {
	
	return $.map(val.organizationType, function(type, i){

		if ($.inArray(type, STOP_WORDS_FOR_TABLE) < 0) {
			return (type);
		}
	}).join(", ");
}

function setEntityLevel(entityLevel){
	$('#entityleveltext').text('  ' + entityLevel.toLowerCase()).css('font-style', 'italic');
	$('#entityHeader').text(entityLevel).css('font-weight', 'bold');
	$('#headerText').css("color", "#2485ae");
}

function toCamelCase(string){
	return string ? (string.substr(0,1).toUpperCase() + string.substr(1, string.length-1).toLowerCase()) : "";
}

function getSize(map){
	var size = 0;
	
	$.each(map, function(){
		size++;
	});
	
	return size;
}

function disableUncheckedEntities(){

	$.each($("input[type=checkbox]." + entityCheckboxSelectorDOMClass + ":not(:checked)"), function(index, val){
		$(val).attr('disabled', true);
		$(val).prev().show();
	});
	
	/*
	 * This indicates the first time this function is called presumably after the 10th checkbox is selected.
	 * We want to display a warning message only in Internet Explorer because in IE the div that handles
	 * disabled-checkbox-clicks is colored white & we cant see the actual checkbox.
	 * 
	 * Note that the usual Error message will still display if the user tries to click on the white box 
	 * (or a disabled checkbox in case of non-IE browsers).   
	 * */
	if ($("#datatable").data("isEntitySelectionAllowed")) {
		if ($.browser.msie) {
			createNotification("warning-notification", { title: i18nStringsUtil.informationString, 
				text: i18nStringsUtil.shortMaxEntityNote },{
				custom: false,
				expires: false
				});	
		}
	} 
		
	
	$("#datatable").data("isEntitySelectionAllowed", false);
}

function enableUncheckedEntities(){
	
	$.each($("input[type=checkbox]." + entityCheckboxSelectorDOMClass + ":not(:checked)"), function(index, val){
		$(val).attr('disabled', false);
		$(val).prev().hide();
	});
	
	
	
	$("#datatable").data("isEntitySelectionAllowed", true);
}

function checkIfColorLimitIsReached(){
	
	if (getSize(URIToCheckedEntities) >= 10) {
		disableUncheckedEntities();
	} else {
		enableUncheckedEntities();
	}
}

function setTickSizeOfAxes(){
	
	var checkedLabelToEntityRecord = {};
	var yearRange;
	
	$.each(URIToCheckedEntities, function(index, val){
		checkedLabelToEntityRecord[index] = URIToEntityRecord[index];
	});
	
	var normalizedYearRange = getNormalizedYearRange();
	
    FlotOptions.xaxis.tickSize = 
		TickSize.getTickSize(normalizedYearRange.normalizedRange, 'x');

    FlotOptions.series.lines.lineWidth = LineWidth.getLineWidth(FlotOptions.xaxis.tickSize);
    
	FlotOptions.yaxis.tickSize = 
			TickSize.getTickSize(calcMaxWithinComparisonParameter(checkedLabelToEntityRecord), 'y');
}
