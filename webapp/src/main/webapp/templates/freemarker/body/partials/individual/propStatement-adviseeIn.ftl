<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "advisee of". See the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-datetime.ftl" as dt>
<@showAdviseeIn statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAdviseeIn statement>
    <#-- It's possible that adviseeIn relationships were created before the custom form and only have
         an rdfs:label. So check to see if there's an advisor first. If not, just display the label.  -->
    <#local linkedIndividual>
        <#if statement.advisor??>
            <#if statement.degreeLabel?? || statement.dateTimeStart?? || statement.dateTimeEnd?? >
                <a href="${profileUrl(statement.uri("advisor"))}" title="${i18n().advisor_label}">${statement.advisorLabel!}</a>,
            <#else>
                <a href="${profileUrl(statement.uri("advisor"))}" title="${i18n().advisor_label}">${statement.advisorLabel!}</a>
            </#if>
            <#if statement.degreeLabel??>
                ${statement.degreeAbbr!statement.degreeLabel!} 
                <#if statement.dateTimeStart?? || statement.dateTimeEnd?? >&nbsp;${i18n().candidacy},<#else>&nbsp;${i18n().candidacy}</#if>
            </#if>
        <#elseif statement.advisingRelLabel??>
            <a href="${profileUrl(statement.uri("advisingRel"))}" title="${i18n().advisingRel_label}">${statement.advisingRelLabel!statement.localName}</a>
        </#if>
    </#local>

    ${linkedIndividual}    <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" /> 
 </#macro>