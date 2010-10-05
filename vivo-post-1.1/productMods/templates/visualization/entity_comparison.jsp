<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir">
	<c:out value="${portalBean.themeDir}" />
</c:set>

<c:set var='jsonContent' value='${requestScope.JsonContent}' />

<div id="body">
	<h1>Temporal Graph Visualization</h1>
	<div id="leftblock">
		<h2>How do you want to compare?</h2>
		<select class="comparisonValues">
		<!--
		TODO: add "selecte" option for publications
		-->
			<option value="Publications" selected="selected">Publications</option>
			<option value="Grants">Grants</option>
			<option value="People">People</option>
			<option value="Item4">Item4</option>
			<option value="Item5">Item5</option>
		</select>
		<br/>
		<button type ="button" align="left"> clear</button>

		<!-- pagination div is for the top navigating buttons 
		TODO: change the wording to be generalized
		-->
		<h2 id="heading">Select sub entities to compare</h2>
		<div id="pagination"></div>

		<!-- #searchresult is for inserting the data from schools -->
			<dl id="searchresult"></dl>
		</div>
		<div id="rightblock"><span class="yaxislabel"></span>
			<div id="graphContainer" style="width: 500px; height: 250px;"></div>
			<div id="bottom" style="width: 500px;">
				<div class="xaxislabel">Year</div>
				<h3><span id="comparisonParameter"></span></h3>
			<p class="displayCounter">You have selected <span id="counter">0</span> of a maximum <span
			id="total">10</span> sub-entities to compare.</p>
			</div>
		</div>
		
</div>
<script type="text/javascript"><!--
	
	$(document).ready(function() {

		var jsonString = '${jsonContent}';
		var jsonObject = {
			prepare : function(arg1){
				loadData(arg1);
			}
		};
		
        var data = [];

        /*
         * options and various global variables
         */
        var graphContainer = $("#graphContainer");
        
        var colors = {};

        //TODO: either remove this free-colors or remove the gloabl one

        var prevColor = {};
        var colorToAssign, colorToRemove;
        
        var labelToEntityRecord = {};
        var setOfLabels = [];

		/*
		 * When the intra-entity parameters are clicked,
		 * update the status accordingly.	
		 */	        
        $("select.comparisonValues").click(function(){
            var selectedValue = $("select option:selected").val();
			$("#comparisonParameter").text("Total Number of " + selectedValue);
			$('.yaxislabel').html("Number of " + selectedValue + lotsofSpaceCharacters).mbFlipText(false);
        });

		/*
		 * click event handler for clear button
		 */
		$("button").click(function(){
			console.log("clear button is clicked!");
			init(graphContainer);
		});

        /*
         * initial display of the grid when the page loads
         */
        init(graphContainer);

        /*
         * by default have all the checkboxes unchecked
         */
        $("input.school").attr("checked", 0);

        /*
         * parse the json returned by jsp and pass it 
         * to loadData
         */
        jsonObject.prepare(jQuery.parseJSON(jsonString));

        /* 
         *  function to populate the labelToEntityRecord object with the
         *  values from the json file and
         *  dynamically generate checkboxes
         */
        function loadData(jsonData){
            var maxValueOfComparisonParameter;
            var ArrayOfMinAndMaxYear = [];
            var ArrayOfLocalMinAndMaxYear = [];
            var yearRange;

            $.each(jsonData, function(index, val){
                setOfLabels.push(val.label);
                val.data = val.yearToPublicationCount;
                labelToEntityRecord[val.label] = val;
            });


            //TOOO: rename the variable name to convey what do you mean by calcMaxOfComparisonParameter.. as in whose max is caluculated
            maxValueOfComparisonParameter = calcMaxOfComparisonParameter(labelToEntityRecord);
            ArrayOfMinAndMaxYear = calcMinandMaxYears(labelToEntityRecord);
            //TODO: see if you want to change the return type to dict/map 
            yearRange = (ArrayOfMinAndMaxYear[1] - ArrayOfMinAndMaxYear[0]);

            //TODO: maintian naming conventions
            setLineWidthAndTickSize(yearRange, FlotOptions);
        	paginationOptions.callback = pageSelectCallback;       

            
            /*
             * pageSelectCallback is a callback function that
             * creates the new_content div and inserts the corresponding html into it.
             * @param {Object} pageIndex
             * @param {Object} jq
             * @returns false to prevent infinite loop!
             */
            function pageSelectCallback(pageIndex, jq){

                 console.log(pageIndex, jq);
            
                //TODO: what is max_epem.. rename it.
                var highestIndexInPage = Math.min((pageIndex + 1) * paginationOptions.items_per_page, setOfLabels.length);
                
                var new_content = '';
                
                /*
                 * Iterate through the list of school setOfLabels and build an HTML string
                 * Also check if some of the checkboxes are previously checked? If they are checked,
                 * then they should be on this time too!
                 */
                 //TODO: refactor this function .. move it out & pass the p[arameters
                for (var i = pageIndex * paginationOptions.items_per_page; i < highestIndexInPage; i++) {
                    var checkedFlag = '', j = 0, fontWeight = '', disabledFlag = '';
                    $.each(data, function(){
                        if (data[j].label == setOfLabels[i]) {
                            checkedFlag = "checked";
                            fontWeight = " style='font-weight:bold;' ";
                        }
                        j++;
                        
                        
                    });
                    new_content += '<p><dt><input type = "checkbox" class="if_clicked_on_school" value="' + setOfLabels[i] + '"' + checkedFlag + ' ' + disabledFlag + '><a href="" ' + fontWeight + ' >' + setOfLabels[i] + '<\/a><\/dt><\/p>';
                    
                }
                
                
                
                /*
                 * Replace the old content with new content
                 */
                $('#searchresult').html(new_content);
                
                /*
                 * When the elements in the paginated div
                 * are clicked this event handler is called
                 */
                $("input.if_clicked_on_school").click(function(){
                
                    var checkbox = $(this);
                    var checkboxValue = $(this).attr("value");
                    var entity = labelToEntityRecord[checkboxValue];
                    
                    /*
                     * Dynamically generate the bar, checkbox and label.
                     */
                    var bottomDiv = $("#bottom");
                    var hiddenLabel = createGraphic(entity, bottomDiv);

                    
                    var divBar = hiddenLabel.next();
                    var divLabel = hiddenLabel.prev();
                    var spanElement = divBar.next('span');
                    var entity = labelToEntityRecord[checkboxValue];

                    /*
                     * If the checkbox is checked
                     */
                    if (checkbox.is(':checked')) {
                    
                        /* check freeColors is not empty and
                         * Remove the first element out of freeColors
                         */

                         //TODO: move this color assignm,ent code out
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
                        
                        
                        /*
                         * calculating the sum of x-values
                         */
                         //TODO: sum_num -> normalized_width etc
                        var sum = 0, sum_num = 0;
                        //TODO: rename calcSu..-> calculateEntiryPubli..
                        sum = calcSum(entity);
                        sum_num = Math.floor(300 * (sum / maxValueOfComparisonParameter));
                        
                        /*
                         * append a div and modify its CSS
                         */
                        divBar.css("background-color", colorToAssign);
                        divBar.css("width", sum_num);
                        divLabel.children("a").html(checkboxValue);
                        spanElement.text(sum);
                        
                        checkbox.next('a').css("font-weight", "bold");
                        
                        
                        data.push(entity);

                        //TODO: rename this to something appropriate
                        //TODO: move this calcminmax... inside stuffZerosIntoLineGraphs
                        ArrayOfLocalMinAndMaxYear = calcMinandMaxYears(data);
                        stuffZerosIntoLineGraphs(data, ArrayOfLocalMinAndMaxYear);
                        
                    } else if (!checkbox.is(':checked')) {
                        /*
                         * If the checkbox is unchecked
                         */

                        	//TODO: move this color de-assignment code out
                            if (colors[entity.label]) {
                                colorToRemove = colors[entity.label];
                                prevColor[entity.label] = colorToRemove;
                                entity.color = "";
                            }
                            
                        /*
                         * Insert it at the end of freeColors
                         */
                            freeColors.push(colorToRemove);

                        /*
                         * removing the item that is unchecked
                         */
                            var ii = 0;
                            while (ii < data.length) {
                                if (data[ii].label == entity.label) {
                                    unStuffZerosFromLineGraph(data[ii]);
                                    data.splice(ii, 1);
                                } else {
                                	ii++;
                                    } 
                                    
                            }
                            ArrayOfLocalMinAndMaxYear = calcZeroLessMinAndMax(data);
                            unStuffZerosFromLineGraphs(data, ArrayOfLocalMinAndMaxYear);

							//TODO: change this logic. by defualt it should be normal.
							//click -> bold
							//unclick -> normal
                            checkbox.next('a').css("font-weight", "normal");
                            
                        /*
                         * Remove the graphic
                         */
                         removeGraphic(checkboxValue);
                        }
					
                    /*
                     *  and plot all we got
                     */
                    if (data.length == 0) {
                    	init(graphContainer);
                    } else {
                    	removeUnknowns(data);
                        $.plot(graphContainer, data, FlotOptions);
                    }
                    
                    
                    /*
                     *  notification about the colors
                     */
                    $("#counter").text((FlotOptions.colors.length - freeColors.length));
                    
                    if (freeColors.length == 0) {
                        $.jGrowl("Colors left: " + freeColors.length, {
                            life: 50
                        });
                    } 
                });
                
                /*
                 * prevent click event propagation
                 */
                return false;
            }
            
            $("#pagination").pagination(setOfLabels.length, paginationOptions);

        }

	});
	
--></script>
