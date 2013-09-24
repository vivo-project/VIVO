<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#mailingAddress. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-sequence.ftl" as s>
<@showAddress statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAddress statement>
 
    <#if statement.street?has_content>
        <p class="address-line">
            ${statement.street}
        </p>
    </#if>    

    <#if ( statement.country?has_content && (statement.country == "US" || statement.country?contains("United States") || statement.country?contains("U.S.") || statement.country?contains("U.S.A.") || statement.country?contains("USA")))>
        <#local cityState><@s.join [statement.locality!, statement.region!], ", " /></#local>                      
        <#local cityStateZip><@s.join [ cityState!, statement.postalCode!], "&nbsp;" /></#local>
        <#if cityStateZip?has_content>
            <p class="address-line">${cityStateZip}</p>
            <p class="address-line">${statement.country!}</p>
     	</#if>
    <#else>        
        <#if statement.locality?has_content>
            <p class="address-line">
                ${statement.locality}
            </p>
        </#if>    
        <#if statement.region?has_content>
            <p class="address-line">
                ${statement.region}
            </p>
        </#if>    
        <#if statement.postalCode?has_content>
            <p class="address-line">   
                ${statement.postalCode}
            </p>
        </#if>    
        <#if statement.country?has_content>
            <p class="address-line">
                ${statement.country}
            </p>
        </#if>
    </#if>
          
</#macro>