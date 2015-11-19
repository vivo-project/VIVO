<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#credentialOrHonor. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>
<@showCredential statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showCredential statement>
 
    <#local linkedIndividual>
        <#if statement.credential??>
            <a href="${profileUrl(statement.uri("credential"))}" title="${i18n().credential_name}">${statement.credentialLabel!statement.issuedCredentialLabel!statement.credentialLocal!}</a>
        <#else>
            <a href="${profileUrl(statement.uri("issuedCredential"))}" title="${i18n().credential_name}">${statement.issuedCredentialLabel!"${i18n().missing_credential}"}</a>
        </#if>
    </#local>

    <#local dateTimeVal>
        <#if statement.dateTime??>
            <@dt.yearSpan statement.dateTime! /> 
        <#else>
            <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
        </#if>
    </#local>


    <@s.join [ linkedIndividual,  dateTimeVal! ] />

 </#macro>
