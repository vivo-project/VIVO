<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->


<#include "mapOfScienceSetup.ftl">

<div id="map-of-science-response">

<#--
<div id="subject-parent-entity" class="hide-dom-on-init">
	<a id="subject-parent-entity-profile-url" href="#"></a>&nbsp;
    <a id="subject-parent-entity-temporal-url" href="#"><img src="${mapOfScienceIcon}" width="15px" height="15px"/></a>
</div>
-->
        
<h2 id="header-entity-label" class="hide-dom-on-init"><span><a id="entityMoniker" href="${entityVivoProfileURL}">${entityLabel}</a></span></h2>

<div id="map-of-science-info" class="hide-dom-on-init"> Explore <span id="mapped-publications" style="font-weight: bold"></span> publications activity across 554 scientific sub-disciplines 
	<img class="filterInfoIcon" id="imageIconOne"  src="${urls.images}/iconInfo.png" 
		alt="information icon" 
    	title="" /> 
</div>

<div id="left-column" class="hide-dom-on-init">
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
    	<img class="filterInfoIcon" id="imageIconTwo" src="${urls.images}/iconInfo.png" 
    		 alt="information icon" 
    		 title="" />
    </div>
    
    <div id="main-science-areas-table-container"></div>
    
    <hr class="subtle-hr"/>
    
    <div id="main-science-areas-table-footer">
    <a id="csv" href="${entityMapOfScienceDisciplineCSVURL}" class="map-of-science-links">Save All as CSV</a>
    <#-- <a class="clear-selected-entities map-of-science-links" title="Clear all selected entities.">Clear</a> -->
    </div>
</div>		

<div id="right-column"><div id="map_area"></div>
<div id="percent-mapped-info">
mapped <span id="percent-mapped"></span>% of <span id="total-publications"></span> publications
	<img class="filterInfoIcon" id="imageIconThree" src="${urls.images}/iconInfo.png" 
		alt="information icon" 
		title="" /></div>

</div>
</div>
<div id="toolTipOne" style="display:none;">
VIVO's Map of Science visualization shows the publication activity of any organization, person, or university in a VIVO instance, 
overlaid on the map of science. This particular page shows the publication activity of ${entityLabel}.<br /> <br /> 

You can use VIVO's Map of Science visualization to see where ${entityLabel} is active in the world of science -- <em><b>based solely 
on publications that have been loaded into this VIVO instance.</b></em><br /><br />

Overlaid circles are larger if ${entityLabel} has many publications in that sub-discipline, and are smaller if ${entityLabel} has 
fewer publications in that sub-discipline.<br /><br /> 

Circles are overlaid on the Map of Science itself, which is made of 554 interconnected sub-disciplines, shown as grey dots here. 
A sub-discipline is defined as a cluster of journals. The Map of Science groups over 16,000 journals into 554 sub-disciplines using 
similarities in their lists of references and key terms. Sub-disciplines that are especially similar to one another are interconnected, 
and will be closer to one another on the map.
<br /><br /> 
For more information on this and other maps of science, see <a href='http://mapofscience.com'>http://mapofscience.com</a> or 
<a href='http://scimaps.org'>http://scimaps.org</a></div>

<div id="toolTipTwo" style="display:none;">
${entityLabel}'s publication activity can be categorized into 13 disciplines or 554 sub-disciplines on the map of science.
<br /><br />
The map of science is divided into 13 disciplines, each of which has its own area on the map, as well as its own color. 
Each of these disciplines contains multiple sub-disciplines. You can hover over a discipline in the table below to show 
which overlaid circles it corresponds to on the map.
<br /><br />
Each grey dot on the map represents one of the 554 sub-disciplines. A sub-discipline is defined as a cluster of journals. 
We grouped over 16,000 journals into 554 disciplines using similarities in their lists of references and key terms. 
Sub-disciplines that are especially similar to one another are connected by lines, and are closer to one another on the map. 
You can hover over a sub-discipline in the table below to show which overlaid circle it corresponds to on the map.

<br /><br /> 

<#--
In the table below, <b># of pubs.</b> column indicates number of publications that fall under a particular field (sub-discipline or 
discipline). Sometimes this number will be fractional. This happens when a journal in which the publication was published is associated 
with more than one (sub)discipline. In these cases, the publication score is fractionally mapped based on the weight scores 
of the journal.<br /><br />

<b>% activity</b> column indicates the percentage of publications that fall under a particular field.
-->

The table below summarizes this institution's body of publications as plotted on the map of science.  
Each row corresponds to a field (discipline or sub-discipline) on the map.
<br /><br />

The <b># of pubs.</b> column shows how many of the publications were mapped to each field.  
This count can be fractional because some publication venues are associated with more than one field.  
Each publication in such a venue contributes fractionally to all associated fields according to a weighting scheme.

<br /><br />
The <b>% activity</b> column shows what proportion of the publications were mapped to each field.

</div>

<div id="toolTipThree" style="display:none;">
This visualization is based on the publications we were able to 'science locate' for ${entityLabel}, and therefore it may not be 
fully representative of the overall publication activity for ${entityLabel}.<br /><br />

The publication coverage of this visualization can be improved by including more publication data in the VIVO system, and by 
ensuring that each publication in the VIVO system is associated with a journal that the Map of Science recognizes (based on 
the holdings of Thomson's ISI database and Elsevier's Scopus database). Journal names containing typos or other idiosyncrasies 
may need to be cleaned up before they are recognized. You may contact a VIVO system administrator if publication coverage is a 
concern.</div>

<div id="searchInfoTooltipText" style="display:none;">
	<!-- Search for specific sub-discipline (or discipline) label in the first column of the table. -->
	List only map of science areas whose names contain this text.
</div>

<div id="error-container">

<h1 id="noPublications-header">${entityLabel}</h1>
    <h3 id="vis-title">Map of Science Visualization</h3>
    <div id="error-body">
        <p><span id="variable-error-text">No publications in the system have been attributed to this organization.</span><hr /> 
        Please visit the ${entityLabel} <a href="${entityVivoProfileURL}">profile page</a> for a complete overview.</p>
    </div>
</div>

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>')}