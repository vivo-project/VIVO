<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "/lib/list.ftl" as l>

<div id="footer">

    <#if bannerImageUrl??>
        <img class="footerLogo" src="${urls.bannerImage}" alt="${tagline!}" />
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

