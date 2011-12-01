<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#mailingAddress. 
    
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
            <a href="${profileUrl(statement.uri("advisee"))}" title="advisee label">${statement.adviseeLabel!}</a>,
            <#if statement.degreeLabel??>
                ${statement.degreeAbbr!statement.degreeLabel!} 
                <#if statement.dateTimeStart??>&nbsp;candidate,<#else>&nbsp;candidate</#if>
            </#if>
        <#elseif statement.advisoryLabel??>
            <a href="${profileUrl(statement.uri("advisory"))}" title="advisory label">${statement.advisoryLabel!statement.localName}</a>
        </#if>
    </#local>

    ${linkedIndividual}    <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
 </#macro>