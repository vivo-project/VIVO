<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "/macros/list.ftl" as l>

<div id="footer">

    <#if bannerImageUrl??>
        <img class="footerLogo" src="${urls.bannerImage}" alt="${tagline!}" />
    </#if>
    
    <div class="footerLinks">
        <ul class="otherNav">  
            <@l.makeList> 
                <li><a href="${urls.about}" title="more about this web site">About</a></li>,
                <#if urls.contact??>    
                    <li><a href="${urls.contact}" title="feedback form">Contact Us</a></li>
                </#if> 
            </@l.makeList>
        </ul>
    </div>
  
    <#include "copyright.ftl">
    
    All Rights Reserved. <a href="${urls.termsOfUse}">Terms of Use</a>

</div>

