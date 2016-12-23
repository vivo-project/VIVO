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
        <#if statement.org??>
			<#assign schemaType = "http://schema.org/Organization" />
			<#assign subclass = statement.subclass!"" />
			<#if subclass?contains("Educational") >
				<#assign schemaType = "http://schema.org/CollegeOrUniversity" />
			</#if>
			<span itemscope itemtype="${schemaType}" >
            	<a itemprop="name" href="${profileUrl(statement.uri("org"))}" title="${i18n().organization_name}">${statement.orgName}</a>
            </span>
        <#elseif editable>
            <#-- Show the link to the context node only if the user is editing the page. -->
            <a href="${profileUrl(statement.uri("edTraining"))}" title="${i18n().missing_organization}">${i18n().missing_organization}</a>
        </#if>
    </#local>

    <@s.join [ degree, linkedIndividual!, statement.deptOrSchool!, statement.info! ] /> <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" false/> 

</#macro>
