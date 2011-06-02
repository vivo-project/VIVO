<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Page providing options for disseminating QR codes -->

<#include "individual-qrCodeGenerator.ftl">

<#assign qrCodeWidth = "150">

<h2>Export QR Code <em>(<a href="${individual.doQrData().aboutQrCodesUrl}" title="More info on QR codes">What is this?</a>)</em></h2>

<#assign thumbUrl = individual.thumbUrl! "${urls.images}/placeholders/person.thumbnail.jpg" >
<img class="qrCode" src="${thumbUrl}" width="160" />

<h3 class="qrCode"><a href="${individual.profileUrl}" title="View this person's profile">${individual.nameStatement.value}</a></h3>

<section class="vcard">
	<h4>VCard</h4>
	<@qrCodeVCard qrCodeWidth="150" />
	<textarea name="qrCodeVCard" readonly>
		&lt;img src="${getQrCodeUrlForVCard(qrCodeWidth)}" /&gt;<#t>
	</textarea><#t>
</section>

<section>
	<h4>Hyperlink</h4>
	<@qrCodeLink qrCodeWidth="150" />
	<textarea name="qrCodeLink" readonly>
		&lt;img src="${getQrCodeUrlForLink(qrCodeWidth)}" /&gt;<#t>
	</textarea><#t>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-qr.css" />')}