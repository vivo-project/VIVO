<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

</div> <!-- #wrapper-content -->

<footer role="contentinfo">
    <p class="copyright">
        <#if copyright??>
            <small>&copy;${copyright.year?c}
            <#if copyright.url??>
                <a href="${copyright.url}">${copyright.text}</a>
            <#else>
                ${copyright.text}
            </#if>
             | <a class="terms" href="${urls.termsOfUse}">Terms of Use</a></small> | 
        </#if>
        Powered by <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank"><strong>VIVO</strong></a>
        <#if user.hasRevisionInfoAccess>
             | Version <a href="${version.moreInfoUrl}">${version.label}</a>
        </#if>
    </p>
    
    <nav role="navigation">
        <ul id="footer-nav" role="list">
            <li role="listitem"><a href="${urls.about}">About</a></li>
            <#if urls.contact??>
                <li role="listitem"><a href="${urls.contact}">Contact Us</a></li>
            </#if> 
            <li role="listitem"><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
        </ul>
    </nav>
</footer>

<#include "scripts.ftl">