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
     
        <#-- Pre-1.4 addresses may only have an rdfs:label, so display that when  -->
        <#-- there's no street number.                                            -->
        <#if statement.street??>
            <div class="adr">
                <div class="street-address">${statement.street}</div>
                <#-- If the subclass is core:US Postal Address, or if the country is     -->
                <#-- the US, display the city, state, and postal code on a single line.  -->
                <#local cityStateZip><@s.join [ statement.city!, statement.state!, statement.postalCode!], "&nbsp;" /></#local>
                <#if ( statement.subclass?? && statement.subclass?contains("USPostalAddress") ) ||  ( statement.country?? && statement.country?contains("United States") ) >
                    <#if cityStateZip?has_content>
                 	    <div class="extended-address">${cityStateZip}</div>
                 	</#if>
                <#else>
                    <div class="locality">${statement.city!}</div>
                    <#if statement.state??><div class="region">${statement.state}</div></#if>
                    <#if statement.postalCode??><div class="postal-code">${statement.postalCode}</div></#if>
                </#if>
                <#if statement.country??><div class="country-name">${statement.country}</div></#if>
            </div>
        <#else>
            <a href="${profileUrl(statement.address)}">${statement.label!}</a>
        </#if>
        
     </#macro>