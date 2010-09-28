<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="portalBean" value="${requestScope.portalBean}" />
<c:set var="themeDir">
	<c:out value="${portalBean.themeDir}" />
</c:set>

<c:set var='jsonContent' value='${requestScope.JsonContent}' />

<body>
<h1>Entity Comparison Visualization</h1>
<div id="leftblock">
<h2>How do you want to compare?</h2>
<select class="comparisonValues">
	<option value="Publications">Publications</option>
	<option value="Grants">Grants</option>
	<option value="People">People</option>
	<option value="Item4">Item4</option>
	<option value="Item5">Item5</option>

</select> <!-- pagination div is for the top navigating buttons -->
<h2 id="heading">Select schools to compare</h2>
<div id="pagination"></div>
<!-- #searchresult is for inserting the data from schools -->
<dl id="searchresult">
</dl>
</div>
<div id="rightblock"><span class="yaxislabel"></span>
<div id="graphContainer" style="width: 500px; height: 250px;"></div>
<div id="bottom" style="width: 500px;">
<div class="xaxislabel">Year</div>
<h3><span id="comparisonParameter"></span></h3>
<p>You have selected <span id="counter">0</span> of a maximum <span
	id="total">10</span> schools to compare.</p>
<a id="file"
	href="json/school-of-library-an_publications-per-year-5.json"></a></div>
</div>

<!-- <div id="entity_comparison_vis_container"><c:out
	value="${jsonContent}" /> <br />
<c:out value="${portalBean.themeDir}" /> <br />
</div>
 -->
<script type="text/javascript"><!--
	
	$(document).ready(function() {

		var jsonString = '${jsonContent}';
		var jsonData = jQuery.parseJSON(jsonString);

		console.log(jsonData);
		$.each(jsonData, function(index, value) {
			console.log(value.entityURI);
			});	


        var data = [];
        
        /*
         * options and various global variables
         */
        var graphContainer = $("#graphContainer");
        
        var colors = {};
        var free_colors = [
                             TURQUOISE, LIGHT_YELLOW, LIGHT_VIOLET, LIGHT_RED, 
                             SKY_BLUE, ORANGE, LIGHT_GREEN, LIGHT_PINK, LIGHT_GREY,
                             PURPLE
                          ];
        var prev_color = {};
        var color_to_assign, color_to_remove;
        
        var col_id = 0;
        var schools = {};
        var labels = [];

		/*
		 * When the intra-entity parameters are clicked,
		 * update the status accordingly.	
		 */	

        
        $("select.comparisonValues").click(function(){
            var selectedValue = $("select option:selected").val();
			$("#comparisonParameter").text("Total Number of " + selectedValue);
			$('.yaxislabel').html("Number of " + selectedValue + lotsofSpaceCharacters).mbFlipText(false);
			loadData(jsonData);
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
         * Also load the data when the page is loaded
         */
/*        var dataurl = $("#file").attr('href');
        //console.log("Dataurl: " + dataurl);
        
        $.ajax({
            url: dataurl,
            method: 'GET',
            dataType: 'json',
            success: loadData
        });
*/		

        
        /* 
         *  function to populate the schools object with the
         *  values from the json file and
         *  dynamically generate checkboxes
         */
        function loadData(jsonData){
        
            var max_val;
            var min_max_array = [];
            var lcl_min_max_array = [];
            var year_range;

			//for converting years and counts into ints
            $.each(jsonData, function(index, val){
				
                });
            $.each(jsonData, function(index, val){
                var new_html = '';
                labels.push(val.label);
                val.data = val.yearToPublicationCount;
                schools[val.label] = val;
                //  createGraphic(val.label);
                //console.log(val.label);
            });
            //console.log('----');
            
            max_val = calcMax(schools);
            min_max_array = calcMinandMaxYears(schools);
            year_range = (min_max_array[1] - min_max_array[0]);
            
            //console.log("Max value is " + max_val);
            //console.log('min_year is: ' + min_max_array[0] + ' and max_year is: ' + min_max_array[1]);
            
            //  stuffZeros(schools, min_max_array);
            setLineWidthAndTickSize(year_range, FlotOptions);
        	paginationOptions.callback = pageSelectCallback;       

            
            /*
             * pageSelectCallback is a callback function that
             * creates the new_content div and inserts the corresponding html into it.
             * @param {Object} page_index
             * @param {Object} jq
             * @returns false to prevent infinite loop!
             */
            function pageSelectCallback(page_index, jq){
            
                var items_per_page = 10;
                var max_elem = Math.min((page_index + 1) * items_per_page, labels.length);
                
                //console.log('labels length: ' + labels.length);
                //console.log('items per page: ' + items_per_page);
                //console.log('page_index: ' + page_index);
                //console.log('max_elem: ' + max_elem);
                //console.log(labels);
                
                
                var new_content = '';
                
                /*
                 * Iterate through the list of school labels and build an HTML string
                 * Also check if some of the checkboxes are previously checked? If they are checked,
                 * then they should be on this time too!
                 */
                for (var i = page_index * items_per_page; i < max_elem; i++) {
                    var checked_flag = '', j = 0, font_weight = '', disabled_flag = '';
                    var white_space = '';
                    $.each(data, function(){
                        if (data[j].label == labels[i]) {
                            checked_flag = "checked";
                            font_weight = " style='font-weight:bold;' ";
                        }
                        j++;
                        
                        
                    });
                    //console.log(checked_flag);
                    new_content += '<p><dt><input type = "checkbox" class="if_clicked_on_school" value="' + labels[i] + '"' + checked_flag + ' ' + disabled_flag + '><a href="" ' + font_weight + ' >' + labels[i] + '<\/a><\/dt><\/p>';
                    
                }
                
                
                
                /*
                 * Replace the old content with new content
                 */
                $('#searchresult').html(new_content);
                //console.log(new_content);
                
                /*
                 * When the elements in the paginated div
                 * are clicked this event handler is called
                 */
                $("input.if_clicked_on_school").click(function(){
                
                    //console.log($(this).attr("value") + ' is clicked');
                    var checkbox = $(this);
                    var checkbox_value = $(this).attr("value");
                    
                    /*
                     * Dynamically generate the bar, checkbox and label.
                     */
                    var bottomDiv = $("#bottom");
                    createGraphic(checkbox_value, bottomDiv);
                    
                    var entity = schools[checkbox_value];
                    var hidden_checkbox = $("label:hidden").filter(function(){
                        if ($(this).attr("value") == checkbox_value) 
                            return $(this);
                    });
                    var div_bar = hidden_checkbox.next();
                    var div_label = hidden_checkbox.prev();
                    var span_element = div_bar.next('span');
                    //console.log(hidden_checkbox);
                    var checkbox_value = $(this).attr("value");
                    var entity = schools[checkbox_value];
                   // entity.data = schools[checkbox_value].yearToPublicationCount;
                    
                    
                    /*
                     * If the checkbox is checked
                     */
                    if (checkbox.is(':checked')) {
                    
                        /* check free_colors is not empty and
                         * Remove the first element out of free_colors
                         */
                        if (contains(free_colors, prev_color[entity.label])) {
                            var index = contains(free_colors, prev_color[entity.label]);
                            //console.log('Past color present in free_colors!');
                            color_to_assign = free_colors[index];
                            free_colors.splice(index, 1);
                        }
                        else {
                            //console.log('Past color not present in free_colors!');
                            color_to_assign = free_colors.shift();
                        }
                        
                        
                        /*
                         * use color_to_assign to plot the current linegraph
                         * also store it in colors
                         */
                        entity.color = color_to_assign;
                        colors[entity.label] = color_to_assign;
                        
                        //console.log('Color removed from the head of free_colors: ' + color_to_assign);
                        
                        /*
                         * calculating the sum of x-values
                         */
                        var sum = 0, sum_num = 0;
                        sum = calcSum(entity);
                        sum_num = Math.floor(300 * (sum / max_val));
                        //console.log('sum_num: ' + sum_num);
                        
                        /*
                         * append a div and modify its CSS
                         */
                        div_bar.css("background-color", color_to_assign);
                        div_bar.css("width", sum_num);
                        div_label.children("a").html(checkbox_value);
                        span_element.text(sum);
                        checkbox.next('a').css("font-weight", "bold");
                        
                        
                        data.push(entity);
                        lcl_min_max_array = calcMinandMaxYears(data);
                        stuffZeros(data, lcl_min_max_array);
                        //console.log('current min_year is: ' + lcl_min_max_array[0] + ' and current max_year is: ' + lcl_min_max_array[1]);
                        
                    }
                    
                    /*
                     * If the checkbox is unchecked
                     */
                    else 
                        if (!checkbox.is(':checked')) {
                        
                            if (colors[entity.label]) {
                                color_to_remove = colors[entity.label];
                                prev_color[entity.label] = color_to_remove;
                                entity.color = "";
                            }
                            
                        /*
                         * Insert it at the end of free_colors
                         */
                            free_colors.push(color_to_remove);
                            
                            //console.log('Color added to the tail of free_colors:' + color_to_remove);
                            div_bar.css("background-color", "#fff");
                            div_label.children("a").html("");
                            
                        /*
                         * removing the item that is unchecked
                         */
                            //console.log(data);
                            var i = 0;
                            while (i < data.length) {
                                if (data[i].label == entity.label) {
                                    removeZeros(data[i]);
                                    data.splice(i, 1);
                                }
                                else 
                                    i++;
                            }
                            //console.log(data);
                            lcl_min_max_array = calcZeroLessMinAndMax(data);
                            unStuffZeros(data, lcl_min_max_array);
                            checkbox.next('a').css("font-weight", "normal");
                            
                        /*
                         * Remove the graphic
                         */
                            removeGraphic(div_label, div_bar, hidden_checkbox, span_element);
                        }
					
                    console.log('data to be plotted ',data);
                    console.log(graphContainer,FlotOptions);
                    /*
                     *  and plot all we got
                     */
                    if (data.length == 0) 
                        init(graphContainer);
                    else 
                        $.plot(graphContainer, data, FlotOptions);
                    
                    //console.log('linewidth is :' + FlotOptions.series.lines.lineWidth);
                    //console.log('ticksize is :' + FlotOptions.xaxis.tickSize);
                    
                    /*
                     *  notification about the colors
                     */
                    $("#counter").text((FlotOptions.colors.length - free_colors.length));
                    
                    if (free_colors.length == 0) {
                        $.jGrowl("Colors left: " + free_colors.length, {
                            life: 50
                        });
						

                    }
                });
                
                /*
                 * prevent click event propagation
                 */
                return false;
            }
            
            $("#pagination").pagination(labels.length, paginationOptions);
        }	

	});
	
--></script>
</body>