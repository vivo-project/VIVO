<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<div id="body">
        <#if (builtFromCacheTime??) >
            <div class="cache-info-vis">${i18n().using_cache_time} ${builtFromCacheTime?time} (${builtFromCacheTime?date?string("MMM dd yyyy")})</div>
        </#if>
        <h2 id="header-entity-label">
            <span>
                <a id="organizationMoniker" href="${organizationVivoProfileURL}" title="${i18n().organization_name}">${organizationLabel}</a>
            </span>
            <span id="subject-parent-entity">
                <span>|&nbsp;&nbsp;</span>
                <a id="subject-parent-entity-profile-url" href="#" title="${i18n().parent_organization_of} ${organizationLabel}"></a>&nbsp;
                <a id="subject-parent-entity-temporal-url" href="#" title="${i18n().temporal_graph_drill_up}">
                    <img src="${temporalGraphDrillUpIcon}" width="15px" height="15px" alt="${i18n().temporal_graph_drill_up}"/>
                </a>
            </span>
        </h2>
        <br>
        
        <div id="leftblock">
            <div id="leftUpper">
                <h3>${i18n().how_to_compare}</h3>
                
                <div style="text-align: left;">
                
                <select class="comparisonValues" style="margin-bottom: 20px;">
                
                <#assign currentViewLink = "${i18n().no_view_link}">
                
                <#list parameterOptions as parameter>
                    <#if currentParameter = parameter.name>
                    
                        <#assign selectedText = "selected='selected'">
                        <#assign currentViewLink = parameter.viewLink>

                    <#else>
                    
                        <#assign selectedText = "">
                        
                    </#if>
                    <option value="${parameter.value}" ${selectedText}>${parameter.dropDownText}</option>
                </#list>

                </select>
                
                <img id="copy-vis-viewlink-icon" title="${i18n().persistent_link_to_visualization}" class="middle" src="${urls.images}/individual/uriIcon.gif" alt="${i18n().uri_icon}" />
                <span id="copy-vis-viewlink"><input type="text" size="21" value="${currentViewLink}" /></span>
                
                </div>
            </div>
                        
            <div id="leftLower">
                <div id="notification-container" style="display:none">
        
                    <div id="error-notification" class="ui-state-error" style="padding:10px; -moz-box-shadow:0 0 6px #980000; -webkit-box-shadow:0 0 6px #980000; box-shadow:0 0 6px #980000;">
                        <a class="ui-notify-close" href="#" title="${i18n().error_notification}"><span class="ui-icon ui-icon-close" style="float:right"></span></a>
                        <span style="float:left; margin:0 5px 0 0;" class="ui-icon ui-icon-alert"></span>
                        <h1>&#035;{title!}</h1>
                        <p>&#035;{text}</p>
                        <p style="text-align:center"><a class="ui-notify-close" href="#">${i18n().close_me}</a></p>
                    </div>
                    
                    <div id="warning-notification" class="ui-state-highlight ui-corner-all" >
                    <a class="ui-notify-close ui-notify-cross" href="#" title="${i18n().error_notification}">x</a>
                    <span style="float: left; margin-right: 0.3em;" class="ui-icon ui-icon-info"></span>
                        <h1>&#035;{title!}</h1>
                        <p>&#035;{text}</p>
                    </div>
                
                </div>
                <div id="people-organizations-filter">
                  <h3>${i18n().what_to_compare}</h3>
                	<span id="organizations-filter" class="filter-option">${i18n().organizations_capitalized}</span> | 
                	<span id="people-filter" class="filter-option">${i18n().people_capitalized}</span> | 
                  <span id="no-filter" class="filter-option">${i18n().view_all}</span>
                	<img class="filterInfoIcon" src="${urls.images}/iconInfo.png" 
                		 alt="${i18n().info_icon}" 
                		 title="${i18n().organization_hierarchy_note(organizationLabel)}" />
                </div>
                
                <div id="paginatedTable"></div>
                <div id="paginated-table-footer">
                <a id="csv" href="${temporalGraphDownloadFileLink}" class="temporalGraphLinks" title="${i18n().save_all_as_csv}">${i18n().save_all_as_csv}</a>
                <a class="clear-selected-entities temporalGraphLinks" title="${i18n().clear_all_selected_entities}">${i18n().clear_capitalized}</a>
                </div>
            </div>
<#--        
            <div id = "stopwordsdiv">
                * The entity types core:Person, foaf:Organization have been excluded as they are too general.
            </div>  
-->         
        </div>
        
        <div id="rightblock">
        
            <h3 id="headerText">${i18n().comparing_capitalized} <span id="comparisonHeader">${currentParameterObject.value}</span> ${i18n().of} <span id="entityHeader">${i18n().institutions_capitalized}</span> ${i18n().in} ${organizationLabel}</h3>
            
            <div id="temporal-graph">
                <div id="yaxislabel"></div>
                <div id="graphContainer"></div>
                <div id="xaxislabel">${i18n().year_capitalized}</div>
            </div>
        
            <div id="bottom">
                <h3><span id="comparisonParameter"></span>&nbsp;
                <img id="incomplete-data-disclaimer" class="infoIcon" src="${urls.images}/iconInfo.png" alt="${i18n().info_icon}" title="${i18n().info_based_on_vivo_data(currentParameterObject.value)}"  width="15px" height="15px"/></h3>
            <p class="displayCounter">${i18n().you_have_selected} <span id="counter">0</span> ${i18n().of_a_maximum} 
            <span id="total">10</span> <span id="entityleveltext"> ${i18n().schools}</span>. 
            <span id="legend-row-header"> 
            <a class="clear-selected-entities temporalGraphLinks" title="${i18n().clear_all_selected_entities}">${i18n().clear_capitalized}</a>
            </span>
            </p>
        
            </div>
            
            <p class="displayCounter">${i18n().legend_capitalized}</p>
            <span class="legend-bar unknown-legend-bar"><span style="width: 25px; margin-bottom:3px;" class="unknown-inner-bar">&nbsp;</span></span> <span id="legend-unknown-bar-text">${currentParameterObject.name} ${i18n().with_unknown_year}</span><br />
            <span style="background-color: #A8A8A8; width: 25px;" class="known-bar legend-bar">&nbsp;</span> <span id="legend-known-bar-text">${currentParameterObject.name} ${i18n().with_known_year}</span><br />
            <span style="background-color: #CDCDCD; width: 25px;" class="current-year-legend-bar legend-bar">&nbsp;</span> <span id="legend-current-year-bar-text">${currentParameterObject.name} ${i18n().from_current_incomplete_year}</span>
        </div>      
</div>
