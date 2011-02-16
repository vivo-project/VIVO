<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-list.ftl" as l>

        </div> <!-- content -->
    </div> <!-- contentwrap -->

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

        <#include "version.ftl">
    </div>

</div> <!-- wrap --> 

<#include "scripts.ftl">