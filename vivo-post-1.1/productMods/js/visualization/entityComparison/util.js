/**
 * init sets some initial options for the default graph. i.e for when the page
 * is initially loaded or when its refreshed or when all the checkboxes on the
 * page are unchecked.
 * 
 * @param graphContainer
 *            is the div that contains the main graph.
 */
function init(graphContainer) {
	
	$("#comparisonParameter").text("Total Number of " + $("select option:selected").val());
	$('.yaxislabel').html("Number of " + $("select option:selected").val() + lotsofSpaceCharacters).mbFlipText(false);
	
	var defaultFlotOptions = {
		xaxis : {
			min : 1999,
			max : 2019,
			tickDecimals : 0,
			tickSize : 2
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
 * unStuffZeros removes the previously stuffed zero values. r is the current
 * data object. s is the current min and max {year} values. All the datapoints <
 * curr_min{year} && > > curr_max{year} are removed, so that they don't show up
 * on the graph
 * 
 * @param {Object}
 *            jsonObject
 * @param {Object}
 *            arrayOfMinAndMaxYears
 * @returns jsonObject with modified data points.
 */
// TODO: Change all variable names to be informatively named. If I read _only_
// the variable name, I should at least have a good idea what it is.
// TODO: Write in the domain of the problem! (for variables, function names,
// comments, etc...)
function unStuffZeros(jsonObject, arrayOfMinAndMaxYears) {

	var currentMinYear = arrayOfMinAndMaxYears[0], currentMaxYear = arrayOfMinAndMaxYears[1];

	console.log('Inside unStuffZeros - ' + 'current Min Year is: '
			+ currentMinYear + ' and current Max Year is: ' + currentMaxYear);

	$
			.each(
					jsonObject,
					function(key, val) {
						var i = 0;
						for (i = 0; i < val.yearToPublicationCount.length; i++) {
							if (((val.yearToPublicationCount[i][0] < currentMinYear) || (val.yearToPublicationCount[i][0] > currentMaxYear))
									&& val.yearToPublicationCount[i][1] == 0) {
								console.log('Removing ['
										+ val.yearToPublicationCount[i][0] + ',0] from '
										+ val.label + ' at position: '
										+ i);
								val.yearToPublicationCount.splice(i, 1);
								i--;
							} else
								continue;
						}
					});
}

/**
 * while unStuffZeros is for a group of data objects, removeZeros is for a
 * single data object. It removes zeroes from the single object passed as
 * parameter.
 * 
 * @param {Object}
 *            jsonObject
 */
function removeZeros(jsonObject) {
	var i = 0;
	for (i = 0; i < jsonObject.yearToPublicationCount.length; i++) {
		if (jsonObject.yearToPublicationCount[i][1] == 0) {
			console.log('Removing [' + jsonObject.yearToPublicationCount[i][0] + ',0] from '
					+ jsonObject.label + ' at position: ' + i);
			jsonObject.yearToPublicationCount.splice(i, 1);
			i--;
		}
	}
}

/**
 * stuffZeros is used to fill discontinuities in data points. For example, if a
 * linegraph has the following data points [1990, 2],[1992,3],[1994,
 * 5],[1996,5],[2000,4],[2001,1]. stuffZeros inserts
 * [1991,0],[1993,0],1995,0]..and so on. It also inserts zeroes at the beginning
 * and the end if the max and min{year} of the current linegraph fall in between
 * the global max and min{year}
 * 
 * @param {Object}
 *            jsonObject
 * @param {Object}
 *            arrayOfMinAndMaxYears
 * @returns jsonObject with stuffed data points.
 */
function stuffZeros(jsonObject, arrayOfMinAndMaxYears) {

	$
			.each(
					jsonObject,
					function(key, val) {
						var position = arrayOfMinAndMaxYears[0], i = 0;

						for (i = 0; i < (arrayOfMinAndMaxYears[1] - arrayOfMinAndMaxYears[0]) + 1; i++) {

							if (val.yearToPublicationCount[i]) {

								if (val.yearToPublicationCount[i][0] != position
										&& position <= arrayOfMinAndMaxYears[1]) {
									console.log('Inserting [' + position
											+ ',0] into ' + val.label
											+ ' at position: ' + i);
									val.yearToPublicationCount.splice(i, 0,
											[ position, 0 ]);
								}
							}

							else {
								console.log('Inserting [' + position
										+ ',0] into ' + val.label
										+ ' at position: ' + i);
								val.yearToPublicationCount.push( [ position, 0 ]);
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
function calcZeroLessMinAndMax(jsonObject) {

	var globalMinYear = 5000, globalMaxYear = 0, minYear, maxYear, i = 0;

	$.each(jsonObject, function(key, val) {

		for (i = 0; i < val.yearToPublicationCount.length; i++) {
			if (val.yearToPublicationCount[i][1] != 0 ) {
				minYear = val.yearToPublicationCount[i][0];
				break;
			}
		}
		
		for (i = val.yearToPublicationCount.length - 1; i >= 0; i--) {
			if (val.yearToPublicationCount[i][1] != 0 && val.yearToPublicationCount[i][0] != -1) {
				maxYear = val.yearToPublicationCount[i][0];
				break;
			}

		}
		if (globalMinYear > minYear)
			globalMinYear = minYear;
		if (globalMaxYear < maxYear)
			globalMaxYear = maxYear;
		
		console.log('[' + minYear + '] ' + ' [' + maxYear + ']');
	});
	console.log('Inside calcZeroLessMinAndMax and curr_min_year is '
			+ globalMinYear + ' curr_max_year is ' + globalMaxYear);
	return [ globalMinYear, globalMaxYear ];
}

/**
 * z is an an object with two properties label and data. data is of the form
 * [year,value] This function returns the min and max values of all such years.
 * 
 * @param {Object}
 *            jsonObject
 * @returns [minYear, maxYear]
 */
function calcMinandMaxYears(jsonObject) {
	var minYear = 5000, maxYear = 0;
	$.each(jsonObject, function(key, val) {
		if (minYear > val.yearToPublicationCount[0][0]){
			minYear = val.yearToPublicationCount[0][0];
		}
		if (maxYear < val.yearToPublicationCount[val.yearToPublicationCount.length - 1][0] && val.yearToPublicationCount[val.yearToPublicationCount.length - 1][0] != -1)
			maxYear = val.yearToPublicationCount[val.yearToPublicationCount.length - 1][0];
		else {
			maxYear = val.yearToPublicationCount[val.yearToPublicationCount.length - 2][0];
		}
	});
	return [ minYear, maxYear ];
}

/**
 * y is an an object with two properties label and data. data is of the form
 * [year,value] This function returns the max of all values.
 * 
 * @param {Object}
 *            jsonObject
 * @returns maxCount
 */
function calcMax(jsonObject) {
	var sum = 0, i = 0, maxCount = 0;
	$.each(jsonObject, function(key, val) {
		for (i = 0; i < val.yearToPublicationCount.length; i++)
			sum += val.yearToPublicationCount[i][1];

		console.log(val.label + '->' + sum);

		if (maxCount < sum)
			maxCount = sum;

		sum = 0;
	});
	return maxCount;
}

/**
 * x is an object and it has two properties label and data. data is a two
 * dimensional array of the form [year, value] This function returns the sum of
 * all the values.
 * 
 * @param {Object}
 *            jsonObject
 * @returns sum{values}.
 */
function calcSum(jsonObject) {
	var sum = 0, i = 0;
	for (i = 0; i < jsonObject.yearToPublicationCount.length; i++)
		sum += jsonObject.yearToPublicationCount[i][1];

	console.log(sum);
	return sum;
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
		if (objectArray[i] == object)
			flag = i;
	}
	return flag;
}

/**
 * Dynamically change the linewidth and ticksize based on input year range.
 * 
 * @param {Object}
 *            yearRange
 */
function setLineWidthAndTickSize(yearRange, flotOptions) {
	
	if (yearRange > 0 && yearRange < 15) {
		flotOptions.series.lines.lineWidth = 4.5;
		flotOptions.xaxis.tickSize = 1;
	} else if (yearRange > 15 && yearRange < 70) {
		flotOptions.series.lines.lineWidth = 3;
		flotOptions.xaxis.tickSize = 1;
	} else {
		flotOptions.series.lines.lineWidth = 1.5;
		flotOptions.xaxis.tickSize = 1;
	}

}
/**
 * Create a div that represents the rectangular bar A hidden input class that is
 * used to pass the value and a label beside the checkbox.
 * 
 * @param {Object}
 *            entityLabel
 */
function createGraphic(entityLabel, bottomDiv) {

	var newHTML = '';
	newHTML += '<p><div id="label"><a href=""><\/a><\/div>';
	newHTML += '<label class="school" type="hidden" value="' + entityLabel + '">';
	newHTML += '<div id="bar"><\/div>';
	newHTML += '<span id = "text"><\/span><\/p><br\/>' ;
	bottomDiv.children('a').after(newHTML);
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
 * 			  span           
 */
function removeGraphic(label, bar, checkbox, span) {

	span.next('br').remove();
	span.remove();
	label.remove();
	bar.remove();
	checkbox.remove();
}
/**
 * These are the options passed to by $.pagination(). Basically they define
 * the items visible per page, number of navigation tabs, and number of edge navigation tabs.
 * @param object
 * @param itemsPerPage
 * @param numberOfDisplayEntries
 * @param numOfEdgeEntries
 */
function setOptionsForPagination(object, itemsPerPage, numberOfDisplayEntries, numOfEdgeEntries){
	 
	object = {
			 items_per_page: itemsPerPage,
			 num_display_entries: numberOfDisplayEntries,
			 num_edge_entries: numOfEdgeEntries,
			 prev_text: "Prev",
			 next_text: "Next"
	 };
}

/*function prepareJSONData(jsonString, jsonData){
	
	jsonData = jQuery.parseJSON(jsonString);

	console.log(jsonData);
	$.each(jsonData, function(index, value) {
		console.log(value.entityURI);
		});	
	
}
*/
