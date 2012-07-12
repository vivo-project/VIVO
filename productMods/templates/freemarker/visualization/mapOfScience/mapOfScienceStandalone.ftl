<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->


<#include "mapOfScienceSetup.ftl">

<div id="map-of-science-response">

<#--
<div id="subject-parent-entity" class="hide-dom-on-init">
	<a id="subject-parent-entity-profile-url" href="#" title="parent entity"></a>&nbsp;
    <a id="subject-parent-entity-temporal-url" href="#" title="map of science"><img src="${mapOfScienceIcon}" width="15px" height="15px"/></a>
</div>
-->
        
<h2 id="header-entity-label" class="hide-dom-on-init"><span><a id="entityMoniker" href="${entityVivoProfileURL}" title="entity label">${entityLabel}</a></span></h2>

<div id="map-of-science-info" class="hide-dom-on-init"> Explore activity (<span id="mapped-publications" style="font-weight: bold"></span> publications) across 554 scientific subdisciplines 
	<img class="filterInfoIcon" id="imageIconOne"  src="${urls.images}/iconInfo.png" 
		alt="information icon" 
    	title="" /> 
</div>

<div id="left-column" class="hide-dom-on-init">
	<div id="notification-container" style="display:none">
        <div id="error-notification" class="ui-state-error" style="padding:10px; -moz-box-shadow:0 0 6px #980000; -webkit-box-shadow:0 0 6px #980000; box-shadow:0 0 6px #980000;">
            <a class="ui-notify-close" href="#" title="error notification"><span class="ui-icon ui-icon-close" style="float:right"></span></a>
            <span style="float:left; margin:0 5px 0 0;" class="ui-icon ui-icon-alert"></span>
            <h1>&#035;{title}</h1>
            <p>&#035;{text}</p>
            <p style="text-align:center"><a class="ui-notify-close" href="#">Close Me</a></p>
        </div>
        
        <div id="warning-notification" class="ui-state-highlight ui-corner-all" >
	        <a class="ui-notify-close ui-notify-cross" href="#" title="error notification">x</a>
	        <span style="float: left; margin-right: 0.3em;" class="ui-icon ui-icon-info"></span>
	        <h1>&#035;{title}</h1>
	        <p>&#035;{text}</p>
        </div>
    </div>
    
    <#-- VIEW TYPE FILTER -->
    <div id="view-type-filter" style="display:${viewTypeFilterDisplay};">
    	<input type="radio" name="view-type" value="ENTITY"> Explore ${entityLabel} </input>
    	<img class="filterInfoIcon" id="exploreInfoIcon" src="${urls.images}/iconInfo.png" alt="information icon" title="" /><br>
		<input type="radio" name="view-type" value="COMPARISON"> Compare organizations <#--/ people --></input>
		<img class="filterInfoIcon" id="compareInfoIcon" src="${urls.images}/iconInfo.png" alt="information icon" title="" /><br><br>
    </div>
    
    <!-- <h3>What do you want to compare?</h3> -->
    
    <div id="main-science-areas-table-container"></div>

</div>		

<div id="right-column">
	<div id="map_area"></div>
	<div id="percent-mapped-info">
		mapped <span id="percent-mapped"></span>% of <span id="total-publications"></span> publications
		<img class="filterInfoIcon" id="imageIconThree" src="${urls.images}/iconInfo.png" alt="information icon" title="" />
			
		<div id="download-unlocated-journal-info">
			<a href="${entityMapOfScienceUnlocatedJournalsCSVURL}" title="save unmapped publications">Save Unmapped Publications</a>
		</div>
	</div>
	<br />
	<div id="subEntityTableArea"></div>
</div>


</div>

<#-- START TOOLTIP TEXT -->

<div id="toolTipOne" style="display:none;">
VIVO's Map of Science visualization depicts the topical expertise a university, organization, or person has 
based on past publications loaded into VIVO. Shown here is the expertise profile of the ${entityLabel}--larger 
circle sizes denote more publications per topic area.<br /><br />

<a href='${subEntityMapOfScienceCommonURL}about'>Learn more about VIVO's Map of Science visualization?</a>
</div>

<div id="toolTipTwo" style="display:none;">
The table below summarizes the publications plotted on the Map of Science. Each row corresponds to a 
(sub)discipline on the map<br /><br />

The <b># of pubs.</b> column shows how many of the publications were mapped to each (sub)discipline. This count can be 
fractional because some publication venues are associated with more than one (sub)discipline. Each publication 
in such a venue contributes fractionally to all associated (sub)disciplines according to a weighting scheme.<br /><br />

The <b>% of activity</b> column shows what proportion of the publications were mapped to each (sub)discipline.

</div>

<div id="toolTipThree" style="display:none;">
This visualization is based on the publications we were able to 'science locate' for ${entityLabel}, and 
therefore it may not be fully representative of the overall publication activity for ${entityLabel}.<br /><br />

The publication coverage of this visualization can be improved by including more publication data in the VIVO 
system, and by ensuring that each publication in the VIVO system is associated with a journal that the Map of 
Science recognizes (based on the holdings of Thomson's ISI database and Elsevier's Scopus database). Journal 
names containing typos or other idiosyncrasies may need to be cleaned up before they are recognized. You may 
contact a VIVO system administrator if publication coverage is a concern.</div>

<div id="exploreTooltipText" style="display:none;">
	Overlay and examine expertise profiles for a organization. Color coding by discipline.
</div>

<div id="compareTooltipText" style="display:none;">
	Overlay and examine expertise profiles for one or more organizations. Color coding by organization.
</div>

<div id="searchInfoTooltipText" style="display:none;">
	List only (sub)disciplines whose names contain this text.
</div>


<#-- COMPARISON TOOLTIP TEXT -->

<div id="comparisonToolTipTwo" style="display:none;">
The listed organizations are children of the ${entityLabel} node in the organizational hierarchy. 
You may 'drill down' to see the organizations below a given sub-organization by selecting the chart icon 
next to a selected sub-organization's name below the graph on the right.
<br /><br />

The <b># of pubs.</b> column shows how many of the publications were mapped to each subdiscipline. This 
count can be fractional because some publication venues are associated with more than one subdiscipline. 
Each publication in such a venue contributes fractionally to all associated subdisciplines according to 
a weighting scheme. 

<br /><br />
The <b>% of activity</b> column shows what proportion of the publications were mapped to each subdiscipline.

</div>

<div id="comparisonSearchInfoTooltipText" style="display:none;">
	<!-- Search for specific subdiscipline (or discipline) label in the first column of the table. -->
	List only organizations <!--(or people) -->whose name contains this text.
</div>
<#-- END TOOLTIP TEXT -->

<div id="error-container">

<h1 id="noPublications-header">${entityLabel}</h1>
    <h3 id="vis-title">Map of Science Visualization</h3>
    <div id="error-body">
        <p><span id="variable-error-text">No publications in the system have been attributed to this organization.</span><hr /> 
        Please visit the ${entityLabel} <a href="${entityVivoProfileURL}">profile page</a> for a complete overview.</p>
    </div>
</div>

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>')}