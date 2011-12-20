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
 
    <#if ( statement.street1?has_content || statement.street2?has_content || statement.street3?has_content || statement.city?has_content || 
           statement.state?has_content ||statement.postalCode?has_content || statement.country?has_content )>
        <#-- until the custom form is ready, provide a link to the address profile for editing --> 
        <div class="adr">
            <#if statement.street1?has_content>
                <div class="address-street1"><a href="${profileUrl(statement.uri("address"))}">${statement.street1}</a></div>
            </#if>
            
            <#if statement.street2?has_content>
                <#if !statement.street1?has_content>
                    <div class="address-street2"><a href="${profileUrl(statement.uri("address"))}">${statement.street2}</a></div>
                <#else>
                    <div class="address-street2">${statement.street2}</div>
                </#if>
            </#if>
            
            <#if statement.street3?has_content>
                <#if !statement.street1?has_content && !statement.street2?has_content>
                    <div class="address-street3"><a href="${profileUrl(statement.uri("address"))}">${statement.street3}</a></div>
                <#else>
                    <div class="address-street3">${statement.street3}</div>
                </#if>
            </#if>

            <#-- If the subclass is vivo:US Postal Address, or if the country is     
                 the US, display the city, state, and postal code on a single line.  -->           
            <#if ( statement.subclass?? && statement.subclass?contains("USPostalAddress") ) ||  
                 ( statement.country?? && ( statement.country?contains("United States") ||
                                            statement.country == "US" ||
                                            statement.country == "USA" ) )>
                <#local cityState><@s.join [statement.city!, statement.state!], ", " /></#local>                      
                <#local cityStateZip><@s.join [ cityState!, statement.postalCode!], "&nbsp;" /></#local>
                <#if cityStateZip?has_content>
             	    <div class="extended-address">${cityStateZip}</div>
             	</#if>
            <#else>
                <#if statement.city?has_content><div class="locality">${statement.city!}</div></#if>
                <#if statement.state?has_content><div class="region">${statement.state}</div></#if>
                <#if statement.postalCode?has_content><div class="postal-code">${statement.postalCode}</div></#if>
            </#if>
            
            <#if statement.country?has_content>
                <div class="country-name">${statement.country}</div>
            </#if>
        </div>
        
    <#-- Pre-1.4 addresses may only have an rdfs:label, since users using the default
         object property form sometimes entered the entire address as the label. Display that when  
         there's no address data. -->
    <#elseif statement.editable>
        <#-- This can be removed when the custom form is available. Until then, provide a link to the
             address profile so the data can be edited. -->
        <a href="${profileUrl(statement.uri("address"))}" title="address label">${statement.label!statement.localName}</a>
    <#else>
        ${statement.label!}
    </#if>
  
 </#macro>