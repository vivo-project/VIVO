<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Page providing options for disseminating QR codes -->
<#assign qrCodeWidth = "150">

<#include "individual-qrCodeGenerator.ftl">

<h2>${i18n().export_qr_code} <em>(<a href="${qrData.aboutQrCodesUrl}" title="${i18n().more_qr_info}">${i18n().what_is_this}</a>)</em></h2>

<#assign thumbUrl = individual.thumbUrl! "${urls.images}/placeholders/person.thumbnail.jpg" >
<img class="individual-photo qrCode" src="${thumbUrl}" width="160" alt="${i18n().alt_thumbnail_photo}"/>

<h3 class="qrCode"><a href="${individual.profileUrl}" title="${i18n().view_this_profile}">${individual.nameStatement.value}</a></h3>

<section class="vcard">
	<h4>${i18n().vcard}</h4>
	<@qrCodeVCard qrCodeWidth />
	<textarea name="qrCodeVCard" readonly>
		&lt;img src="${getQrCodeUrlForVCard(qrCodeWidth)!}" /&gt;<#t>
	</textarea><#t>
</section>

<section>
	<h4>${i18n().hyperlink}</h4>
	<@qrCodeLink qrCodeWidth />
	<textarea name="qrCodeLink" readonly>
		&lt;img src="${getQrCodeUrlForLink(qrCodeWidth)!}" /&gt;<#t>
	</textarea><#t>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-qr.css" />')}