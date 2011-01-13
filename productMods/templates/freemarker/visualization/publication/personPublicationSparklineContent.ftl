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
        <script type="text/javascript">
    
            function drawPubCountVisualization(providedSparklineImgTD) {
    
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Year');
                data.addColumn('number', 'Publications');
                data.addRows(${sparklineVO.yearToEntityCountDataTable?size});
        
                <#list sparklineVO.yearToEntityCountDataTable as yearToPublicationCountDataElement>                        
                    data.setValue(${yearToPublicationCountDataElement.yearToEntityCounter}, 0, '${yearToPublicationCountDataElement.year}');
                    data.setValue(${yearToPublicationCountDataElement.yearToEntityCounter}, 1, ${yearToPublicationCountDataElement.currentEntitiesCount});
                </#list>
        
                <#-- Create a view of the data containing only the column pertaining to publication count. -->
                var sparklineDataView = new google.visualization.DataView(data);
                sparklineDataView.setColumns([1]);
        
                <#if sparklineVO.shortVisMode>
         
                <#-- For the short view we only want the last 10 year's view of publication count, hence we filter 
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
                        width: 150,
                        height: 60,
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
         
                    var totalPubs = parseInt(renderedShortSparks) + parseInt(${sparklineVO.unknownYearPublications});
                    
                    if ( totalPubs == 1 ) {
                        var pubDisplay = "publication";
                    } else {
                        var pubDisplay = "publications";
                    }
                    
                    $('#${sparklineContainerID} td.sparkline_number').text(totalPubs).css("font-weight", "bold").attr("class", "grey").append("<span style='color: #2485AE;'> "+ pubDisplay +"<br/></span>");
            
                    var sparksText = ' within the last 10 years';
            
                 <#else>
            
                    /*
                     * Sparks that will be rendered will always be the one's which has 
                     * any year associated with it. Hence.
                     * */
                    var renderedSparks = ${sparklineVO.renderedSparks};      
                    var totalPubs = parseInt(renderedSparks) + parseInt(${sparklineVO.unknownYearPublications});
                    
                    if ( totalPubs == 1 ) {
                        var pubDisplay = "publication";
                    } else {
                        var pubDisplay = "publications";
                    }
                    
                    $('#${sparklineContainerID} td.sparkline_number').text(totalPubs).css("font-weight", "bold").attr("class", "grey").append("<span style='color: #2485AE;'> "+ pubDisplay +"<br/></span>");
            
                    var sparksText = '  from <span class="sparkline_range">${sparklineVO.earliestYearConsidered?c}' 
                                        + ' to ${sparklineVO.latestRenderedPublicationYear?c}</span> ' 
                                        + ' <br /><a href="${sparklineVO.downloadDataLink}">(.CSV File)</a> ';
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
                    sparklineImgTD.attr('width', '150');
                    sparklineImgTD.attr('class', 'sparkline_style');
            
                    row.append(sparklineImgTD);
            		var row2 = $('<tr>');
                    var sparklineNumberTD = $('<td>');
                    sparklineNumberTD.attr('class', 'sparkline_number');
					sparklineNumberTD.css('text-align', 'left');
                    row2.append(sparklineNumberTD);
                    var row3 = $('<tr>');
                    
                    var sparklineTextTD = $('<td>');
                    sparklineTextTD.attr('class', 'sparkline_text');
                    row3.append(sparklineTextTD);
                    table.append(row);
                    table.append(row2);
                    table.append(row3);
                    table.prependTo('#${sparklineContainerID}');
            
                }
            
                drawPubCountVisualization(sparklineImgTD);
            });
        </script>
         
    </div><!-- Sparkline Viz -->

    <#if sparklineVO.shortVisMode>
    
    <#-- Shifted the link to co-author to the individual-sparkline.ftl instead. --> 
    
    <#else>
        <!-- For Full Sparkline - Print the Table of Publication Counts per Year -->
        
        <#if displayTable?? && displayTable>
        
	        <p>	
				<#assign tableID = "publications_sparkline_data_table" />
				<#assign tableCaption = "Publications per year " />
				<#assign tableActivityColumnName = "Publications" />
				<#assign tableContent = sparklineVO.yearToActivityCount />
				<#assign fileDownloadLink = sparklineVO.downloadDataLink />
				
				<#include "yearToActivityCountTable.ftl">
	
	            Download data as <a href="${sparklineVO.downloadDataLink}">.csv</a> file.
	            <br />
	        </p>
        
        
        </#if>
        

    </#if>
</div>