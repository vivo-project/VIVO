<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for Site Administration site configuration panel -->

<#if siteConfig?has_content>
    <div class="pageBodyGroup">
        
        <h3>Site Configuration</h3>

        <ul>
            <#if siteConfig.urls.portals??>
                <#if siteConfig.urls.siteInfo??>
                    <li><a href="${siteConfig.urls.siteInfo}">Current portal information</a></li>
                </#if>
                <li><a href="${siteConfig.urls.portals}">List all portals</a></li>
            <#else>
                <#if siteConfig.urls.siteInfo??>
                    <li><a href="${siteConfig.urls.siteInfo}">Site information</a></li>
                </#if>
            </#if>

            <#if siteConfig.urls.menuN3Editor??>
                <#if currentTheme != "vivo-basic"> <#-- vivo-basic doesn't support menu pages -->
                    <li><a href="${siteConfig.urls.menuN3Editor}">Menu management</a></li> 
                </#if>
            </#if>
            
            <#if currentTheme != "wilma"> <#-- wilma doesn't support tabs -->
                <li><a href="${siteConfig.urls.tabs}">Tab management</a></li>
            </#if>
   
            <#if siteConfig.urls.users??>
                <li><a href="${siteConfig.urls.users}">User accounts</a></li>  
            </#if>
            
        </ul>
    </div>
</#if>