<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- QR code -->

<#macro qrCodeFoafPerson qrCodeWidth>

	<#local foaf = "http://xmlns.com/foaf/0.1/">
	<#local rdfs = "http://www.w3.org/2000/01/rdf-schema#">

	<#local label = individual.nameStatement.value >
	<#local workPhone = (propertyGroups.getProperty("${core}workPhone").firstValue)! >
	<#local firstName = (propertyGroups.getProperty("${foaf}firstName").firstValue)! >
	<#local lastName = (propertyGroups.getProperty("${foaf}lastName").firstValue)! >

	<#assign vCard><#t>
	    BEGIN:VCARD%0A<#t>
	    VERSION:3.0%0A<#t>
	    N:${lastName};${firstName}%0A<#t>
	    FN:${label}%0A<#t>
	    <#if workPhone??><#t>
	    	TEL;TYPE=WORK,VOICE:${workPhone}%0A<#t>
	    </#if><#t>
	    <#if email.statements?has_content><#t>
	        <#list email.statements as statement><#t>
	        	EMAIL;TYPE=PREF,INTERNET:${statement.value}%0A<#t>
	        </#list><#t>
	    </#if><#t>
	    END:VCARD<#t>
	</#assign><#t>
	
	<#local qrCodeUrl = "https://chart.googleapis.com/chart?cht=qr&amp;chs=${qrCodeWidth}x${qrCodeWidth}&amp;chl=${vCard}&amp;choe=UTF-8" >
	
	<img src="${qrCodeUrl}" />
</#macro>
	