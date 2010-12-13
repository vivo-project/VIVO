<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#assign visContainerID = '${sparklineVO.visContainerDivID}'>

<#if sparklineVO.shortVisMode>
	<#assign sparklineContainerID = 'pub_count_short_sparkline_vis'>
<#else>
	<#assign sparklineContainerID = 'pub_count_full_sparkline_vis'>
</#if>

<#-- This is used to prevent collision between sparkline & visualization conatiner div ids. -->
<#if visContainerID?upper_case == sparklineContainerID?upper_case>
	<#assign sparklineContainerID = visContainerID + "_spark"> 
</#if>

<div class="staticPageBackground">

<div id="${visContainerID}">

<style type='text/css'>
	.sparkline_style table {
		margin: 0;
		padding: 0;
		width: auto;
		border-collapse: collapse;
		border-spacing: 0;
		vertical-align: inherit;
	}
	
	table.sparkline_wrapper_table
	        td,th {
		vertical-align: bottom;
	}
	
	.vis_link a {
		padding-top: 5px;
	}
	
	td.sparkline_number {
		text-align: right;
		padding-right: 5px;
	}
	
	td.sparkline_text {
		text-align: left;
	}
	
	.incomplete-data-holder {
		
	}
</style>

<script type="text/javascript">
	
	function drawPubCountVisualization(providedSparklineImgTD) {
	
		var data = new google.visualization.DataTable();
        data.addColumn('string', 'Year');
        data.addColumn('number', 'Publications');
        data.addRows(${sparklineVO.numOfYearsToBeRendered});
        
        <#list sparklineVO.yearToPublicationCountDataTable as yearToPublicationCountDataElement>                        
			data.setValue(${yearToPublicationCountDataElement.publicationCounter}, 0, '${yearToPublicationCountDataElement.publishedYear}');
            data.setValue(${yearToPublicationCountDataElement.publicationCounter}, 1, ${yearToPublicationCountDataElement.currentPublications});
        </#list>
        
        <#-- Create a view of the data containing only the column pertaining to publication count. -->
        var sparklineDataView = new google.visualization.DataView(data);
        sparklineDataView.setColumns([1]);
        
        <#if sparklineVO.shortVisMode>
         
        console.log("Yay! Short Vis Mode!");
         
        <#-- For the short view we only want the last 10 year's view of publication count, hence we filter 
        	the data we actually want to use for render. -->
         
        sparklineDataView.setRows(data.getFilteredRows([{
                column: 0,
                minValue: '${sparklineVO.earliestRenderedPublicationYear?c}',
                maxValue: '${sparklineVO.latestRenderedPublicationYear?c}'
                /*minValue: '2001',
                maxValue: '2011'*/
        }]));
         
         
        <#else>
         
        console.log("Yay! Full Vis Mode!");
         
        </#if>
         
         
        <#-- Create the vis object and draw it in the div pertaining to sparkline. -->
        var sparkline = new google.visualization.ImageSparkLine(providedSparklineImgTD[0]);
        sparkline.draw(sparklineDataView, {
                width: 65,
                height: 30,
                showAxisLines: false,
                showValueLabels: false,
                labelPosition: 'none'
        });
            
		<#if sparklineVO.shortVisMode>
		 
		   	<#-- We want to display how many publication counts were considered, so this is used to calculate this. -->
		 
			var shortSparkRows = sparklineDataView.getViewRows();
			var renderedShortSparks = 0;
			$.each(shortSparkRows, function(index, value) {
			    renderedShortSparks += data.getValue(value, 1);
			});
		 
		 	$('#${sparklineContainerID} td.sparkline_number').text(parseInt(renderedShortSparks) + parseInt(${sparklineVO.unknownYearPublications}));
		 	
		 	var sparksText = ' publication(s) within the last 10 years <span class="incomplete-data-holder" title="This information'
		 	 				+ ' is based solely on publications which have been loaded into the VIVO system. This may only be a small' 
		 	 				+ ' sample of the person\'s total work.">incomplete list</span>';
		 	
		 <#else>
			
			/*
			 * Sparks that will be rendered will always be the one's which has 
			 * any year associated with it. Hence.
			 * */
			var renderedSparks = ${sparklineVO.renderedSparks};		 
		 	$('#${sparklineContainerID} td.sparkline_number').text(parseInt(renderedSparks) + parseInt(${sparklineVO.unknownYearPublications}));
		 	
		 	var sparksText = ' publication(s) from <span class="sparkline_range">${sparklineVO.earliestYearConsidered?c}' 
		 						+ ' to ${sparklineVO.latestRenderedPublicationYear?c}</span> ' 
		 						+ ' <a href="${sparklineVO.downloadDataLink}" class="inline_href">(.CSV File)</a> ';
		 </#if>
		 
		 $('#${sparklineContainerID} td.sparkline_text').html(sparksText);
		 
	}
	
	/*
	 * This will activate the visualization. It takes care of creating 
	 * div elements to hold the actual sparkline image and then calling the 
	 * drawPubCountVisualization function. 
	 * */

	$(document).ready(function() {
    	var sparklineImgTD; 
    	
	/*
	 * This is a nuclear option (creating the container in which everything goes)
	 * the only reason this will be ever used is the API user never submitted a 
	 * container ID in which everything goes. The alternative was to let the 
	 * vis not appear in the calling page at all. So now atleast vis appears but 
	 * appended at the bottom of the body.
	 * */
	 
		if ($('#${visContainerID}').length === 0) {
	        $('<div/>', {
	            'id': '${visContainerID}'
	        }).appendTo('body');
		}		
		
		if ($('#${sparklineContainerID}').length === 0) {
		
			$('<div/>', {
			    'id': '${sparklineContainerID}',
			    'class': 'sparkline_style'
			}).prependTo('#${visContainerID}');
			
			var table = $('<table>');
            table.attr('class', 'sparkline_wrapper_table');
            var row = $('<tr>');
            sparklineImgTD = $('<td>');
            sparklineImgTD.attr('id', '${sparklineContainerID}_img');
            sparklineImgTD.attr('width', '65');
            sparklineImgTD.attr('align', 'right');
            sparklineImgTD.attr('class', 'sparkline_style');
            
            row.append(sparklineImgTD);
            
            console.log(sparklineImgTD);
            
            var sparklineNumberTD = $('<td>');
            sparklineNumberTD.attr('width', '30');
            sparklineNumberTD.attr('align', 'right');
            sparklineNumberTD.attr('class', 'sparkline_number');
            row.append(sparklineNumberTD);
            var sparklineTextTD = $('<td>');
            sparklineTextTD.attr('width', '450');
            sparklineTextTD.attr('class', 'sparkline_text');
            row.append(sparklineTextTD);
            table.append(row);
            table.prependTo('#${sparklineContainerID}');
            
        }
            
        console.log(sparklineImgTD);
            
        drawPubCountVisualization(sparklineImgTD);
    });
</script>
		 
</div>

<#if sparklineVO.shortVisMode>

<span class="vis_link">
    <a href="${sparklineVO.fullTimelineNetworkLink}">View all VIVO publications and corresponding co-author network.</a>

</span>

<#else>

<!-- For Full Sparkline - Print the Table of Publication Counts per Year -->
<p>
    <table id='sparkline_data_table'>
        <caption>
            Publications per year <a href="${sparklineVO.downloadDataLink}">(.CSV File)</a>
        </caption>
        <thead>
            <tr>
                <th>
                    Year
                </th>
                <th>
                    Publications
                </th>
            </tr>
        </thead>
        <tbody>
        
        <#list sparklineVO.yearToActivityCount?keys as year>
        	<tr>
                <td>
                    ${year}
                </td>
                <td>
                    ${sparklineVO.yearToActivityCount[year]}
                </td>
            </tr>
        </#list>
            
        </tbody>
    </table>
    Download data as <a href="${sparklineVO.downloadDataLink}">.csv</a> file.
    <br />
</p>

</#if>

</div>