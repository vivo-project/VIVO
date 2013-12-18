<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- profile page type select element on individual 2-column profile page -->

<div id="profileTypeContainer" <#if !user.hasSiteAdminAccess>style="top:55px"</#if> >
    <h2>${i18n().profile_type}</h2>
    <select id="profilePageType">
        <option value="standard" <#if profileType == "standard" || profileType == "none">selected</#if> >${i18n().standard_view}</option>
        <option value="quickView" <#if profileType == "quickView">selected</#if> >${i18n().quick_view}</option>
    </select>
</div>
