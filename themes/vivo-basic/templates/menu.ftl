<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

    <#-- Note to UI team: do not change this div without also making the corresponding change in menu.jsp -->
    <div id="navAndSearch" class="block">
        <div id="primaryAndOther">
            <ul id="primary"><li> &nbsp; <!-- tabs have been removed --> </li></ul>
        </div>
        <div id="searchBlock">
            <form id="searchForm" action="${urls.search}" >
                <label for="search">Search </label>
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
