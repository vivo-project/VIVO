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
The Map of Science visualization shows the publication activity of any organization, person, or university in 
a VIVO instance mapped by the area of science or subdiscipline being researched.<br /> <br /> 

You can use VIVO's Map of Science visualization to see where ${entityLabel} is active in the world of science, 
<em><b>although only publications that have been loaded into this VIVO site will appear.</b></em><br /><br />

Colored circles are drawn for subdisciplines where the organization has publications. The more publications the 
organization has, the larger the circle drawn. Those subdisciplines without any publications are marked in gray.
<br /><br /> 

Circles are overlaid on the Map of Science, which is made of 554 interconnected subdisciplines, shown as grey 
dots here. A subdiscipline is defined as a cluster of journals that share a common topic. The Map of Science 
groups over 16,000 journals into 554 subdisciplines using similarities in their lists of references and key 
terms. Subdisciplines that are especially similar to one another are connected by lines, and will be closer to 
one another on the map.<br /><br /> 

For more information on this and other maps of science, see <a href='http://mapofscience.com'>http://mapofscience.com</a> or 
<a href='http://scimaps.org'>http://scimaps.org</a></div>

<div id="toolTipTwo" style="display:none;">
Any organization's publication activity can be categorized into 13 disciplines or 554 subdisciplines on the map 
of science.<br /><br />

The map of science is divided into 13 disciplines, each of which has its own area on the map, as well as its own 
color. Each of these disciplines contains multiple subdisciplines. You can switch between the discipline and 
subdiscipline view. You also can hover over a discipline (or subdiscipline) in the table below to show which 
overlaid circles it corresponds to on the map.  The selected dot will have a darker ring around it than the 
thers.<br /><br />

The table below summarizes the organization's body of publications as plotted on the map of science. Each row 
corresponds to a field (discipline or subdiscipline) on the map.<br /><br />

The <b># of pubs.</b> column shows how many of the publications were mapped to each discipline or subdiscipline. 
This count can be fractional because some publication venues are associated with more than one. Each publication 
in such a venue contributes fractionally to all associated discipline or subciscipline according to an assigned 
weight.<br /><br />

The <b>% of activity</b> column shows what proportion of the publications were mapped to each discipline or 
subdiscipline.

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
	List only subdisciplines (or disciplines) whose name contains this text.
</div>


<#-- COMPARISON TOOLTIP TEXT -->

<div id="comparisonToolTipTwo" style="display:none;">
The listed organizations are children of the University of Florida node in the organizational hierarchy. 
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