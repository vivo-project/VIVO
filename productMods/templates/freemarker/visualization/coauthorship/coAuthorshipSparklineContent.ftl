<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#assign visContainerID = '${sparklineVO.visContainerDivID}'>

<#if sparklineVO.shortVisMode>
    <#assign sparklineContainerID = 'unique_coauthors_short_sparkline_vis'>
<#else>
    <#assign sparklineContainerID = 'unique_coauthors_full_sparkline_vis'>
</#if>

<#-- This is used to prevent collision between sparkline & visualization conatiner div ids. -->
<#if visContainerID?upper_case == sparklineContainerID?upper_case>
    <#assign sparklineContainerID = visContainerID + "_spark"> 
</#if>

<div class="staticPageBackground">
    <div id="${visContainerID}">
        <script type="text/javascript">
        
            function drawCoauthorsSparklineVisualization(providedSparklineImgTD) {
    
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Year');
                data.addColumn('number', 'Unique co-authors');
                data.addRows(${sparklineVO.yearToEntityCountDataTable?size});
                
                <#list sparklineVO.yearToEntityCountDataTable as yearToUniqueCoauthorsDataElement>                        
                    data.setValue(${yearToUniqueCoauthorsDataElement.yearToEntityCounter}, 0, '${yearToUniqueCoauthorsDataElement.year}');
                    data.setValue(${yearToUniqueCoauthorsDataElement.yearToEntityCounter}, 1, ${yearToUniqueCoauthorsDataElement.currentEntitiesCount});
                </#list>
                
                <#-- Create a view of the data containing only the column pertaining to coauthors count. -->
                var sparklineDataView = new google.visualization.DataView(data);
                sparklineDataView.setColumns([1]);
        
                <#if sparklineVO.shortVisMode>
         
                <#-- For the short view we only want the last 10 year's view of coauthors count, hence we filter 
                    the data we actually want to use for render. -->
         
                sparklineDataView.setRows(data.getFilteredRows([{
                        column: 0,
                        minValue: '${sparklineVO.earliestRenderedPublicationYear?c}',
                        maxValue: '${sparklineVO.latestRenderedPublicationYear?c}'
                }]));
         
                <#else>

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
         
                    <#-- We want to display how many coauthors were considered, so this is used to calculate this. -->
         
                    var shortSparkRows = sparklineDataView.getViewRows();
                    var renderedShortSparks = 0;
                    $.each(shortSparkRows, function(index, value) {
                        renderedShortSparks += data.getValue(value, 1);
                    });
         
                    $('#${sparklineContainerID} td.sparkline_number').text(parseInt(renderedShortSparks) + parseInt(${sparklineVO.unknownYearPublications}));
            
                    var sparksText = ' co-author(s) within the last 10 years <span class="incomplete-data-holder" title="This information'
                                    + ' is based solely on publications which have been loaded into the VIVO system. This may only be a small' 
                                    + ' sample of the person\'s total work.">incomplete list</span>';
            
                 <#else>
            
                    /*
                     * Sparks that will be rendered will always be the one's which has 
                     * any year associated with it. Hence.
                     * */
                    var renderedSparks = ${sparklineVO.renderedSparks};      
                    $('#${sparklineContainerID} td.sparkline_number').text(parseInt(renderedSparks) + parseInt(${sparklineVO.unknownYearPublications}));
            
                    var sparksText = ' co-author(s) from <span class="sparkline_range">${sparklineVO.earliestYearConsidered?c}' 
                                        + ' to ${sparklineVO.latestRenderedPublicationYear?c}</span> ' 
                                        + ' <a href="${sparklineVO.downloadDataLink}" class="inline_href">(.CSV File)</a> ';
                                        
                 </#if>
         
                 $('#${sparklineContainerID} td.sparkline_text').html(sparksText);
         
            }
    
            /*
             * This will activate the visualization. It takes care of creating 
             * div elements to hold the actual sparkline image and then calling the 
             * drawCoauthorsSparklineVisualization function. 
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
            
                drawCoauthorsSparklineVisualization(sparklineImgTD);
            });
        </script>
         
    </div>
    
    <!-- Sparkline Viz -->

    <#if sparklineVO.shortVisMode>
        <#--<span class="vis_link">-->
            <p><a class="all-vivo-publications" href="${sparklineVO.fullTimelineNetworkLink}">View full timeline and co-author network.</a></p>
        <#--</span>-->
    <#else>
        <!-- For Full Sparkline - Print the Table of Couauthor Counts per Year -->
        <p>
            <#if displayTable?? && displayTable>
        
		        <p>	
					<#assign tableID = "sparkline_data_table" />
					<#assign tableCaption = "Unique Co-Authors per year " />
					<#assign tableActivityColumnName = "Count" />
					<#assign tableContent = sparklineVO.yearToActivityCount />
					<#assign fileDownloadLink = sparklineVO.downloadDataLink />
					
					<#include "yearToActivityCountTable.ftl">
		
		            Download data as <a href="${sparklineVO.downloadDataLink}">.csv</a> file.
		            <br />
		        </p>
        
	        </#if>
        
        </p>
    </#if>
</div>