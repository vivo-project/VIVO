<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

    <#-- Note to UI team: do not change this div without also making the corresponding change in menu.jsp -->
    <div id="navAndSearch" class="block">
        <div id="primaryAndOther">
            <ul id="primary">
                <#list tabMenu.items as item>
                    <li>
                        <a href="${item.url}" <#if item.active> class="activeTab" </#if>>
                            ${item.linkText}
                        </a>
                    </li>
                </#list>
            </ul>
        </div>

        <div id="searchBlock">
            <form id="searchForm" action="${urls.search}" >
                <label for="search">Search </label>

                <#if user.showFlag1SearchField>
                    <select id="search-form-modifier" name="flag1" class="form-item" >
                        <option value="nofiltering" selected="selected">entire database (${user.loginName})</option>
                        <option value="${portalId}">${siteTagline!}</option>
                    </select>
                <#else>
                    <input type="hidden" name="flag1" value="${portalId}" />
                </#if>

                <input type="text" name="querytext" id="search" class="search-form-item" value="${querytext!}" size="20" />
                <input class="search-form-submit" name="submit" type="submit"  value="Search" />
            </form>
        </div>
    </div> <!-- navAndSearch -->
</div> <!-- header --> 

<hr class="hidden" />

<div id="contentwrap">
    <#if flash?has_content>
        <section id="flash-message" role="alert">
            ${flash}
        </section>
    </#if> 

    <div id="content">