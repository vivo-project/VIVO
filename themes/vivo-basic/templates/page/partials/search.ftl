<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<div id="searchBlock">
    <form id="searchForm" action="${urls.search}" >                    
        <label for="search">Search </label>

        <#if showFlag1SearchField??>
            <select id="search-form-modifier" name="flag1" class="form-item" >
                <option value="nofiltering" selected="selected">entire database (${loginName})</option>
                <option value="${portalId}">${siteTagline!}</option>
            </select>
        <#else>
            <input type="hidden" name="flag1" value="${portalId}" />
        </#if>
            
        <input type="text" name="querytext" id="search" class="search-form-item" value="${querytext!}" size="20" />
        <input class="search-form-submit" name="submit" type="submit"  value="Search" />
    </form>
</div>