<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<div id="nav-wrapper">
    <div id="nav">
        <div class="container">
            <nav class="navbar navbar-default">
                <div class="navbar-header pull-right">
                    <ul class="nav pull-left">
                        <li>
                        <#include "search.ftl">
                        </li>
                    </ul>
                </div>

                <button class="navbar-toggle pull-left" type="button" data-toggle="collapse" data-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="button-label">${i18n().collapsed_menu_name}</span>
                    <div class="button-bars">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </div>
                </button>

                <div class="collapse navbar-collapse navbar-left" id="navbarContent">
                    <ul id="main-nav" role="list" class="nav navbar-nav mr-auto mt-2 mt-md-0">
                    <#list menu.items as item>
                        <li class="nav-item" role="listitem" <#if item.active> class="active" </#if>><a href="${item.url}" title="${item.linkText} ${i18n().menu_item}" class="nav-link">${item.linkText}</a></li>
                    </#list>
                    </ul>
                </div>
            </nav>
        </div>
    </div>
</div>
