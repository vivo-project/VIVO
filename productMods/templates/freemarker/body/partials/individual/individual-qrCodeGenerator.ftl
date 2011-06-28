<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- QR code -->

<#macro qrCodeVCard qrCodeWidth>

    <#local qrCodeUrl = getQrCodeUrlForVCard(qrCodeWidth)>

    <#if qrCodeUrl != "">
        <img src="${qrCodeUrl}" />
    </#if>
</#macro>

<#macro qrCodeLink qrCodeWidth>

    <#local qrCodeUrl = getQrCodeUrlForLink(qrCodeWidth)>

    <#if qrCodeUrl != "">
        <img src="${qrCodeUrl}" />
    </#if>
</#macro>

<#-- This macro will display a vCard QR code (includes email, phone number, URI, etc)
     * default display is a small icon that reveals the full QR code when clicked
     * setting the display to "full" will render a full-size QR code (<@renderCode display="full" />)
     * the size can be set using the width parameter (default is 125px)
 -->
<#macro renderCode display="icon" width="125">
    <#if hasValidVCard()>
        <#assign qrCodeLinkedImage><a title="Export QR codes" href="${individual.doQrData().exportQrCodeUrl}"><@qrCodeVCard qrCodeWidth=width /></a></#assign>
        
        <#if (display == "full")>
            <h5 class="qrCode">vCard QR</h5>
            ${qrCodeLinkedImage}
        <#elseif (display == "icon")>
            <li role="listitem">
                <a id="qrIcon" title="vCard QR Code" href="${individual.doQrData().exportQrCodeUrl}"><img class="middle" src="${urls.images}/individual/qr_icon.png" alt="qr icon" /></a>
                <span id="qrCodeImage" class="hidden">${qrCodeLinkedImage} <a class="qrCloseLink" href="#">Close</a></span>
            </li>
        <#else>
            <p class="notice">You have passed an invalid value for the qrCode display parameter.</p>
        </#if>
        
    </#if>
</#macro>



<#function getQrCodeUrlForVCard qrCodeWidth>

    <#local qrData = individual.doQrData() >

    <#local core = "http://vivoweb.org/ontology/core#">
    <#local foaf = "http://xmlns.com/foaf/0.1/">
    <#local rdfs = "http://www.w3.org/2000/01/rdf-schema#">

    <#local firstName = qrData.firstName! >
    <#local lastName = qrData.lastName! >
    <#local org = "" >
    <#local title = qrData.preferredTitle! >
    <#local phoneNumber = qrData.phoneNumber! >
    <#local email = qrData.email! >
    <#local url = qrData.externalUrl! >
    <#local photo = individual.thumbUrl! >
    <#local rev = "" >

    <#local qrCodeUrl = "">
    <#if firstName != "" && lastName != "">
        <#local vCard><#t>
            BEGIN:VCARD<#lt>
            VERSION:3.0<#lt>
            N:${lastName};${firstName}<#lt>
            FN:${firstName} ${lastName}<#lt>
            <#if org != ""> ORG:${org}</#if><#lt>
            <#if title != "">TITLE:${title}</#if><#lt>
            <#if phoneNumber != "">TEL;TYPE=WORK,VOICE:${phoneNumber}</#if><#lt>
            <#if email != "">EMAIL;TYPE=PREF,INTERNET:${email}</#if><#lt>
            <#if url != "">URL:${url}</#if><#lt>
            <#if photo != "">PHOTO;VALUE=URL;TYPE=JPG:${photo}</#if><#lt>
            <#if rev != "">REV:${rev}</#if><#lt>
            END:VCARD<#t>
        </#local><#t>

        <#local vCard = (removeBlankLines(vCard))?url>

        <#local qrCodeUrl = "https://chart.googleapis.com/chart?cht=qr&amp;chs=${qrCodeWidth}x${qrCodeWidth}&amp;chl=${vCard}&amp;choe=UTF-8" >
    </#if>

    <#return qrCodeUrl>
</#function>



<#function getQrCodeUrlForLink qrCodeWidth>

    <#local qrData = individual.doQrData() >

    <#local url = qrData.externalUrl! >

    <#local qrCodeUrl = "">
    <#if url != "">
        <#local qrCodeContent = url?url> 
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

    <#local qrData = individual.doQrData() >

    <#local firstName = qrData.firstName! >
    <#local lastName = qrData.lastName! >

    <#local validVCard = false>
    <#if firstName != "" && lastName != "">
        <#local validVCard = true>
    </#if>

    <#return validVCard>
</#function>