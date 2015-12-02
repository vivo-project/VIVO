<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--  
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<@showTelephone statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showTelephone statement>
 
    <#if statement.number?has_content>
            ${statement.number}
    </#if>    
          
</#macro>