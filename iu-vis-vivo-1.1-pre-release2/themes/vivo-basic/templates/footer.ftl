<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "listMacros.ftl" as l>

<div id="footer">

    <#if urls.bannerImage??>
        <img class="footerLogo" src="${urls.bannerImage}" alt="${siteTagline!}" />
    </#if>
    
    <div class="footerLinks">
        <ul class="otherNav">  
            <@l.firstLastList> 
                <#include "subMenuLinks.ftl">
            </@l.firstLastList>
        </ul>
    </div>
  
    <#include "copyright.ftl">

    All Rights Reserved. <a href="${urls.termsOfUse}">Terms of Use</a>

</div>

