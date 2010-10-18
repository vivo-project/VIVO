/**
 * init sets some initial options for the default graph. i.e for when the page
 * is initially loaded or when its refreshed or when all the checkboxes on the
 * page are unchecked.
 * 
 * @param graphContainer
 *            is the div that contains the main graph.
 */
function init(graphContainer) {

	// TODO: make use of the id on the select field instead of a generic one.
	$("#comparisonParameter").text("Total Number of " + $("select.comparisonValues option:selected").val());
	$('#yaxislabel').html("Number of " + $("select.comparisonValues option:selected").val()+ lotsofSpaceCharacters).mbFlipText(false);
	$("span#paramdesc").text($("select.comparisonValues option:selected").val() + ' (desc)');	
	$("span#paramasc").text($("select.comparisonValues option:selected").val() + ' (asc)');	
	
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

	$
	.each(
			jsonObject,
			function(key, val) {
				var i = 0;
				for (i = 0; i < val.data.length; i++) {
					if (((val.data[i][0] < currentMinYear) || (val.data[i][0] > currentMaxYear))
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

	var arrayOfMinAndMaxYears = [ year.globalMin, year.globalMax ];

	$
	.each(
			jsonObject,
			function(key, val) {
				var position = arrayOfMinAndMaxYears[0], i = 0;

				for (i = 0; i < (arrayOfMinAndMaxYears[1] - arrayOfMinAndMaxYears[0]) + 1; i++) {

					if (val.data[i]) {

						if (val.data[i][0] != position
								&& position <= arrayOfMinAndMaxYears[1]) {
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

	var globalMinYear = 5000, globalMaxYear = 0, minYear, maxYear, i = 0;

	$.each(jsonObject, function(key, val) {

		for (i = 0; i < val.data.length; i++) {
			if (val.data[i][1] != 0) {
				minYear = val.data[i][0];
				break;
			}
		}

		for (i = val.data.length - 1; i >= 0; i--) {
			if (val.data[i][1] != 0 && val.data[i][0] != -1) {
				maxYear = val.data[i][0];
				break;
			}

		}
		if (globalMinYear > minYear)
			globalMinYear = minYear;
		if (globalMaxYear < maxYear)
			globalMaxYear = maxYear;

	});

	year.globalMin = globalMinYear;
	year.globalMax = globalMaxYear;
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
	var minYear = 5000, maxYear = 0;
	$.each(jsonObject, function(key, val) {
		if (minYear > val.data[0][0]) {
			minYear = val.data[0][0];
		}
		if (maxYear < val.data[val.data.length - 1][0]
		        && val.data[val.data.length - 1][0] != -1){
			maxYear = val.data[val.data.length - 1][0];
		}else {
			maxYear = val.data[val.data.length - 2][0];
		}
	});

	year.min = minYear;
	year.max = maxYear;
}

/**
 * y is an an object with two properties label and data. data is of the form
 * [year,value] This function returns the max of all values.
 * 
 * @param {Object}
 *            jsonObject
 * @returns maxCount
 */
function calcMaxOfComparisonParameter(jsonObject) {
	var sum = 0, i = 0, maxCount = 0;
	$.each(jsonObject, function(key, val) {
		for (i = 0; i < val.data.length; i++)
			sum += val.data[i][1];

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
function calcSumOfComparisonParameter(jsonObject) {

	var sum = 0, i = 0;
	for (i = 0; i < jsonObject.data.length; i++) {
		sum += jsonObject.data[i][1];
	}

	// sum += jsonObject.publicationCount;
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
function createGraphic(entity, bottomDiv) {

	var parentP = $('<p>');
	parentP.attr('id', slugify(entity.label));

	var labelDiv = $('<div>')
	labelDiv.attr('id', 'label');
	labelDiv.html('<a href="' + getEntityURL(entity) + '"></a>');
	
	var checkbox = $('<input>');
	checkbox.attr('type','checkbox');
	checkbox.attr('checked', true);
	checkbox.attr('id','checkbox');
	checkbox.attr('class', 'easyDeselectCheckbox');
	checkbox.attr('value', entity.label);
	
	var hiddenLabel = $('<label>');
	//hiddenLabel.attr('class', 'school');
	hiddenLabel.attr('type', 'hidden');
	hiddenLabel.attr('value', entity.label);

	var barDiv = $('<div>');
	barDiv.attr('id', 'bar');

	var numAttributeText = $('<span>');
	numAttributeText.attr('id', 'text');

	parentP.append(checkbox);
	parentP.append(labelDiv);
	parentP.append(hiddenLabel);
	parentP.append(barDiv);
	parentP.append(numAttributeText);

	bottomDiv.children('p.displayCounter').after(parentP);

	return hiddenLabel;

}

function getEntityURL(entity) {
	
	var result = '', path = '', uri = '';
	
	if(entity.visMode == "PERSON"){
		path = "/vivo1/individual?";
		uri = "uri=" + entity.entityURI;
		var home = "&home=1";
		result = (path + uri + home);
	}else{
		path = "/vivo1/visualization?";
		var visAndRenderMode = "vis=entity_comparison&render_mode=standalone&";
		var visMode = "vis_mode=" + entity.visMode + "&";
		uri = "uri=" + entity.entityURI;
		result = (path + visAndRenderMode + visMode + uri);
	}
	return result;
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
function removeGraphic(checkbox) {
	
	console.log("removeGraphic is called for "+$(checkbox).attr("value"));
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
			prev_text : "Prev",
			next_text : "Next"
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

/**
 * generate the corresponding bar (representing area under the linegraph)
 * and label of the entity clicked
 * @param entity
 * @param divBar
 * @param divLabel
 * @return
 */
function generateBarAndLabel(entity, divBar, divLabel,checkbox, spanElement){
	
	var sum = calcSumOfComparisonParameter(entity);
	var normalizedWidth = getNormalizedWidth(entity, sum);
	var checkboxValue = $(checkbox).attr("value");
	
	//append a div and modify its css
    divBar.css("background-color", colorToAssign);
    divBar.css("width", normalizedWidth);
    divLabel.children("a").html(checkboxValue);
    spanElement.text(sum);
    checkbox.next('a').css("font-weight", "bold");

}

function getNormalizedWidth(entity, sum){
	
	 var maxValueOfComparisonParameter = calcMaxOfComparisonParameter(labelToEntityRecord);
	 var normalizedWidth = 0;
	 
	 normalizedWidth = Math.floor(225 * (sum / maxValueOfComparisonParameter));
	 
	 return normalizedWidth;
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

function createCheckBoxesInsidePaginatedDiv(pageIndex){
	
    var highestIndexInPage = Math.min((pageIndex + 1) * paginationOptions.items_per_page, setOfLabels.length);                
    var newContent = ' ';
    
    /*
     * Iterate through the list of school setOfLabels and build an HTML string
     * Also check if some of the checkboxes are previously checked? If they are checked,
     * then they should be on this time too!
     */
    for (var i = pageIndex * paginationOptions.items_per_page; i < highestIndexInPage; i++) {
        var checkedFlag = ' ', j = 0, fontWeight = ' ';
        $.each(renderedObjects, function(){
            if (renderedObjects[j].label == setOfLabels[i]) {
                checkedFlag = "checked";
            	fontWeight = " style='font-weight:bold;' ";
            }
            j++;                        
        });
        newContent += '<li><input type = "checkbox" class="if_clicked_on_school" value="' + setOfLabels[i] + '"' + checkedFlag + ' ' + '><a href="" ' + fontWeight + ' >' + setOfLabels[i] + '<\/a><\/li>';        
    }
	               
	// replace old content with new content
    $('#searchresult').html(newContent);
    populateMapOfCheckedEntities();

}

function populateMapOfCheckedEntities(){
	var checkedEntities = $("input[type=checkbox].if_clicked_on_school");
	$.each(checkedEntities, function(index, val){
		labelToCheckedEntities[$(val).attr("value")] = val;
	});
}

function generateCheckBoxes(label, checkedFlag, fontFlag){
	
	var parentP = $('<p>');
	
	var li = $('<li>');
	
	var checkbox = $('<input>');
	checkbox.attr('type','checkbox');
	checkbox.attr('class','if_clicked_on_school');
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
	
	$.each(labelToCheckedEntities, function(index, val){
		if($(val).is(':checked')){
			$(val).attr("checked", false);
			removeUsedColor(labelToEntityRecord[$(val).attr("value")]);
			removeEntityUnChecked(renderedObjects, labelToEntityRecord[$(val).attr("value")]);
			removeGraphic(val);
			displayLineGraphs();
			console.log(index);
		}
	});
	
	labelToCheckedEntities = {};
	updateCounter();

}

function updateCounter(){
	//notification about the max items that can be clicked
	$("#counter").text(renderedObjects.length);
	if (freeColors.length == 0) {
        $.jGrowl("Colors left: " + freeColors.length, {
            life: 50
        });
    } 
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
	if(labelToCheckedEntities[value]){
		delete labelToCheckedEntities[value];
	}
}

function sortByParameterDesc(value1, value2){
	var entity1 = labelToEntityRecord[value1];
	var entity2 = labelToEntityRecord[value2];
	
	var sum1 = calcSumOfComparisonParameter(entity1);
	var sum2 = calcSumOfComparisonParameter(entity2);
	
	return (sum2 - sum1);
}

function sortByParameterAsc(value1, value2){
	var entity1 = labelToEntityRecord[value1];
	var entity2 = labelToEntityRecord[value2];
	
	var sum1 = calcSumOfComparisonParameter(entity1);
	var sum2 = calcSumOfComparisonParameter(entity2);
	
	return (sum1 - sum2);
}

function sortByEntityLabelDesc(value1, value2){
	
	var result;
	
	if(value1 > value2){
		result = -1;
	}else if(value1 < value2){
		result = 1;
	 }else {
		 result = 0;
	 }
	return result;
}

function sortByEntityLabelAsc(value1, value2){
	
	var result;
	
	if (value1 > value2) {
		result = 1;
	} else if(value1 < value2) {
		result = -1;
	 } else {
		 result = 0;
	 }
	return result;
}

function renderPaginatedDiv(){

    //$("#entityTitleSortBy").trigger('click', "azdesc");          
    paginationDiv.pagination(setOfLabels.length, paginationOptions);
}


jQuery.fn.liveUpdate = function(list){
	
	  list = jQuery(list);
	
	  if ( list.length ) {
	    var rows = list.children('li');	    
	    var cache = rows.map(function(){
	    	  console.log($(this).children('a').text().toLowerCase());
	        return $(this).children('a').text().toLowerCase();
	      });
	    
	    console.log("rows: ", rows , " cache: ", cache);
	     
	    this.keyup(filter).keyup().parents('form').submit(function(){
	        return false;
	      });
	  }
	   
	  return this;
	   
	  function filter(){
		  
	    var term = jQuery.trim( jQuery(this).val().toLowerCase() ), scores = [];
	    console.log("this: ",this, " term: " +term);
	    
	    if ( !term ) {
	      rows.show();
	    } else {
	      rows.hide();
	      cache.each(function(i){
	    	 var score = this.score(term);
	    	 if (score > 0) { 
	        	scores.push([score, i]); 
	         }
	      });

	      jQuery.each(scores.sort(function(a, b){return b[0] - a[0];}), function(){
	        jQuery(rows[ this[1] ]).show();
		      console.log("showing : ", ($(rows[this[1]]).children('a').text()));
	      });
	    }
	  }
	};

/*
 * function to create a table to be 
 * used by jquery.dataTables. The JsonObject 
 * returned is used to populate the pagination table.
 */	
function prepareTableForDataTablePagination(jsonData){
	
	var table = $('<table>');
	table.attr('cellpadding', '0');
	table.attr('cellspacing', '0');
	table.attr('border', '0');
	table.attr('id', 'datatable');
	
	var thead = $('<thead>');
	var tr = $('<tr>');
	
	var checkboxTH = $('<th>');
	checkboxTH.html(' ');
	
	var entityLabelTH = $('<th>');
	entityLabelTH.html('Entity Label');
	
	var publicationCountTH = $('<th>');
	publicationCountTH.html('Publication Count');

	var entityTypeTH = $('<th>');
	entityTypeTH.html('Entity Type');

	tr.append(checkboxTH);
	tr.append(entityLabelTH);
	tr.append(publicationCountTH);
	tr.append(entityTypeTH);
	
	thead.append(tr);
	
	table.append(thead);
	
	var tbody = $('<tbody>');
	
	$.each(labelToEntityRecord, function(index, val){
		var row = $('<tr>'); 
		
		var checkboxTD = $('<td>');
		checkboxTD.html('<input type="checkbox" class="if_clicked_on_school" value="' + index + '"'+'/>');
		
		var labelTD =  $('<td>');
		labelTD.html(index);
		
		var publicationCountTD =  $('<td>');
		publicationCountTD.html(calcSumOfComparisonParameter(val));
		
		var entityTypeTD =  $('<td>');
		entityTypeTD.html(val.organizationType.join(", "));
		
		row.append(checkboxTD);
		row.append(labelTD);
		row.append(publicationCountTD);
		row.append(entityTypeTD);
		
		tbody.append(row);
	});
	
	table.append(tbody);
	tableDiv.append(table);
	
	$('#datatable').dataTable({
		"sDom": '<f>tl'
//		"bLengthChange": false,
//		"bAutoWidth": false
	});
}

