<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<meta charset="utf-8" />
<!-- Google Chrome Frame open source plug-in brings Google Chrome's open web technologies and speedy JavaScript engine to Internet Explorer-->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>${(title?html)!siteName!}</title>

<#-- VIVO OpenSocial Extension by UCSF -->
<#if openSocial??>
    <#if openSocial.visible>
        <#-- Required to add these BEFORE stylesheets.flt and headScripts.ftl are processed -->
        ${stylesheets.add('<link rel="stylesheet" href="${urls.theme}/css/openSocial/gadgets.css" />')}
        ${headScripts.add('<script type="text/javascript" src="${openSocial.containerJavascriptSrc}"></script>',
                          '<script type="text/javascript" language="javascript">${openSocial.gadgetJavascript}</script>',
                          '<script type="text/javascript" src="${urls.base}/js/openSocial/orng.js"></script>')}
    </#if>
</#if>

<#include "stylesheets.ftl">
<link rel="stylesheet" href="${urls.theme}/css/screen.css" />

<#include "headScripts.ftl">

<!--[if (gte IE 6)&(lte IE 8)]>
<script type="text/javascript" src="${urls.base}/js/selectivizr.js"></script>
<![endif]-->

<#-- Inject head content specified in the controller. Currently this is used only to generate an rdf link on
an individual profile page. -->
${headContent!}

<style>
    :root {
        <#if themePrimaryColorLighter?has_content>--primary-color-lighter: ${themePrimaryColorLighter};</#if>
        <#if themePrimaryColor?has_content>--primary-color: ${themePrimaryColor};</#if>
        <#if themePrimaryColorDarker?has_content>--primary-color-darker: ${themePrimaryColorDarker};</#if>
        <#if themeBannerColor?has_content>--banner-color: ${themeBannerColor};</#if>
        <#if themeSecondaryColor?has_content>--secondary-color: ${themeSecondaryColor};</#if>
        <#if themeAccentColor?has_content>--accent-color: ${themeAccentColor};</#if>
        <#if themeLinkColor?has_content>--link-color: ${themeLinkColor};</#if>   
        <#if themeTextColor?has_content>--text-color: ${themeTextColor};</#if>
        <#if logoUrl?has_content>--logo-url: url('${logoUrl}');</#if>
        <#if logoSmallUrl?has_content>--logo-small-url: url('${logoSmallUrl}');</#if>
    }
</style>

<link rel="shortcut icon" type="image/x-icon" href="${urls.base}/favicon.ico">
