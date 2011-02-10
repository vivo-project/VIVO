<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Individual profile page template for foaf:Organization individuals (extends individual.ftl in vivo)-->

<#-- Do not show the link for temporal visualization unless deploy.properties specifies "visualization.temporal = enabled" -->
<#if temporalVisualizationEnabled??>
    <#assign classSpecificExtension>
        <#include "individual-visualizationTemporalGraph.ftl">
    </#assign>
</#if>

<#include "individual.ftl">