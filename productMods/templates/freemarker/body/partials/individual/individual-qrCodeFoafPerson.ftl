<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#include "individual-qrCodeGenerator.ftl"> 

<#if hasValidVCard()>
    <li role="listitem"><a title="Export QR codes" href="${individual.doQrData().exportQrCodeUrl}"><img class="middle" src="${urls.images}/individual/qr_icon.png" alt="qr icon" /></a></li>
</#if>