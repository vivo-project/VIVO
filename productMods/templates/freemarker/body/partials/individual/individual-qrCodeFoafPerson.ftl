<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- QR code -->

<#macro qrCodeVCard qrCodeWidth>

<#--
Example:

BEGIN:VCARD
VERSION:3.0
N:Conlon;Michael
FN:Michael Conlon
ORG:University of Florida
TITLE:Associate Director and Chief Operating Officer
TEL;TYPE=WORK,VOICE:(352) 273-8872
EMAIL;TYPE=PREF,INTERNET:mconlon@ufl.edu
URL:https://vivo.ufl.edu/display/n25562
PHOTO;VALUE=URL;TYPE=JPG:https://vivo.ufl.edu/file/n34850/_main_image_491-NUCATS-STS-042310.jpg
REV:20080424T195243Z
END:VCARD
-->

	<#local core = "http://vivoweb.org/ontology/core#">
	<#local foaf = "http://xmlns.com/foaf/0.1/">
	<#local rdfs = "http://www.w3.org/2000/01/rdf-schema#">

	<#local label = individual.nameStatement.value >
	<#local firstName = (allProperties.getProperty("${foaf}firstName").firstValue)! >
	<#local lastName = (allProperties.getProperty("${foaf}lastName").firstValue)! >
	<#local org = "temp" >
	<#local title = "temp" >
	<#local phoneNumber = (allProperties.getProperty("${core}phoneNumber").firstValue)! >
	<#local email = (allProperties.getProperty("${core}email").firstValue)! >
	<#local url = "temp" >
	<#local photo = "temp" >
	<#local rev = "temp" >

	<#assign vCard><#t>
		BEGIN:VCARD<#lt>
		VERSION:3.0<#lt>
		N:${lastName};${firstName}<#lt>
		FN:${firstName} ${lastName}<#lt>
		ORG:${org}<#lt>
		TITLE:${title}<#lt>
		TEL;TYPE=WORK,VOICE:${phoneNumber}<#lt>
		EMAIL;TYPE=PREF,INTERNET:${email}<#lt>
		URL:${url}<#lt>
		PHOTO;VALUE=URL;TYPE=JPG:${photo}<#lt>
		REV:${rev}<#lt>
		END:VCARD<#t>
	</#assign><#t>
	
	<#local qrCodeUrl = "https://chart.googleapis.com/chart?cht=qr&amp;chs=${qrCodeWidth}x${qrCodeWidth}&amp;chl=${vCard}&amp;choe=UTF-8" >
	
	<img src="${qrCodeUrl}" />
</#macro>
