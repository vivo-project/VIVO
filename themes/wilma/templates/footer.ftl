<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#-- This is a temporary file and will be removed once we have completed the transition to freemarker -->

<footer>
    <#if copyright??>
        <p class="copyright"><small>&copy;${copyright.year?c} 
    <#if copyright.url??>
        <a href="${copyright.url}">${copyright.text}</a>  
    <#else>
        ${copyright.text}
    </#if> 
        All Rights Reserved | <a class="terms" href="${urls.termsOfUse}">Terms of Use</a></small> | Powered by <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank"><strong>VIVO</strong></a></p>
    </#if>
    
    <nav>
        <ul id="footer-nav">
            <li><a href="${urls.about}">About</a></li>
            <#if urls.contact??>
                <li><a href="${urls.contact}">Contact Us</a></li>
            </#if>
                <li><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
        </ul>
    </nav>
</footer>

${scripts.add("/js/jquery.js")}
${scripts.tags}

<#include "googleAnalytics.ftl">