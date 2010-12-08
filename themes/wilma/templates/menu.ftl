<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<nav role="navigation">
    <ul id="main-nav" role="list">
        <#list menu.items as item>
            <li role="listitem"><a href="${item.url}" <#if item.active> class="selected" </#if>>${item.linkText}</a></li>
        </#list>
    </ul>
</nav>

<div id="wrapper-content" role="main">        
    <#if flash?has_content>
        <section id="flash-message" role="alert">
            ${flash}
        </section>
    </#if>