<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

</div> <!-- #wrapper-content -->

<footer role="contentinfo">
    <#if copyright??>
        <p class="copyright"><small>&copy;${copyright.year?c} 
        <#if copyright.url??>
            <a href="${copyright.url}">${copyright.text}</a>  
        <#else>
        </#if>
            All Rights Reserved | <a class="terms" href="${urls.termsOfUse}">Terms of Use</a></small> | Powered by <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank"><strong>VIVO</strong></a> 
            <#if user.hasRevisionInfoAccess>
                | Version <a href="${version.moreInfoUrl}">${version.label}</a>
            </#if>
        </p>
    </#if>
       
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

<!--[if lt IE 7]>
<script type="text/javascript" src="${urls.base}/js/jquery_plugins/supersleight.js"></script>
<script type="text/javascript" src="${urls.base}/js/utils.js"></script>
<![endif]-->

<!--[if (gte IE 6)&(lte IE 8)]>
<script type="text/javascript" src="${urls.base}/js/selectivizr.js"></script>
<![endif]-->

<#include "scripts.ftl">