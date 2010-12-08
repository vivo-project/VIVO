<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-list.ftl" as l>

<div id="wrap" class="container">
    <div id="header">

        <#-- uncomment this div to place your institutional branding/identity at the top of every page
        <div id="institution">
        </div>
        -->

        <div id="identity">

            <h1><a title="Home" href="${urls.home}">${siteName}</a></h1>

            <ul id="otherMenu">  
                <@l.firstLastList>  
                    <#if user.loggedIn>
                        <li>
                            Logged in as <strong>${user.loginName}</strong> (<a href="${urls.logout}">Log out</a>)
                        </li>                                        
                        <#if user.hasSiteAdminAccess>
                            <li><a href="${urls.siteAdmin}">Site Admin</a></li>
                        </#if>
                    <#else>
                         <li><a title="log in to manage this site" href="${urls.login}">Log in</a></li>
                    </#if> 
            
                    <#include "subMenuLinks.ftl">
                </@l.firstLastList>
            </ul>   
        </div>