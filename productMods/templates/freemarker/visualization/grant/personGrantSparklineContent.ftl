<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#assign visContainerID = '${sparklineVO.visContainerDivID}'>

<#if sparklineVO.shortVisMode>
    <#assign sparklineContainerID = 'grant_count_short_sparkline_vis'>
<#else>
    <#assign sparklineContainerID = 'grant_count_full_sparkline_vis'>
</#if>

<#-- This is used to prevent collision between sparkline & visualization conatiner div ids. -->
<#if visContainerID?upper_case == sparklineContainerID?upper_case>
    <#assign sparklineContainerID = visContainerID + "_spark"> 
</#if>

<div class="staticPageBackground">
    <div id="${visContainerID}">
        <script type="text/javascript">
            <#if sparklineVO.shortVisMode>
                var visualizationOptions = {
                    width: 150,
                    height: 60,
                    color: '3399CC',
                    chartType: 'ls',
                    chartLabel: 'r'
                };
            <#else>
                var visualizationOptions = {
                    width: 250,
                    height: 75,
                    color: '3399CC',
                    chartType: 'ls',
                    chartLabel: 'r'
                };
            </#if>

            function drawGrantCountVisualization(providedSparklineImgTD) {
            
                var unknownYearGrantCounts = ${sparklineVO.unknownYearGrants};
                var onlyUnknownYearGrants = false;
    
                var data = new google.visualization.DataTable();
                data.addColumn('string', '${i18n().year_capitalized}');
                data.addColumn('number', '${i18n().grants_capitalized}');
                data.addRows(${sparklineVO.yearToEntityCountDataTable?size});
        
                var knownYearGrantCounts = 0;
                
                <#list sparklineVO.yearToEntityCountDataTable as yearToGrantCountDataElement>                        
                    data.setValue(${yearToGrantCountDataElement.yearToEntityCounter}, 0, '${yearToGrantCountDataElement.year}');
                    data.setValue(${yearToGrantCountDataElement.yearToEntityCounter}, 1, ${yearToGrantCountDataElement.currentEntitiesCount});
                    knownYearGrantCounts += ${yearToGrantCountDataElement.currentEntitiesCount};
                </#list>
        
                <#-- Create a view of the data containing only the column pertaining to grant count. -->
                var sparklineDataView = new google.visualization.DataView(data);
                sparklineDataView.setColumns([1]);
        
                <#if sparklineVO.shortVisMode>
         
                <#-- For the short view we only want the last 10 year's view of grant count, hence we filter 
                    the data we actually want to use for render. -->
         
                sparklineDataView.setRows(data.getFilteredRows([{
                        column: 0,
                        minValue: '${sparklineVO.earliestRenderedGrantYear?c}',
                        maxValue: '${sparklineVO.latestRenderedGrantYear?c}'
                }]));

                <#else>

                </#if>
                

                /*
                This means that all the publications have unknown years & we do not need to display
                the sparkline.
                */            
                if (unknownYearGrantCounts > 0 && knownYearGrantCounts < 1) {
                    
                    onlyUnknownYearGrants = true;
                    
                } else {

                /* 
                Test if we want to go for the approach when serving visualizations from a secure site..
                If "https:" is not found in location.protocol then we do everything normally.
                */
                if (location.protocol.indexOf("https") == -1) {
                    /*
                    This condition will make sure that the location protocol (http, https, etc) does not have 
                    for word https in it.
                    */
                
                <#-- Create the vis object and draw it in the div pertaining to sparkline. -->
                var sparkline = new google.visualization.ImageSparkLine(providedSparklineImgTD[0]);
                sparkline.draw(sparklineDataView, {
                        width: visualizationOptions.width,
                        height: visualizationOptions.height,
                        showAxisLines: false,
                        showValueLabels: false,
                        labelPosition: 'none'
                });  
                
                
                } else {
                
                    <#-- Prepare data for generating google chart URL. -->
                    
                    <#-- If we need to serve data for https:, we have to create an array of values to be plotted. -->
                    var chartValuesForEncoding = new Array();
                    
                    $.each(sparklineDataView.getViewRows(), function(index, value) {
                        chartValuesForEncoding.push(data.getValue(value, 1));
                    });
                    
                    var chartImageURL = constructVisualizationURLForSparkline(
                                extendedEncodeDataForChartURL(chartValuesForEncoding, 
                                                              sparklineDataView.getColumnRange(0).max), 
                                visualizationOptions);

                    var imageContainer = $(providedSparklineImgTD[0]);
                    
                    imageContainer.image(chartImageURL, 
                            function(){
                                imageContainer.empty().append(this); 
                                $(this).addClass("google-visualization-sparkline-image");
                            },
                            function(){
                                // For performing any action on failure to
                                // find the image.
                                imageContainer.empty();
                            }
                    );
                
                }
                
                }   
                
                var totalGrantCount = knownYearGrantCounts + unknownYearGrantCounts; 
                
                <#if sparklineVO.shortVisMode>
         
                    <#-- We want to display how many grant counts were considered, so this is used to calculate this. -->
         
                    var shortSparkRows = sparklineDataView.getViewRows();
                    var renderedShortSparks = 0;
                    $.each(shortSparkRows, function(index, value) {
                        renderedShortSparks += data.getValue(value, 1);
                    });
                    
                    /*
                    In case that there are only unknown grants we want the text to mention these counts,
                    which would not be mentioned in the other case because the renderedShortSparks only hold counts
                    of grants which have any date associated with it.
                    */
                    var totalGrants = onlyUnknownYearGrants ? unknownYearGrantCounts : renderedShortSparks;
                    
                    if (totalGrants === 1) {
                        var grantDisplay = "${i18n().grant}";
                    } else {
                        var grantDisplay = "${i18n().grants}";
                    }
                    
                    $('#${sparklineContainerID} td.sparkline_number').text(totalGrants).css("font-weight", "bold").attr("class", "grey").append("<span style='color: #2485AE;'> " + grantDisplay + " <br/></span>");
            
                    var sparksText = '  ${i18n().within_last_10_years}';
                    
                    if (totalGrants !== totalGrantCount) {
                        sparksText += ' (' + totalGrantCount + ' ${i18n().total})';
                    }
            
                 <#else>
                 
                    /*
                     * Sparks that will be rendered will always be the one's which has 
                     * any year associated with it. Hence.
                     * */
                    var renderedSparks = ${sparklineVO.renderedSparks};  
                    
                    /*
                    In case that there are only unknown grants we want the text to mention these counts,
                    which would not be mentioned in the other case because the renderedSparks only hold counts
                    of grants which have any date associated with it.
                    */
                    var totalGrants = onlyUnknownYearGrants ? unknownYearGrantCounts : renderedSparks;
                    
                    if (totalGrants === 1) {
                        var grantDisplay = "${i18n().grant}";
                    } else {
                        var grantDisplay = "${i18n().grants}";
                    }
                        
                    $('#${sparklineContainerID} td.sparkline_number').text(totalGrants).css("font-weight", "bold").attr("class", "grey").append("<span style='color: #2485AE;'> " + grantDisplay + " <br/></span>");
            
                    var sparksText = '  from <span class="sparkline_range">${sparklineVO.earliestYearConsidered?c}' 
                                        + ' through ${sparklineVO.latestRenderedGrantYear?c}</span>';
                                        
                    if (totalGrants !== totalGrantCount) {
                        sparksText += ' (' + totalGrantCount + ' ${i18n().total})';
                    }                                        
                               
                    if (totalGrantCount) {
                        sparksText += '<br /> <a href="${sparklineVO.downloadDataLink}"  title=".csv ${i18n().file}">(.CSV ${i18n().file_capitalized})</a> ';                    
                    }                                                
                                         
                 </#if>
         
                 if (!onlyUnknownYearGrants) {
                    $('#${sparklineContainerID} td.sparkline_text').html(sparksText);
                 }
         
            }
                 
            /*
             * This will activate the visualization. It takes care of creating 
             * div elements to hold the actual sparkline image and then calling the 
             * drawGrantCountVisualization function. 
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
                    sparklineImgTD.attr('width', visualizationOptions.width);
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
                    sparklineTextTD.css('text-align', 'left');
                    row3.append(sparklineTextTD);
                    table.append(row);
                    table.append(row2);
                    table.append(row3);
                    table.prependTo('#${sparklineContainerID}');
                }
                
                drawGrantCountVisualization(sparklineImgTD);
            });
        </script>  
        
    </div><!-- Sparkline Viz -->      
    
    <#if sparklineVO.shortVisMode>
        <#--<span class="vis_link">-->
            <p><a class="all-vivo-grants" href="${sparklineVO.fullTimelineNetworkLink}" title="${i18n().view_all_grants}">${i18n().view_all_grants_text}</a></p>
        <#--</span>-->
    <#else>
        <!-- For Full Sparkline - Print the Table of Grant Counts per Year -->
        <#if displayTable?? && displayTable>
        
            <p> 
                <#assign tableID = "grant_sparkline_data_table" />
                <#assign tableCaption = "${i18n().grant_per_year}" />
                <#assign tableActivityColumnName = "${i18n().grants_capitalized}" />
                <#assign tableContent = sparklineVO.yearToActivityCount />
                <#assign fileDownloadLink = sparklineVO.downloadDataLink />
                
                <#include "yearToActivityCountTable.ftl">
    
                ${i18n().download_data_as} <a href="${sparklineVO.downloadDataLink}" title=".csv ${i18n().link}">.csv</a> ${i18n().file}.
                <br />
            </p>
        
        
        </#if>
        

    </#if>
</div>