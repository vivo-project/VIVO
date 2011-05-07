<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#informationResourceInAuthorship -->

<#import "lib-sequence.ftl" as s>

<@showAuthorship statement individual/>

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAuthorship statement individual>
    <#if statement.person??>
        <a href="${profileUrl(statement.person)}">
            <span about="${individual.uri}" rel="core:informationResourceInAuthorship"> 
                <span about="${statement.authorship}" rel="core:linkedAuthor">
                    <span class="link" about="${statement.person}" property="rdfs:label">
                        ${statement.personName}<#t>
                    </span><#t>
                </span><#t>
            </span><#t>
        </a><#t>
    <#else>
        <a href="${profileUrl(statement.authorship)}">
            <span class="link" about="${individual.uri}" rel="core:informationResourceInAuthorship" resource="${statement.authorship}">  
                missing author<#t>
            </span><#t>
        </a><#t>
    </#if>
</#macro>