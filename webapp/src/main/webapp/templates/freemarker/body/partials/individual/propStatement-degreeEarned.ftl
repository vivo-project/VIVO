<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "education and training". See the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>
<#-- Coming from propDelete, individual is not defined, but we are editing. -->
<@showEducationalTraining statement=statement editable=(!individual?? || individual.editable) />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showEducationalTraining statement editable>

    <#local degree>
        <#if statement.degreeName??>
            ${statement.degreeAbbr!statement.degreeName} 
            <#if statement.majorField??> ${i18n().in} ${statement.majorField}</#if>
        <#elseif statement.typeName??>
            ${statement.typeName!}
        </#if>
    </#local>
    
    <#local linkedIndividual>
        <#if statement.degree??>
            <a href="${profileUrl(statement.uri("degree"))}" title="${i18n().degree}">${statement.degreeAbbr!statement.degreeName}</a>
        <#else>
            <#-- Show the link to the context node only if the user is editing the page. -->
            ${i18n().missing_degree}
        </#if>
    </#local>

    ${linkedIndividual}

</#macro>
