<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 

     There are two templates for displaying publication information on foaf person pages.

     - personPublicationSparklineContent.ftl
     - personPublicationCountNoSparkline.ftl
     
     The first is the template use with the Wilma theme and the individual--foaf-person.ftl
     template. The second is used with the individual--foaf-person-2column.ftl template.
     
     Update the include statement below to use the correct "sparkline" template for your
     foaf person template.
     
-->
<#if shouldVIVOrenderVis>
	<#include "personPublicationCountNoSparkline.ftl"> 
</#if>
