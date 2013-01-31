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
    
            function drawPubCountVisualization() {
                var unknownYearPublicationCounts = ${sparklineVO.unknownYearPublications};
                var onlyUnknownYearPublications = false;
                
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Year');
                data.addColumn('number', 'Publications');
                data.addRows(${sparklineVO.yearToEntityCountDataTable?size});
                
                var knownYearPublicationCounts = 0;
        
                <#list sparklineVO.yearToEntityCountDataTable as yearToPublicationCountDataElement>                        
                    data.setValue(${yearToPublicationCountDataElement.yearToEntityCounter}, 0, '${yearToPublicationCountDataElement.year}');
                    data.setValue(${yearToPublicationCountDataElement.yearToEntityCounter}, 1, ${yearToPublicationCountDataElement.currentEntitiesCount});
                    knownYearPublicationCounts += ${yearToPublicationCountDataElement.currentEntitiesCount};
                </#list>
            
                <#-- Create a view of the data containing only the column pertaining to publication count. -->
                var sparklineDataView = new google.visualization.DataView(data);
                sparklineDataView.setColumns([1]);

                /*
                This means that all the publications have unknown years & we do not need to display
                the sparkline.
                */            
                if (unknownYearPublicationCounts > 0 && knownYearPublicationCounts < 1) {
                    
                    onlyUnknownYearPublications = true;
                    
                } 
                
                var unknownYearPublicationCounts = ${sparklineVO.unknownYearPublications};
                var totalPublicationCount = knownYearPublicationCounts + unknownYearPublicationCounts;

                var shortSparkRows = sparklineDataView.getViewRows();
                var renderedShortSparks = 0;
                $.each(shortSparkRows, function(index, value) {
                    renderedShortSparks += data.getValue(value, 1);
                });

                var tenYearCount = onlyUnknownYearPublications ? unknownYearPublicationCounts : renderedShortSparks;

                var td1Text = totalPublicationCount;
                var td2Text = "";
                var infoImgText = "<img class='infoIcon' src='" + infoIconSrc + "' height='14px' width='14px' alt='information icon' title='These numbers are based solely on publications that have been loaded into this VIVO application. If this is your profile, you can enter additional publications below.' />";

                if ( !onlyUnknownYearPublications ) {
                    if ( td1Text == tenYearCount ) {
                        td2Text += "in the last 10 full years " ;
                    }
                    else {
                        td2Text += "<strong>" + tenYearCount + "</strong> in the last 10 full years " ;
                    }
                    
                    if ( tenYearCount < 100 ) {
                        td2Text += infoImgText;
                    } 
                }
                else {
                    td2Text += "total <img class='infoIcon' src='" + infoIconSrc + "' height='14px' width='14px' alt='information icon' title='These numbers are based solely on publications that have been loaded into this VIVO application. If this is your profile, you can enter additional publications below.' />";
                }
         
                $('#${sparklineContainerID} td#totalPubs').html(td1Text);
                $('#${sparklineContainerID} td#tenYearCount').html(td2Text);
                         
                if ( td1Text > 99 && td1Text < 1000 ) {
                    $('#${sparklineContainerID} td#totalPubs').attr('style','font-size:1.25em');
                }
                else if ( td1Text > 999 ) {
                    $('#${sparklineContainerID} td#totalPubs').attr('style','font-size:1.1em');
                }
            }
    

            $(document).ready(function() {
                var sparklineImgTD; 
        
        
                if ($('#${sparklineContainerID}').length === 0) {
        
                    $('<div/>', {
                        'id': '${sparklineContainerID}',
                    }).prependTo('#${visContainerID}');
                    $('#${sparklineContainerID}').css('margin-top','4px').css('height','48px');
                    var table = $('<table>');
                    var row = $('<tr>');
                    var totalPubs = $('<td>');
                    totalPubs.attr('id', 'totalPubs');
                    totalPubs.attr('id', 'totalPubs');
                    var tenYearCount = $('<td>');
                    tenYearCount.attr('id', 'tenYearCount');
                    row.append(totalPubs);
                    row.append(tenYearCount);
                    table.append(row);
                    table.prependTo('#${sparklineContainerID}');
                }
            
                drawPubCountVisualization();
            });
        </script>
         
            </div> <!-- Sparkline Viz -->

        </div>
