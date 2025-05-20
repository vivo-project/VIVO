<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<meta charset="utf-8" />
<!-- Google Chrome Frame open source plug-in brings Google Chrome's open web technologies and speedy JavaScript engine to Internet Explorer-->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="generator" content="VIVO ${version.label}" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">

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

${headScripts.add('<script type="text/javascript" src="${urls.base}/webjars/floatingui/floating-ui.core.umd.js"></script>')}
${headScripts.add('<script type="text/javascript" src="${urls.base}/webjars/floatingui/floating-ui.dom.umd.js"></script>')}
${headScripts.add('<script type="text/javascript" src="${urls.base}/js/tooltip/tooltip-utils.js"></script>')}

<#include "stylesheets.ftl">
<link rel="stylesheet" href="${urls.theme}/css/screen.css" />

<#include "headScripts.ftl">

<#if metaTags??>
    ${metaTags.list()}
</#if>

<!--[if (gte IE 6)&(lte IE 8)]>
<script type="text/javascript" src="${urls.base}/js/selectivizr.js"></script>
<![endif]-->

<#-- Inject head content specified in the controller. Currently this is used only to generate an rdf link on
an individual profile page. -->
${headContent!}

<style>
    :root {
        <#if themePrimaryColorLighter?? && themePrimaryColorLighter != "null">--primary-color-lighter: ${themePrimaryColorLighter};</#if>
        <#if themePrimaryColor?? && themePrimaryColor != "null">--primary-color: ${themePrimaryColor};</#if>
        <#if themePrimaryColorDarker?? && themePrimaryColorDarker != "null">--primary-color-darker: ${themePrimaryColorDarker};</#if>
        <#if themeBannerColor?? && themeBannerColor != "null">--banner-color: ${themeBannerColor};</#if>
        <#if themeSecondaryColor?? && themeSecondaryColor != "null">--secondary-color: ${themeSecondaryColor};</#if>
        <#if themeAccentColor?? && themeAccentColor != "null">--accent-color: ${themeAccentColor};</#if>
        <#if themeLinkColor?? && themeLinkColor != "null">--link-color: ${themeLinkColor};</#if>   
        <#if themeTextColor?? && themeTextColor != "null">--text-color: ${themeTextColor};</#if>
        <#if logoUrl?? && logoUrl != "null">--logo-url: url('${logoUrl}');</#if>
        <#if logoSmallUrl?? && logoSmallUrl != "null">--logo-small-url: url('${logoSmallUrl}');</#if>
    }
</style>

<link rel="shortcut icon" type="image/x-icon" href="${urls.base}/favicon.ico">
