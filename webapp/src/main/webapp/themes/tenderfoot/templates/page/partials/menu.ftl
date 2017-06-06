<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<div id="nav">
    <div class="container">
        <nav class="navbar navbar-default">
            <p class="navbar-text visible-xs-inline-block">Menu</p>
            <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarContent">
                <ul id="main-nav" role="list" class="nav navbar-nav mr-auto mt-2 mt-md-0">
                <#list menu.items as item>
                    <li class="nav-item" role="listitem" <#if item.active> class="active" </#if>><a href="${item.url}" title="${item.linkText} ${i18n().menu_item}" class="nav-link">${item.linkText}</a></li>
                </#list>
                </ul>
                <ul class="nav pull-right navbar-nav">
                    <li>
                    <#include "search.ftl">
                    </li>
                </ul>
        </nav>
    </div>
</div>
