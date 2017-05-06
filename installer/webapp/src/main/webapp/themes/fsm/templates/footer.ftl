<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

</div> <!-- #wrapper-content -->

<#--
<footer role="contentinfo">
    <p class="copyright">
        <#if copyright??>
            <small>&copy;${copyright.year?c}
            <#if copyright.url??>
                <a href="${copyright.url}" title="${i18n().menu_copyright}">${copyright.text}</a>
            <#else>
                ${copyright.text}, Evanston, IL
            </#if>
             | <a class="terms" href="${urls.termsOfUse}" title="${i18n().menu_termuse}">${i18n().menu_termuse}</a></small> | 
        </#if>
        ${i18n().menu_powered} <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank" title="${i18n().menu_powered} VIVO"><strong>VIVO</strong></a>
        <#if user.hasRevisionInfoAccess>
             | ${i18n().menu_version} <a href="${version.moreInfoUrl}" title="${i18n().menu_version}">${version.label}</a>
        </#if>
    </p>

    
    <nav role="navigation">
        <ul id="footer-nav" role="list">
            <li role="listitem"><a href="${urls.about}" title="${i18n().menu_about}">${i18n().menu_about}</a></li>
            <#if urls.contact??>
                <li role="listitem"><a href="${urls.contact}" title="${i18n().menu_contactus}">${i18n().menu_contactus}</a></li>
            </#if> 
            <li role="listitem"><a href="http://www.vivoweb.org/support" target="blank" title="${i18n().menu_support}">${i18n().menu_support}</a></li>
        </ul>
    </nav>
</footer>
-->

<footer>
    <div id="NU-logo">
        <a href="http://www.northwestern.edu"><img alt="Northwestern University" src="http://nucats.northwestern.edu/sites/all/themes/nucats/images/footer/nu-footer-logo.png"></a></div>
        <div class="footer_content">
            <p>${i18n().menu_powered} <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank" title="${i18n().menu_powered} VIVO"><strong>VIVO</strong></a></p>

            <p>Don't forget to cite the <a href="https://www.ctsacentral.org/">CTSA</a> Grant: UL1TR000150.<br><a href="http://nucats.northwestern.edu/about/ctsa-resources">NIH Citation Requirements</a></p>

            <p><a href="http://www.northwestern.edu/"></a><a href="http://planitpurple.northwestern.edu/">Northwestern Calendar: PlanIt Purple</a> | <a href="http://googlesearch.northwestern.edu/">Northwestern Search</a></p>
        </div>
        <div class="multiSiteFooter">
            <ul><li><a href="http://www.northwestern.edu/contact.html">Contact</a></li>
                <li><a href="http://www.northwestern.edu/disclaimer.html">Disclaimer</a></li>
                <li><a href="http://www.northwestern.edu/emergency/index.html">Emergency Information</a></li>
                <li><a href="http://policies.northwestern.edu/">University Policies</a></li>
                <li><a href="http://m.northwestern.edu/">Mobile</a></li>
            </ul></div>
        </div>
    </div>
</footer>

<#include "scripts.ftl">