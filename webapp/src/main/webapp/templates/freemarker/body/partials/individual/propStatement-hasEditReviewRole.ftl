<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "reviewer of". See the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>
<@showRole statement property />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showRole statement property>
    <#local linkedIndividual>
    		<#if statement?has_content && statement.activity?has_content>
        	<a href="${profileUrl(statement.uri("activity"))}" title="${i18n().activity_name}">${statement.activityLabel!statement.activityName!}</a>
        </#if>
    </#local>

    <#local dateTime>
        <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
    </#local>

    <#-- If property is collated, then subclass label is redundant information -->
    ${linkedIndividual} <#if (! property.collatedBySubclass) && statement.subclassLabel?has_content>&nbsp;(${statement.subclassLabel!})</#if> ${dateTime!}
</#macro>