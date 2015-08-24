<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showRole statement property  />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showRole statement property >
  
    <#local linkedIndividual>
        <#if statement.indivInRole??>
    		<#if statement.vSubclass?? && statement.vSubclass?contains("vcard")>
				<#if statement.indivLabel?replace(" ","")?length == statement.indivLabel?replace(" ","")?last_index_of(",") + 1 >
        			${statement.indivLabel?replace(",","")}
				<#else>
					${statement.indivLabel}
				</#if>
    		<#else>
        			<a href="${profileUrl(statement.uri("indivInRole"))}" title="${i18n().name}">${statement.indivLabel!statement.indivName}</a>
    		</#if>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("role"))}"  title="${i18n().missing_person_in_role}">${i18n().missing_person_in_role}</a>
        </#if>
    </#local>

    <#-- 
         Generally roles are assigned a label when entered through a custom form. Investigator and its subclasses do not,
         so use the type label instead if not collated by subclass.
    -->
    <#local roleLabel>
       
        <#if statement.roleTypeLabel?has_content>
            <#assign roleTypeLabel = statement.roleTypeLabel!"" >
		<#else>
			<#assign roleTypeLabel = "" >
        </#if>
        <#if statement.roleLabel??>
            ${statement.roleLabel?replace(" Role", "")?replace(" role","")}
        <#elseif !property.collatedBySubclass >
            ${roleTypeLabel?replace(" Role", "")}
        </#if>
    </#local>

    ${linkedIndividual}&nbsp;${roleLabel!}&nbsp;<@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
</#macro>
