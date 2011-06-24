<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#include "individual-qrCodeGenerator.ftl">

<#-- This macro will display a vCard QR code (includes email, phone number, URI, etc)
     * default display is a small icon that reveals the full QR code when clicked
     * setting the display to "full" will render a full-size QR code (<@qrCode display="full" />)
     * the size can be set using the width parameter (default is 125px)
 -->
<#macro qrCode display="icon" width="125">
    <#if hasValidVCard()>
        <#assign qrCodeLinkedImage><a title="Export QR codes" href="${individual.doQrData().exportQrCodeUrl}"><@qrCodeVCard qrCodeWidth=width /></a></#assign>
        
        <#if (display == "full")>
            <h5 class="qrCode">vCard QR</h5>
            ${qrCodeLinkedImage}
        <#elseif (display == "icon")>
            <li role="listitem">
                <a id="qrIcon" title="vCard QR Code" href="${individual.doQrData().exportQrCodeUrl}"><img class="middle" src="${urls.images}/individual/qr_icon.png" alt="qr icon" /></a>
                <span id="qrCodeImage" class="hide">${qrCodeLinkedImage} <a class="qrCloseLink" href="#">Close</a></span>
            </li>
        <#else>
            <p class="notice">You have passed an invalid value for the qrCode display parameter.</p>
        </#if>
        
    </#if>
</#macro>