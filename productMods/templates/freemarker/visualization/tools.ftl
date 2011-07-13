<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign shortVisualizationURLRoot ="/vis">
<#assign refreshCacheURL = "${urls.base}${shortVisualizationURLRoot}/refresh-cache">

<h2>Visualization Tools</h2>

<a href="${refreshCacheURL}">Refresh Cached Models for Visualization</a> 

<h5>Why</h5>
Certain class of visualizations like Temporal graph and Map of Science that work on a large scale 
attempt to calculate counts of publications (or grants) for all the organizations within an organization. 
These queries tend to be both memory intensive and time consuming. In order to provide a good user experience when 
these visualizations are served we decided to save the results of these queries to be reused later.

<h5>What</h5>
To this end we have devised a caching solution which will store information about the organization 
hierarchy, which publications were published from which organizations etc (among other things) in 
semantic way (i.e. we store the rdf model).<br/>

We're currently caching these models in-memory, which works nicely, for now. Currently the cache is only built 
once, on first user request after a server restart, so the visualization will essentially never be updated 
until the server is restarted again. It is not being updated real-time or periodically. This means that the data in these 
models might be stale depending upon when was it last created. In future releases we will come 
up with a solution that stores these models on disk and be updated periodically.<br /> 

<h5>How</h5>
To refresh these models either restart the server or click on "refresh cache" link above. We realize that 
in production VIVO instances it is not feasible to restart the server to refresh these models. Hence we 
recommend the above link. Administrators can use this link to trigger regeneration of all the existing 
models.<br />