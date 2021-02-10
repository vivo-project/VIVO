<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#-- Custom form for managing labels for individuals - specific to VIVO which handles people as well -->
<#assign isPersonType = "false"/>
<#if editConfiguration.pageData.isPersonType?has_content>
	<#assign isPersonType = editConfiguration.pageData.isPersonType />
</#if>
<form id="addLabelForm" name="addLabelForm" class="customForm" action="${submitUrl}">
           <h2>${i18n().add_label}</h2>
           <#if isPersonType?has_content && isPersonType = "true">
	        <p>
	            <label for="firstName">${i18n().first_name} ${requiredHint}</label>
	            <input size="30"  type="text" id="firstName" name="firstName" value="${firstNameValue}" />
	        </p>
			<#--TODO: With ISF changes, add middle name input-->

			<p>
	            <label for="middleName">${i18n().middle_name} </label>
	            <input size="30"  type="text" id="middleName" name="middleName" value="${middleNameValue}" />
	        </p>

	        <p>
	            <label for="lastName">${i18n().last_name} ${requiredHint}</label>
	            <input size="30"  type="text" id="lastName" name="lastName" value="${lastNameValue}" />
	        </p>
	    <#else>
	        <p>
	            <label for="name">${i18n().name_capitalized} ${requiredHint}</label>
	            <input size="30"  type="text" id="label" name="label" value="${labelValue}" />
	        </p>
	    </#if>

 			<input type="hidden" name="editKey" id="editKey" value="${editKey}"/>

        	<input type="submit" class="submit" id="submit" value="${i18n().save_button}" role="button" role="input" />
			${i18n().or}
			<a href="${urls.referringPage}" class="cancel" title="${i18n().cancel_title}" >${i18n().cancel_link}</a>

            <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>
            </form>
