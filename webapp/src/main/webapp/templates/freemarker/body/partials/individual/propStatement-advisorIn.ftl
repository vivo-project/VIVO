<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "advisees". See the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-datetime.ftl" as dt>
<@showAdvisorIn statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAdvisorIn statement>
    <#-- It's possible that advisorIn relationships were created before the custom form and only have
         an rdfs:label. So check to see if there's an advisee first. If not, just display the label.  -->
    <#local linkedIndividual>
        <#if statement.advisee??>
            <#if statement.degreeLabel?? || statement.dateTimeStart?? || statement.dateTimeEnd?? >
                <a href="${profileUrl(statement.uri("advisee"))}" title="${i18n().advisee_label}">${statement.adviseeLabel!}</a>,
            <#else>
                <a href="${profileUrl(statement.uri("advisee"))}" title="${i18n().advisee_label}">${statement.adviseeLabel!}</a>
            </#if>
            <#if statement.degreeLabel??>
                ${statement.degreeAbbr!statement.degreeLabel!} 
                <#if statement.dateTimeStart?? || statement.dateTimeEnd?? >&nbsp;${i18n().candidate},<#else>&nbsp;${i18n().candidate}</#if>
            </#if>
        <#elseif statement.advisingRelLabel??>
            <a href="${profileUrl(statement.uri("advisingRel"))}" title="${i18n().advisingRel_label}">${statement.advisingRelLabel!statement.localName}</a>
        </#if>
    </#local>

    ${linkedIndividual}    <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
 </#macro>