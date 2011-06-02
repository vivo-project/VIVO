<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
Menu Edit Form
Associated with generator:
edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.MenuEditingFormGenerator
-->



<h2>${editConfiguration.formTitle}</h2>
<#assign pageTitle = editConfiguration.literalValues["title"].getString() />
<form class="editForm" action = "${editConfiguration.submitToUrl}">
	
	<input name="title" id="title" value="${pageTitle}">
	
	
	<input type="hidden" name="editKey" value="${editConfiguration.editKey}" />
	
	<div style="margin-top: 0.2em">
		<input type="submit" value="${editConfiguration.submitLabel}" />
	</div>
	
</form>


<@dumpAll/>