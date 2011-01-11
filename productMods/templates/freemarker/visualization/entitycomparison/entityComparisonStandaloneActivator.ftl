<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign standardVisualizationURLRoot ="/visualizationfm">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign organizationURI ="${organizationURI?url}">
<#assign jsonContent ="${jsonContent}">
<#assign organizationLabel = "${organizationLabel}">
<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">
<#assign subOrganizationTemporalGraphURL = "${urls.base}${standardVisualizationURLRoot}?vis=entity_comparison">
<#assign subOrganizationVivoProfileURL = "${urls.base}/individual?">



<#assign temporalGraphSmallIcon = '${urls.images}/visualization/temporal_vis_small_icon.jpg'>


<#assign TemporalGraphDownloadFile = '${urls.base}${dataVisualizationURLRoot}?vis=entity_comparison&uri=${organizationURI}&labelField=label&vis_mode=UNIVERSITY'>


<#-- Javascript files -->

<#assign flot = '${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/jquery.flot.js'>
<#assign excanvas = '${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/excanvas.js'>


<!--[if IE]><script type="text/javascript" src="${excanvas}"></script><![endif]-->

<#assign fliptext = '${urls.base}/js/visualization/entitycomparison/jquery_plugins/fliptext/jquery.mb.flipText.js'>

<#assign jqueryNotify = '${urls.base}/js/jquery_plugins/jquery.notify.min.js'>
<#assign jqueryUI = '${urls.base}/js/jquery-ui/js/jquery-ui-1.8.4.custom.min.js'>

<#assign datatable = '${urls.base}/js/visualization/entitycomparison/jquery_plugins/datatable/jquery.dataTables.js'>
<#assign autoellipsis = '${urls.base}/js/visualization/entitycomparison/jquery_plugins/jquery.AutoEllipsis.js'>

<#assign entityComparisonUtils = '${urls.base}/js/visualization/entitycomparison/util.js'>
<#assign entityComparisonConstants = '${urls.base}/js/visualization/entitycomparison/constants.js'>

<script type="text/javascript" src="${flot}"></script>
<script type="text/javascript" src="${fliptext}"></script>

<!--
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/jquery-ui.min.js"></script>
-->

<script type="text/javascript" src="${jqueryUI}"></script>




<script type="text/javascript" src="${datatable}"></script>
<script type="text/javascript" src="${autoellipsis}"></script>
<script type="text/javascript" src="${entityComparisonUtils}"></script>
<script type="text/javascript" src="${entityComparisonConstants}"></script>
<script type="text/javascript" src="${jqueryNotify}"></script>

<#-- CSS files -->


<#assign demoTable = "${urls.base}/js/visualization/entitycomparison/jquery_plugins/datatable/demo_table.css" />

<#assign jqueryUIStyle = "${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.4.custom.css" />
<#assign jqueryNotifyStyle = "${urls.base}/css/jquery_plugins/ui.notify.css" />

<#assign entityComparisonStyle = "${urls.base}/css/visualization/entitycomparison/layout.css" />
<#assign entityComparisonStyleIEHack = "${urls.base}/css/visualization/entitycomparison/layout-ie.css" />

<#assign vizStyle = "${urls.base}/css/visualization/visualization.css" />



<link href="${jqueryUIStyle}" rel="stylesheet" type="text/css" />
<link href="${demoTable}" rel="stylesheet" type="text/css" />
<link href="${entityComparisonStyle}" rel="stylesheet" type="text/css" />
<link href="${vizStyle}" rel="stylesheet" type="text/css" />
<link href="${jqueryNotifyStyle}" rel="stylesheet" type="text/css" />
<!--[if IE]><link href="${entityComparisonStyleIEHack}" rel="stylesheet" type="text/css" /><![endif]-->


<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
	
var contextPath = "${urls.base}";
var temporalGraphDownloadFile = "${TemporalGraphDownloadFile}"
var temporalGraphSmallIcon = "${temporalGraphSmallIcon}";
var subOrganizationVivoProfileURL = "${subOrganizationVivoProfileURL}";
var subOrganizationTemporalGraphURL = "${subOrganizationTemporalGraphURL}";

</script>

<script type="text/javascript">

	$(document).ready(function() {
	
		var jsonString = '${jsonContent}';
		var organizationLabel = '${organizationLabel}';
		
		/* This is used to cache the current state whether the user is allowed to select more entities from 
		the datatable or not. Once Max number of entity selection is reached the user can no longer select 
		more & this variable will be set to false. */
		$("#datatable").data("isEntitySelectionAllowed", true);
		
		$("#organizationLabel").text(organizationLabel).css("color", "#2485ae");
		$("#organizationMoniker").text(organizationLabel);
		$("#organizationMoniker").attr("href", "${organizationVivoProfileURL}");
		
		$notificationContainer = $("#notification-container").notify();
		
		var jsonObject = {
				prepare : function(arg1){
					loadData(arg1);
				}
			};
		
		graphContainer = $("#graphContainer");
		tableDiv = $('#paginatedTable');

 		// initial display of the grid when the page loads
 		init(graphContainer);
		
		/*
		 * When the intra-entity parameters are clicked,
		 * update the status accordingly.	
		 */	        
        $("select.comparisonValues").click(function(){
            var selectedValue = $("select.comparisonValues option:selected").val();
			$("#comparisonParameter").text("Total Number of " + selectedValue);
			$('#yaxislabel').html("Number of " + selectedValue).mbFlipText(false);
			$('#yaxislabel').css("color", "#595B5B");
			$('#comparisonHeader').html(selectedValue).css('font-weight', 'bold');
        });

		//click event handler for clear button
		$("a#clear").click(function(){
			clearRenderedObjects();
		});	
		
		$("input[type=checkbox].easyDeselectCheckbox").live('click', function(){
			
			var checkbox = $(this);
			var checkboxValue = $(this).attr("value");
			var linkedCheckbox = labelToCheckedEntities[checkboxValue];
			//console.log('checkbox value is '+ checkboxValue);
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
							
		//parse the json object and pass it to loadData
        jsonObject.prepare(jQuery.parseJSON(jsonString));
        
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
		function loadData(jsonData) {
		
		    // var yearRange;
		    $.each(jsonData, function (index, val) {
		        setOfLabels.push(val.label);
		        labelToEntityRecord[val.label] = val;
		    });
		
		    getEntityVisMode(jsonData);
		    prepareTableForDataTablePagination(jsonData);
		    setEntityLevel();
		
					/*
		           		calcMinandMaxYears(labelToEntityRecord, year);
						yearRange = (year.max - year.min);
		
		           		setLineWidthAndTickSize(yearRange, FlotOptions);     
						setTickSizeOfYAxis(calcMaxOfComparisonParameter(labelToEntityRecord), FlotOptions);
		            */
		
		    $(".disabled-checkbox-event-receiver").live("click", function () {
		
		        if ($(this).next().is(':disabled')) {
		
		            createNotification("error-notification", {
		                title: 'Error',
		                text: 'A Maximum 10 entities can be compared. Please remove some & try again.'
		            }, {
		                custom: true,
		                expires: 3500
		            });
		
		        }
		
		    });
		    
					/*
		             * When the elements in the paginated div
		             * are clicked this event handler is called
		             */
		    $("input.if_clicked_on_school").live('click', function () {
		
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
		This will make sure that top 3 entites are selected by default when the page loads.
		
		*/		
		$.each($("input.if_clicked_on_school"), function(index, checkbox) {
		    	
			    	if (index > 2) {
			    		return false;
			    	}
		    	
		    		$(this).attr('checked', true);
			    	
			        var checkboxValue = $(this).attr("value");
			        var entity = labelToEntityRecord[checkboxValue];
			        
			        performEntityCheckboxSelectedActions(entity, checkboxValue, $(this));
			        
			        performEntityCheckboxClickedRedrawActions();
			        
			    });

	});
	
</script>

<div id="body">

		<h2 style="width: 36%; padding-left:45px;"><a href="" id = "organizationMoniker"></a></h2>
		<div id="leftblock">
			<div id="leftUpper">
				<h3>How do you want to compare?</h3>
				
				<div style="text-align: left;">
				
				<select class="comparisonValues" style="margin-bottom: 20px;">
					<option value="Publications" selected="selected">by Publications</option>
					<option value="Grants">by Grants</option>
<#--
					<option value="People" disabled="disabled">by People</option>
					<option value="Item4" disabled="disabled">by Item4</option>
					<option value="Item5" disabled="disabled">by Item5</option>
-->
				</select>
				
				</div>
			</div>
						
			<div id="leftLower">
				<div id="notification-container" style="display:none">
		
					<div id="error-notification" class="ui-state-error" style="padding:10px; -moz-box-shadow:0 0 6px #980000; -webkit-box-shadow:0 0 6px #980000; box-shadow:0 0 6px #980000;">
						<a class="ui-notify-close" href="#"><span class="ui-icon ui-icon-close" style="float:right"></span></a>
						<span style="float:left; margin:0 5px 0 0;" class="ui-icon ui-icon-alert"></span>
						<h1>&#035;{title}</h1>
						<p>&#035;{text}</p>
						<p style="text-align:center"><a class="ui-notify-close" href="#">Close Me</a></p>
					</div>
					
					<div id="warning-notification">
					<a class="ui-notify-close ui-notify-cross" href="#">x</a>
						<h1>&#035;{title}</h1>
						<p>&#035;{text}</p>
					</div>
				
				</div>
				<h3>Who do you want to compare?</h3>
				<div id="paginatedTable">
				</div>
			</div>
<#--		
			<div id = "stopwordsdiv">
				* The entity types core:Person, foaf:Organization have been excluded as they are too general.
			</div>	
-->			
		</div>
		<div id="rightblock">
		
			<h4 id="headerText" style="padding-left:60px;">Comparing <span id="comparisonHeader">Publications</span> of <span id="entityHeader">Institutions</span> in <span id="organizationLabel"></span></h4>
			<div id="graphContainer" style="width: 450px; height: 250px;"></div>
			<div id="yaxislabel"></div>
			
			<div id="bottom" style="width: 450px; height: 350px;">
				<div id="xaxislabel">Year</div>
		
				<div id="bottomButtons">
					<a id="clear" style="cursor:pointer;" class="temporalGraphLinks" >Remove All</a>
					<a id="csv" href="${TemporalGraphDownloadFile}" class="temporalGraphLinks" >Save as CSV</a>
<#--					<a id="image" href = "" class="temporalGraphLinks" onClick="window.print()"> Save as Image</a>				
-->				</div><br/>
		
				<h4><span id="comparisonParameter"></span></h4>
		
			<p class="displayCounter">You have selected <span id="counter">0</span> of a maximum <span
			id="total">10</span> <span id="entityleveltext"> schools</span> to compare.</p>
		
			</div>
		</div>		
</div>