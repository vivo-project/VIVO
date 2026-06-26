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
        <#if brandingColors?has_content>
            <#list brandingColors?keys as key>
                ${key}: ${brandingColors[key]};
            </#list>
        </#if>
        <#if logoUrl?has_content >--logo-url: url('${logoUrl}');</#if>
        <#if logoSmallUrl?has_content >--logo-small-url: url('${logoSmallUrl}');</#if>
    }
</style>

<script>
    var globalI18nStrings = {
        brandingColorsSubmitAlert: '${i18n().branding_colors_submit_alert?js_string}',
        brandingColorsCancelAlert: '${i18n().branding_colors_cancel_alert?js_string}',
        brandingColorsResetAlert: '${i18n().branding_colors_reset_alert?js_string}',
        brandingColorsErrorFetchConfig: '${i18n().branding_colors_error_fetch_config?js_string}',
        brandingColorsErrorFormatConfig: '${i18n().branding_colors_error_format_config?js_string}',
        brandingColorsErrorUnexpectedConfig: '${i18n().branding_colors_error_unexpected_config?js_string}',
        brandingColorsOpenAfterSave: '${i18n().branding_colors_open_after_save?js_string}',
    }
</script>

<#if customCssPath?has_content >
    <link id="custom-css-path" rel="stylesheet" href="${customCssPath}">
</#if>

<link rel="shortcut icon" type="image/x-icon" href="${urls.base}/favicon.ico">
