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

	<#local firstName = (allProperties.getProperty("${foaf}firstName").firstValue)! >
	<#local lastName = (allProperties.getProperty("${foaf}lastName").firstValue)! >
	<#local org = "" >
	<#local title = (allProperties.getProperty("${core}preferredTitle").firstValue)! >
	<#local phoneNumber = (allProperties.getProperty("${core}phoneNumber").firstValue)! >
	<#local email = (allProperties.getProperty("${core}email").firstValue)! >
	<#local url = urls.currentPage! >
	<#local photo = individual.thumbUrl! >
	<#local rev = "temp" >

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
	
		<img src="${qrCodeUrl}" />
	</#if>
</#macro>

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


