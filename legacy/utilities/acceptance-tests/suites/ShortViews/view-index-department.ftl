<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#import "lib-vivo-properties.ftl" as p>

<a href="${individual.profileUrl}" title="individual name">${individual.name}</a>

<@p.displayTitle individual />

<#if locations??>
    <#list locations as location>
        <#if (location.label)?? >
            <span class="title">Located in ${location.label}</span>
        </#if>
    </#list>
</#if>

<#if (deptHead[0].label)?? >
    <span class="title">Headed by ${deptHead[0].label}</span>
</#if>

