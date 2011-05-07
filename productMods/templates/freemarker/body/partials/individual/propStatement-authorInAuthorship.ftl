<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#authorInAuthorship -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showAuthorship statement property individual />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAuthorship statement property individual>
        
    <#if statement.infoResource??>     
        <a href="${profileUrl(statement.infoResource)}">
            <span about="${individual.uri}" rel="core:authorInAuthorship">                     
                <span about="${statement.authorship}" rel="core:linkedInformationResource">               
                    <span class="link" about="${statement.infoResource}" property="rdfs:label">
                        ${statement.infoResourceName}<#t>
                    </span><#t>
                </span><#t>
            </span><#t>
        </a><#t>       
        <#if statement.dateTimeValue?has_content>
            <span about="${statement.infoResource}" rel="core:dateTimeValue">
                <#if statement.dateTime?has_content>
                    <span about="${statement.dateTimeValue}" property="core:dateTime" content="${statement.dateTime}">
                        <@dt.yearSpan statement.dateTime />
                    </span>
                </#if>
             </span>
        </#if>            
    <#else>
        <a href="${profileUrl(statement.authorship)}">
            <span class="link" about="${individual.uri}" rel="core:authorInAuthorship" resource="${statement.authorship}">         
                missing information resource<#t>                       
            </span><#t>
        </a><#t>
    </#if>        

</#macro>