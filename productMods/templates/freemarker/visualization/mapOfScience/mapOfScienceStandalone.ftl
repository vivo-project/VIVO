<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->


<#include "mapOfScienceSetup.ftl">

<div id="map-of-science-response">
<div id="left-column">
	<div id="notification-container" style="display:none">
        <div id="error-notification" class="ui-state-error" style="padding:10px; -moz-box-shadow:0 0 6px #980000; -webkit-box-shadow:0 0 6px #980000; box-shadow:0 0 6px #980000;">
            <a class="ui-notify-close" href="#"><span class="ui-icon ui-icon-close" style="float:right"></span></a>
            <span style="float:left; margin:0 5px 0 0;" class="ui-icon ui-icon-alert"></span>
            <h1>&#035;{title}</h1>
            <p>&#035;{text}</p>
            <p style="text-align:center"><a class="ui-notify-close" href="#">Close Me</a></p>
        </div>
        
        <div id="warning-notification" class="ui-state-highlight ui-corner-all" >
        <a class="ui-notify-close ui-notify-cross" href="#">x</a>
        <span style="float: left; margin-right: 0.3em;" class="ui-icon ui-icon-info"></span>
            <h1>&#035;{title}</h1>
            <p>&#035;{text}</p>
        </div>
    
    </div>
    
    <!-- <h3>What do you want to compare?</h3> -->
    <div id="science-areas-filter">
    	<span id="discipline-filter" class="filter-option active-filter">13 Disciplines</span> | 
    	<span id="subdisciplines-filter" class="filter-option">554 Sub-Disciplines</span>
    	<img class="filterInfoIcon" src="${urls.images}/iconInfo.png" 
    		 alt="information icon" 
    		 title="Spiel on Discuipline vs Sub-Discipline" />
    </div>
    
    <div id="main-science-areas-table-container"></div>
    <div id="main-science-areas-table-footer">
    <a id="csv" href="${entityMapOfScienceDisciplineCSVURL}" class="map-of-science-links">Export Complete Table</a>
    <#-- <a class="clear-selected-entities map-of-science-links" title="Clear all selected entities.">Clear</a> -->
    </div>
</div>		

<div id="right-column"><div id="map_area"></div></div>
</div>