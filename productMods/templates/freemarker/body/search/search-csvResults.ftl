<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#assign today = .now >
<#assign todayDate = today?date>
Results from ${siteName} for ${querytext} on ${todayDate}

Name, Title, Email, URI, VIVO Profile URL
<#list individuals as individual>                 
"${individual.name}",<#if individual.preferredTitle?has_content>"${individual.preferredTitle}"</#if>,<#if individual.email?has_content>"${individual.email}"</#if>,"${individual.uri}","${individual.profileUrl}"
</#list>
