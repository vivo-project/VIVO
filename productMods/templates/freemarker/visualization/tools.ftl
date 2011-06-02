<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign shortVisualizationURLRoot ="/vis">
<#assign refreshCacheURL = "${urls.base}${shortVisualizationURLRoot}/refresh-cache">

<h2>Visualization Tools</h2>

<a href="${refreshCacheURL}">Refresh Cached Models for Visualization</a> 

<h5>Why</h5>
Large-scale visualizations like the Temporal Graph or the Map of Science involve calculating total counts of publications or 
of grants for some entity. Since this means checking also through of its all sub-entities, the underlying queries can be both 
memory-intensive and time-consuming. For a faster user experience, we wish to save the results of these queries for later re-use.

<h5>What</h5>
To this end we have devised a caching solution which will retain information about the hierarchy of organizations-namely, 
which publications are attributed to which organizations-by storing the RDF model. <br />
We're currently caching these models in memory.  The cache is built (only once) on the first user request after a server restart.  
Because of this, the same model will be served until the next restart. This means that the data in these models may become stale 
depending upon when it was last created. This works well enough for now. In future releases we will improve this solution so that 
models are stored on disk and periodically updated.

<h5>How</h5>
The models are refreshed each time the server restarts.  Since this is not generally practical on production instances, 
administrators can instead use the “refresh cache” link above to do this without a restart.
<br />