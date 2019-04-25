<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- VIVO-specific default data property statement template.

     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.
 -->
<@showStatement statement />

<#macro showStatement statement>
    <a href="http://www.scopus.com/authid/detail.url?authorId=${statement.value!}" title="${i18n().scopus_id_link}" target="_blank">${statement.value!}</a>
</#macro>





