<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign literalValues = editConfiguration.existingLiteralValues />
<#assign uriValues = editConfiguration.existingUriValues />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />
<#assign showRoleLabelField = editConfiguration.pageData.showRoleLabelField />
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>
<#--Freemarker variables with default values that can be overridden by specific forms-->

<#assign blankSentinel = "" />
<#if editConfigurationConstants?has_content && editConfigurationConstants?keys?seq_contains("BLANK_SENTINEL")>
	<#assign blankSentinel = editConfigurationConstants["BLANK_SENTINEL"] />
</#if>

<#--This flag is for clearing the label field on submission for an existing object being selected from autocomplete.
Set this flag on the input acUriReceiver where you would like this behavior to occur. -->
<#assign flagClearLabelForExisting = "flagClearLabelForExisting" />


<#-- typeSelectorLabel, numDateFields,  roleExamples, roleActivityVClass -->

<#if !typeSelectorLabel?has_content>
	<#assign typeSelectorLabel = roleDescriptor />
</#if>
<#if !numDateFields?has_content>
	<#assign numDateFields = 2 />
</#if>

<#if !roleExamples?has_content>
	<#assign roleExamples = "" />
</#if>

<#if !roleActivityVClass?has_content>
	<#assign roleActivityVClass = "" />
</#if>

<#--Setting values for titleVerb, submitButonText, and disabled Value-->
<#if editConfiguration.objectUri?has_content>
	<#assign titleVerb = "${i18n().edit_capitalized}"/>
	<#assign title= editTitle/>
	<#assign submitButtonText>${i18n().save_changes}</#assign>
	<#if editMode = "repair">
		<#assign disabledVal = ""/>
	<#else>
		<#assign disabledVal = "disabled"/>
	</#if>
<#else>
	<#assign title= createTitle/>
	<#assign titleVerb = "${i18n().create_capitalized}"/>
	<#assign submitButtonText>${i18n().create_entry}</#assign>
	<#assign disabledVal = ""/>
	<#assign editMode = "add" />
</#if>

<#--Get existing value for specific data literals and uris-->


<#--Get selected activity type value if it exists, this is alternative to below-->
<#assign activityTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "roleActivityType")/>

 <#--Get activity label value-->
<#assign activityLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "activityLabel") />
<#assign activityLabelDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "activityLabelDisplay") />


<#--Get role label-->
<#assign roleLabel = lvf.getFormFieldValue(editSubmission, editConfiguration, "roleLabel") />

<#--For role activity uri-->
<#assign existingRoleActivityValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "existingRoleActivity") />


<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(${i18n().year_hint_format})</span>" />


<h2>${editConfiguration.subjectName}${title} </h2>
<#--Display error messages if any-->
<#if activityLabelDisplayValue?has_content >
    <#assign activityLabelValue = activityLabelDisplayValue />
</#if>


<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        	<#if errorFieldName == "startField">
        	    <#if submissionErrors[errorFieldName]?contains("before")>
        	        ${i18n().start_year_must_precede_end}
        	    <#else>
        	        ${submissionErrors[errorFieldName]}
        	    </#if>
        	    <br />
        	<#elseif errorFieldName == "endField">
    	        <#if submissionErrors[errorFieldName]?contains("after")>
    	            ${i18n().end_year_must_be_later}
    	        <#else>
    	            ${submissionErrors[errorFieldName]}
    	        </#if>
	        </#if>
        </#list>
        <#--Checking if Type, Name and Role fields are empty-->
         <#if lvf.submissionErrorExists(editSubmission, "roleActivityType")>
 	        ${i18n().please_select_type}<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "activityLabel")>
 	        ${i18n().select_or_enter_name}<br />
        </#if>
        <#--Checking if role field is empty-->
        <#if lvf.submissionErrorExists(editSubmission, "roleLabel")>
    	    ${i18n().specify_role_for_activity}
        </#if>

        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base />

<section id="add${roleDescriptor?cap_first}RoleToPersonTwoStage" role="region">

    <form id="add${roleDescriptor?cap_first}RoleToPersonTwoStage" class="customForm noIE67" action="${submitUrl}"  role="add/edit grant role">

            <#if showRoleLabelField = true>
            <p><label for="roleLabel" class="inline">${i18n().user_role} ${roleExamples}</label>
                <input  size="50"  type="text" id="roleLabel" name="roleLabel" value="${roleLabel}" />
            </p>
            </#if>

      <h3>${genericLabel?cap_first}</h3>

       <p>
        <label for="typeSelector" class="inline">${i18n().type_capitalized?cap_first}<#if editMode != "edit"> ${requiredHint}<#else>:</#if></label>
        <#--Code below allows for selection of first 'select one' option if no activity type selected-->
        <#if activityTypeValue?has_content>
        	<#assign selectedActivityType = activityTypeValue />
        <#else>
        	<#assign selectedActivityType = "" />
        </#if>
       		<#assign roleActivityTypeSelect = editConfiguration.pageData.roleActivityType />
       		<#assign roleActivityTypeKeys = roleActivityTypeSelect?keys />

        <#if editMode != "edit" || ( editMode == "edit" && roleActivityVClass == "organizations") >
            <select id="typeSelector" name="roleActivityType" acGroupName="activity">
                <#list roleActivityTypeKeys as key>
                    <option value="${key}"<#if selectedActivityType = key>selected</#if>>${roleActivityTypeSelect[key]}</option>
                </#list>
            </select>
        <#else>
           <#list roleActivityTypeKeys as key>
               <#if selectedActivityType = key >
                 <span class="readOnly" id="typeSelectorSpan">${roleActivityTypeSelect[key]}</span>
                 <input type="hidden" id="typeSelectorInput" name="roleActivityType" acGroupName="activity" value="${activityTypeValue}" >
               </#if>
           </#list>
        </#if>
       </p>


<#--   <div class="fullViewOnly"> -->
<#--   UQAM
		Adaptation of structure for french 
 -->
            <p>
                <label for="activity" class="inline">${i18n().name?cap_first} ${requiredHint}</label>
                <input class="acSelector" size="50"  type="text" id="activity" name="activityLabel"  acGroupName="activity" value="${activityLabelValue}" />
                <input class="display" type="hidden" id="activityDisplay" acGroupName="activity" name="activityLabelDisplay" value="${activityLabelDisplayValue}">
            </p>

            <input type="hidden" id="roleToActivityPredicate" name="roleToActivityPredicate" value="" />
            <!--Populated or modified by JavaScript based on type of activity, type returned from AJAX request-->

            <div class="acSelection" acGroupName="activity">
                <p class="inline">
                    <label>${i18n().selected}:</label>
                    <span class="acSelectionInfo"></span>
                    <a href="/vivo/individual?uri=" class="verifyMatch" title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized}</a> ${i18n().or}
                    <a href="#" class="changeSelection" id="changeSelection">${i18n().change_selection})</a>

                    </p>
                    <input class="acUriReceiver" type="hidden" id="roleActivityUri" name="existingRoleActivity"  value="${existingRoleActivityValue}" ${flagClearLabelForExisting}="true" />
                    <!-- Field value populated by JavaScript -->
            </div>

            <#if numDateFields == 1 >
               <#--Generated html is a map with key name mapping to html string-->
               <#if htmlForElements?keys?seq_contains("startField")>
                	<label class="dateTimeLabel" for="startField">${i18n().start_year}</label>
               		${htmlForElements["startField"]} ${yearHint}
               </#if>
            <#else>
                <h3 class="label">${i18n().years_participating} </h3>
                <#if htmlForElements?keys?seq_contains("startField")>
                	    <label class="dateTime" for="startField">${i18n().start_year}</label>
               		    ${htmlForElements["startField"]} ${yearHint}
               </#if>
               <p></p>
               <#if htmlForElements?keys?seq_contains("endField")>
               		    <label class="dateTime" for="endField">${i18n().end_year}</label>
               		    ${htmlForElements["endField"]} ${yearHint}
               </#if>
            </#if>
<#--        </div> -->
        <p class="submit">
            <input type="hidden" id="editKey" name="editKey" value="${editKey}" />
            <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> ${i18n().or} </span><a class="cancel" href="${cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
        </p>

        <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>
    </form>

<#--Specifying form-specific script and adding stylesheets and scripts-->

 <script type="text/javascript">
	var customFormData  = {
	    acUrl: '${urls.base}/autocomplete?tokenize=true',
	    editMode: '${editMode}',
	    acTypes: ${acTypes!},
	    <#if acMultipleTypes??>acMultipleTypes: ${acMultipleTypes!},</#if>
	    // used in repair mode: button text and org name label
	    defaultTypeName: <#if genericLabel??>'${genericLabel?js_string}'<#else>'activity'</#if>,
	    baseHref: '${urls.base}/individual?uri=',
        blankSentinel: '${blankSentinel}',
        flagClearLabelForExisting: '${flagClearLabelForExisting}'
	};
	var i18nStrings = {
        selectAnExisting: "${i18n().select_an_existing?js_string}",
        orCreateNewOne: "${i18n().or_create_new_one?js_string}",
        selectedString: "${i18n().selected?js_string}"
    };
    </script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.12.1.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.12.1.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}

</section>
