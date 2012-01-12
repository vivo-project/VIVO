<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Page providing options for disseminating QR codes -->
<#assign qrCodeWidth = "150">

<#include "individual-qrCodeGenerator.ftl">

<h2>Export QR Code <em>(<a href="${individual.qrData().aboutQrCodesUrl}" title="More info on QR codes">What is this?</a>)</em></h2>

<#assign thumbUrl = individual.thumbUrl! "${urls.images}/placeholders/person.thumbnail.jpg" >
<img class="individual-photo qrCode" src="${thumbUrl}" width="160" />

<h3 class="qrCode"><a href="${individual.profileUrl}" title="View this person's profile">${individual.nameStatement.value}</a></h3>

<section class="vcard">
	<h4>VCard</h4>
	<@qrCodeVCard qrCodeWidth />
	<textarea name="qrCodeVCard" readonly>
		&lt;img src="${getQrCodeUrlForVCard(qrCodeWidth)!}" /&gt;<#t>
	</textarea><#t>
</section>

<section>
	<h4>Hyperlink</h4>
	<@qrCodeLink qrCodeWidth />
	<textarea name="qrCodeLink" readonly>
		&lt;img src="${getQrCodeUrlForLink(qrCodeWidth)!}" /&gt;<#t>
	</textarea><#t>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-qr.css" />')}