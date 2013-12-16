<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
     There are two templates for displaying publication information on foaf person pages.

     - personPublicationSparklineContent.ftl, which shows the actual sparkline
     - personPublicationCountNoSparkline.ftl, which shows on counts and not the sparkline
     
     The first is the template used with the Wilma theme and the individual--foaf-person.ftl
     template. The second is used with the individual--foaf-person-2column.ftl template.
-->
<#if shouldVIVOrenderVis>
    <#-- Added requestingTemplate variable in release 1.6 to support multi-view option -->
    <#if requestingTemplate = "foaf-person-wilma" >
	    <#include "personPublicationSparklineContent.ftl"> 
	<#else>
        <#include "personPublicationCountNoSparkline.ftl"> 
    </#if>
</#if>
