<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- Custom object property statement view for faux property "editors". See the PropertyConfig.n3 file for details.

     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-meta-tags.ftl" as lmt>

<@showEditorship statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showEditorship statement>
    <#if statement.editor??>
    	<#if statement.subclass?? && statement.subclass?contains("vcard")>
			<#if statement.editorName?replace(" ","")?length == statement.editorName?replace(" ","")?last_index_of(",") + 1 >
        		${statement.editorName?replace(",","")}
			<#else>
				${statement.editorName!i18n().missing_editor}
			</#if>
    	<#else>
        	<a href="${profileUrl(statement.uri("editor"))}" title="${i18n().editor_name}">${statement.editorName}</a>
    	</#if>
		<@lmt.addCitationMetaTag uri="http://vivoweb.org/ontology/core#Editorship" content=statement.editorName />
    <#else>
        <#-- This shouldn't happen, but we must provide for it -->
        <a href="${profileUrl(statement.uri("editorship"))}" title="${i18n().missing_editor}">${i18n().missing_editor}</a>
    </#if>
</#macro>
