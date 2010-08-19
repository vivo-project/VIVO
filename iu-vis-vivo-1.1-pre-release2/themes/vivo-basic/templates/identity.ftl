<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "listMacros.ftl" as l>

<div id="identity">

    <h1><a title="Home" href="${urls.home}">${siteName}</a></h1>
    
    <#-- RY We will need this in non-NIHVIVO versions
    <#if siteTagline?has_content>
        <em>${siteTagline}</em>
    </#if>
    -->
    
    <ul id="otherMenu">  
        <@l.firstLastList>  
            <#if loginName??>
                <li>
                    Logged in as <strong>${loginName}</strong> (<a href="${urls.logout}">Log out</a>)
                </li>                                        
                <li><a href="${urls.siteAdmin}">Site Admin</a></li>
            <#else>
                 <li><a title="log in to manage this site" href="${urls.login}">Log in</a></li>
            </#if> 
            
            <#include "subMenuLinks.ftl">
        </@l.firstLastList>       
    </ul>   
</div>