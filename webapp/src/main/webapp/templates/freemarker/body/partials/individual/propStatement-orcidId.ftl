<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- VIVO-specific default data property statement template. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<@showStatement statement />

<#macro showStatement statement>
    <a href="${statement.value!}" title="ORCID iD" target="_blank">${statement.value!"ORCID iD not found"}</a>
    <#if orcidInfo??>
        <#if (orcidInfo.orcids[statement.value])!false>
            <span style="color:#FF7700">(confirmed)</span>
        <#else>
            <span style="color:#FF7700">(pending confirmation)</span>
        </#if>
    </#if>
</#macro>





