<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Setup needed by all individual templates -->

<#import "lib-list.ftl" as l>
<#import "lib-properties.ftl" as p>
<#assign core = "http://vivoweb.org/ontology/core#">

<#assign showEditingLinks = individual.showEditingLinks>

<#assign editingClass>
    <#if showEditingLinks>editing</#if>
</#assign>

<#assign propertyGroups = individual.propertyList>