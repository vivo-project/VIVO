<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Individual profile page template for foaf:Organization individuals (extends individual.ftl in vivo)-->

<#assign classSpecificExtension>
    <#if temporalVisualizationEnabled??>
        <#include "individual-visualizationTemporalGraph.ftl">
    </#if>
</#assign>

<#include "individual.ftl">
