<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#hasAssociatedConcept. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<@showConcept statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showConcept statement>
  <a href="${profileUrl(statement.uri("concept"))}" title="${i18n().concept_name}" class="label label-primary">${statement.conceptLabel!statement.conceptName}</a><#if statement.vocabularySourceName??> (${statement.vocabularySourceName})</#if>
</#macro>

<#--  <a href="${profileUrl(statement.uri("concept"))}" title="${i18n().concept_name}" class="label label-primary">${statement.conceptLabel!statement.conceptName}</a>  -->
