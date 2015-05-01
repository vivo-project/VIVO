<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- QR code -->

<#macro qrCodeVCard qrCodeWidth>

    <#local qrCodeUrl = getQrCodeUrlForVCard(qrCodeWidth)!>

    <#if qrCodeUrl != "">
        <img src="${qrCodeUrl}" />
    </#if>
</#macro>

<#macro qrCodeLink qrCodeWidth>

    <#local qrCodeUrl = getQrCodeUrlForLink(qrCodeWidth)!>

    <#if qrCodeUrl != "">
        <img src="${qrCodeUrl}" />
    </#if>
</#macro>

<#-- This macro will display a vCard QR code (includes email, phone number, URI, etc)
     * default display is a small icon that reveals the full QR code when clicked
     * setting the display to "full" will render a full-size QR code (<@renderCode display="full" />)
     * the size can be set using the width parameter (default is 125px)
 -->
<#--
    added the imageFile parameter because a different icon is used for the wilma theme (qr_icon.png)
    than is used for the 2 column and quick views (qr-code-icon.png).
-->
<#macro renderCode imageFile display="icon" width="125">
    <#if hasValidVCard()>
        <#local qrData = qrData>
        <#local qrCodeLinkedImage><a title="${i18n().export_qr_codes}" href="${qrData.exportQrCodeUrl}"><@qrCodeVCard qrCodeWidth=width /></a></#local>
        
        <#if (display == "full")>
            <h5 class="qrCode">${i18n().vcard_qr}</h5>
            ${qrCodeLinkedImage}
        <#elseif (display == "icon")>
                <a id="qrIcon" title="${i18n().vcard_qr_code}" href="${qrData.exportQrCodeUrl}"><img  src="${urls.images}/individual/${imageFile!}" alt="${i18n().qr_icon}" /></a>
                <span id="qrCodeImage" class="hidden">${qrCodeLinkedImage} <a class="qrCloseLink" href="#"  title="${i18n().qr_code}">${i18n().close_capitalized}</a></span>
        <#else>
            <p class="notice">${i18n().invalid_qr_code_parameter}</p>
        </#if>
        
    </#if>
</#macro>



<#function getQrCodeUrlForVCard qrCodeWidth>

    <#local qrData = qrData>

    <#local core = "http://vivoweb.org/ontology/core#">
    <#local foaf = "http://xmlns.com/foaf/0.1/">
    <#local rdfs = "http://www.w3.org/2000/01/rdf-schema#">

    <#local firstName = qrData.firstName! >
    <#local lastName = qrData.lastName! >
    <#local org = "" >
    <#local title = qrData.preferredTitle! >
    <#local phoneNumber = qrData.phoneNumber! >
    <#local email = qrData.email! >
    <#local url = individual.uri! >
    <#local photo = individual.thumbUrl! >
    <#local rev = "" >
    
    <#if firstName != "" && lastName != "">
        <#local vCard><#t>
            BEGIN:VCARD<#lt>
            VERSION:3.0<#lt>
            N:${lastName};${firstName}<#lt>
            FN:${firstName} ${lastName}<#lt>
            <#if org?has_content> ORG:${org}</#if><#lt>
            <#if title?has_content>TITLE:${title}</#if><#lt>
            <#if phoneNumber?has_content>TEL;TYPE=WORK,VOICE:${phoneNumber}</#if><#lt>
            <#if email?has_content>EMAIL;TYPE=PREF,INTERNET:${email}</#if><#lt>
            <#if url?has_content>URL:${url}</#if><#lt>
            <#if photo?has_content>PHOTO;VALUE=URL;TYPE=JPG:${photo}</#if><#lt>
            <#if rev?has_content>REV:${rev}</#if><#lt>
            END:VCARD<#t>
        </#local><#t>

        <#local vCard = (removeBlankLines(vCard))?url>

        <#local qrCodeUrl = "https://chart.googleapis.com/chart?cht=qr&amp;chs=${qrCodeWidth}x${qrCodeWidth}&amp;chl=${vCard}&amp;choe=UTF-8" >
    </#if>

    <#return qrCodeUrl>
</#function>



<#function getQrCodeUrlForLink qrCodeWidth>

    <#local qrData = qrData>

    <#local externalUrl = qrData.externalUrl! >

    <#local qrCodeUrl = "">
    <#if externalUrl != "">
    	<#local fullExternalUrl = externalUrl + individual.profileUrl> 
    	<#local qrCodeContent = fullExternalUrl?url> 
        <#local qrCodeUrl = "https://chart.googleapis.com/chart?cht=qr&amp;chs=${qrCodeWidth}x${qrCodeWidth}&amp;chl=${qrCodeContent}&amp;choe=UTF-8" >
    </#if>

    <#return qrCodeUrl>
</#function>



<#function removeBlankLines input>

    <#local test = "\n\n">
    <#local replacement = "\n">

    <#local output = input>

    <#local maxLoop = 50>
    <#list 1..maxLoop as i>
        <#if output?contains(test)>
            <#local output = output?replace(test, replacement)>
        <#else>
            <#break>
        </#if>
    </#list>

    <#return output>
</#function>

<#function hasValidVCard>

    <#local qrData = qrData>

    <#local firstName = qrData.firstName! >
    <#local lastName = qrData.lastName! >

    <#local validVCard = false>
    <#if firstName != "" && lastName != "">
        <#local validVCard = true>
    </#if>

    <#return validVCard>
</#function>