<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign aboutImagesRoot = '${urls.images}/visualization/mapofscience/about/'>

<h2>About VIVO's Map of Science Visualization</h2>
<h3>Reference Basemap</h3>
<p>The VIVO Map of Science visualization uses the UCSD map of science and classification 
system that was computed using paper-level data from about 25,000 journals from Elsevier's 
Scopus and and Thomson Reuters' Web of Science (WoS) for the years 2001-2010. The UCSD map 
of science assigns the 25,000 journals to 554 subdisciplines that are further aggregated 
into 13 main disciplines of science. In the map, each discipline has a distinct color 
(green for 'Biology', brown for 'Earth Sciences') and a label. (Sub)disciplines that are 
similar to one another closer to one another on the map. (Sub)disciplines that are 
especially similar are connected by grey lines.</p>

<h3>Data Overlay</h3>
<p>Publication activity of a university, organization, or person can be overlaid on the 
map to generate expertise profiles. The process is as follows: (1) The set of unique 
journals is identified, (2) the number of times each journal served as a publication venue 
is calculated, and (3) the area size of the 13 disciplines and 554 subdisciplines is 
calculated based on these journal publication venue counts. Note that some journals are 
associated with exactly one (sub)discipline while others, e.g., interdisciplinary ones like 
<em>Science</em> or <em>Nature</em>, are fractionally associated with multiple 
(sub)disciplines. Subdisciplines inherit the colors of their parent disciplines. 
(Sub)disciplines without any associated publications are given in gray.</p>

<img src="${aboutImagesRoot}/scimap_discipline.jpg" width="450" height="327" />
<img src="${aboutImagesRoot}/scimap_subdiscipline.jpg" width="450" height="327" />

<h3>Interactivity</h3>
<p>The map can be explored at two levels-by 13 disciplines or 554 subdisciplines. Clicking 
into a node in the map brings up the number of fractionally associated journal publications 
and the percentage of publications mapped to this (sub)discipline. Hover over a discipline 
in the table on the left to see what circles it corresponds to on the map.</p>

<h3>Links</h3>
<p>For more information on the UCSD map of science and classification system, see 
<a href="http://sci.cns.iu.edu/ucsdmap" target="_blank">http://sci.cns.iu.edu/ucsdmap</a>. 
For other maps of science, see 
<a href="http://scimaps.org" target="_blank">http://scimaps.org</a> and 
<a href="http://mapofscience.com" target="_blank">http://mapofscience.com</a>.</p>
