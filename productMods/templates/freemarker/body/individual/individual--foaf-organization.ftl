<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Individual profile page template for foaf:Organization individuals (extends individual.ftl in vivo)-->

<#-- Do not show the link for temporal visualization unless it's enabled -->

<#if temporalVisualizationEnabled>
    <#assign classSpecificExtension>
        <section id="right-hand-column" role="region">
            <#include "individual-visualizationTemporalGraph.ftl">
            <#include "individual-visualizationMapOfScience.ftl">
        </section> <!-- #right-hand-column -->
    </#assign>
</#if>

<#if individual.mostSpecificTypes?seq_contains("Academic Department")>
    <#assign departmentalGrantsExtension>    
        <div id="activeGrantsLink">
        <img src="${urls.base}/images/individual/arrow-green.gif">
            <a href="${urls.base}/deptGrants?individualURI=${individual.uri}">
                View all active grants
            </a>    
        </div>
    </#assign>
    
    <#assign departmentalResearchAreas>
        <#include "individual-dept-research-areas.ftl">
    </#assign>
    
    <#assign departmentalMemberships>
        <#include "individual-dept-memberships.ftl">
    </#assign>    
</#if>

<#include "individual.ftl">

