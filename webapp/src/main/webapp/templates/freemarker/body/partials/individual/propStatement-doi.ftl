<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- VIVO-specific default data property statement template. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-meta-tags.ftl" as lmt>

<@showStatement statement property />

<#macro showStatement statement property>
    <#assign doi = (statement.value!).toLowerCase().
                    replace("http://doi.org/", "").
                    replace("https://doi.org/", "").
                    replace("http://dx.doi.org/", "").
                    replace("https://dx.doi.org/", "").
                    replace("doi:", "").trim()>
    <a href="https://doi.org/${doi}" title="${i18n().doi_link}" target="_blank">${doi}</a>
    <@lmt.addCitationMetaTag uri=(property.uri!) content=(doi) />
</#macro>





