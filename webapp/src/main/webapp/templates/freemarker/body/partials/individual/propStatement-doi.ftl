<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- VIVO-specific default data property statement template. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-meta-tags.ftl" as lmt>

<@showStatement statement property />

<#macro showStatement statement property>
    <a href="http://dx.doi.org/${statement.value!}" title="${i18n().doi_link}" target="_blank">${statement.value!}</a>
    <@lmt.addCitationMetaTag uri=(property.uri!) content=(statement.value!) />
</#macro>





