<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

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
