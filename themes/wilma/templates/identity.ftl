<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<header id="branding" role="banner">
    <h1 class="vivo-logo"><a href="${urls.home}"><span class="displace">${siteName}</span></a></h1>
    <#-- Since we are using a graphic text for the tagline, we won't render ${siteTagline}
    <#if siteTagline?has_content>
        <em>${siteTagline}</em>
    </#if>-->

    <nav role="navigation">
        <ul id="header-nav" role="list">
            <#if user.loggedIn>
                <li role="listitem"><span class="loginIcon">U</span> ${user.loginName}</li>
                <li role="listitem"><a href="${urls.logout}">Log out</a></li>
                <#if user.hasSiteAdminAccess>
                    <li role="listitem"><a href="${urls.siteAdmin}">Site Admin</a></li>
                </#if>
            <#else>
                <li role="listitem"><a title="log in to manage this site" href="${urls.login}">Log in</a></li>
            </#if>
            <#-- List of links that appear in submenus, like the header and footer. -->
                <li role="listitem"><a href="${urls.about}">About</a></li>
            <#if urls.contact??>
                <li role="listitem"><a href="${urls.contact}">Contact Us</a></li>
            </#if>
                <li role="listitem"><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
                <li role="listitem"><a href="${urls.index}">Index</a></li>
        </ul>
    </nav>
    
    <section id="search" role="region">
        <fieldset>
            <legend>Search form</legend>
            
            <form id="search-form" action="${urls.search}" name="search" role="search"> 
                <#if user.showFlag1SearchField>
                    <select id="search-form-modifier" name="flag1" class="form-item" >
                        <option value="nofiltering" selected="selected">entire database (${user.loginName})</option>
                        <option value="${portalId}">${siteTagline!}</option>
                    </select>
                
                <#else>
                    <input type="hidden" name="flag1" value="${portalId}" /> 
                </#if> 
                
                <div id="search-field">
                    <input type="text" name="querytext" class="search-vivo" value="${querytext!}" />
                    <input type="submit" value="Search" class="submit">
                </div>
            </form>
        </fieldset>
    </section>
</header>