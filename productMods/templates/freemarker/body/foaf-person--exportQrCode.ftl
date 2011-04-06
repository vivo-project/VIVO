<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Page providing options for disseminating QR codes -->

<#include "individual-qrCodeFoafPerson.ftl">

<#assign qrCodeWidth = "150">

<h2>Export QR Code</h2>
<div>
	<div style="float:left">
		<#assign thumbUrl = individual.thumbUrl! "${urls.images}/placeholders/person.thumbnail.jpg" >
		<img src="${thumbUrl}" />
	</div>
	<div style="float:left">
		<h3>${individual.nameStatement.value}</h3>
	</div>
	<div style="clear:both" />
</div>

<div style="border:1px solid #cccccc">
	<h4 style="padding-left:5px">VCard</h4>
	<@qrCodeVCard qrCodeWidth="150" />
	<textarea readonly="readonly" style="width:600px;height:120px">
		&lt;img src="${getQrCodeUrlForVCard(qrCodeWidth)}" /&gt;<#t>
	</textarea><#t>
</div>

<div style="border:1px solid #cccccc">
	<h4 style="padding-left:5px">Hyperlink</h4>
	<@qrCodeLink qrCodeWidth="150" />
	<textarea readonly="readonly" style="width:600px;height:120px">
		&lt;img src="${getQrCodeUrlForLink(qrCodeWidth)}" /&gt;<#t>
	</textarea><#t>
</div>



