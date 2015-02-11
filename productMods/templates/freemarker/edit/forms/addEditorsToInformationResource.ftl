<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom form for adding editors to information resources -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain page specific information information-->
<#assign newRank = editConfiguration.pageData.newRank />
<#assign existingEditorInfo = editConfiguration.pageData.existingEditorInfo />
<#assign rankPredicate = editConfiguration.pageData.rankPredicate />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#--Submission values for these fields may be returned if user did not fill out fields for new person-->
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName") />
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName") />
<#assign middleNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "middleName") />

<#--UL class based on size of existing editors-->
<#assign ulClass = ""/>
<#if (existingEditorInfo?size > 0)>
	<#assign ulClass = "class='dd'"/>
</#if>

<#assign title="<em>${editConfiguration.subjectName}</em>" />
<#assign requiredHint="<span class='requiredHint'> *</span>" />
<#assign initialHint="<span class='hint'>(${i18n().initial_okay})</span>" />

<@lvf.unsupportedBrowser urls.base/>

<h2>${title}</h2>

<#if submissionErrors?has_content>
    <section id="error-alert" role="alert" class="validationError">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        		  ${submissionErrors[errorFieldName]} <br/>
        </#list>
        
        </p>
    </section>
</#if>

<h3>${i18n().manage_editors}</h3>

<ul id="dragDropList" ${ulClass}>

<script type="text/javascript">
    var editorshipData = [];
</script>


<#assign editorHref="/individual?uri=" />
<#--This should be a list of java objects where URI and name can be retrieved-->
<#list existingEditorInfo as editorship>
	<#assign editorUri = editorship.editorUri/>
	<#assign editorName = editorship.editorName/>

	<li class="editorship">
			<#-- span.editor will be used in the next phase, when we display a message that the editor has been
			removed. That text will replace the a.editorName, which will be removed. -->    
			<span class="editor">
					<#-- This span is here to assign a width to. We can't assign directly to the a.editorName,
					for the case when it's followed by an em tag - we want the width to apply to the whole thing. -->
					<span class="itemName">
							<#if (editorUri?length > 0)>
									<span class="editorName">${editorName}</span>
								<#else>      
									<span class="editorName">${editorship.editorshipName}</span><em> (${i18n().no_linked_editor})</em>
							</#if>
					</span>

					<a href="${urls.base}/edit/primitiveDelete" class="remove" title="${i18n().remove_editor_link}">${i18n().remove_capitalized}</a>
			</span>
	</li>

	<script type="text/javascript">
			editorshipData.push({
					"editorshipUri": "${editorship.editorshipUri}",
					"editorUri": "${editorUri}",
					"editorName": "${editorName}"                
			});
	</script>
</#list>
</ul>
<br />
<section id="showAddForm" role="region">
    <input type="hidden" name = "editKey" value="${editKey}" />
    <input type="submit" id="showAddFormButton" value="${i18n().add_editor}" role="button" />

    <span class="or"> ${i18n().or} </span>
    <a id="returnLink" class="cancel" href="${cancelUrl}&url=/individual" title="${i18n().cancel_title}">${i18n().return_to_publication}</a>
    <img id="indicatorOne" class="indicator hidden" alt="${i18n().processing_indicator}" src="${urls.base}/images/indicatorWhite.gif" />
</section> 

<form id="addEditorForm" action ="${submitUrl}" class="customForm noIE67">
    <h3>${i18n().add_an_editor}</h3>

    <section id="personFields" role="personContainer">
    		<#--These wrapper paragraph elements are important because javascript hides parent of these fields, since last name
    		should be visible even when first name/middle name are not, the parents should be separate for each field-->
    		<p class="inline">
        <label for="lastName">${i18n().last_name} <span class='requiredHint'> *</span></label>
        <input class="acSelector" size="35"  type="text" id="lastName" name="lastName" value="${lastNameValue}" role="input" />
        </p>
				
				<p class="inline">
        <label for="firstName">${i18n().first_name} ${requiredHint} ${initialHint}</label>
        <input  size="20"  type="text" id="firstName" name="firstName" value="${firstNameValue}"  role="input" />
        </p>
        
				<p class="inline">
				<label for="middleName">${i18n().middle_name} <span class='hint'>(${i18n().initial_okay})</span></label>
        <input  size="20"  type="text" id="middleName" name="middleName" value="${middleNameValue}"  role="input" />
        </p>
      
        <div id="selectedEditor" class="acSelection">
            <p class="inline">
                <label>${i18n().selected_editor}:&nbsp;</label>
                <span class="acSelectionInfo" id="selectedEditorName"></span>
                <a href="${urls.base}/individual?uri=" id="personLink" class="verifyMatch"  title="${i18n().verify_match_capitalized}">(${i18n().verify_match_capitalized})</a>
                <input type="hidden" id="personUri" name="personUri" value=""  role="input" /> <!-- Field value populated by JavaScript -->
            </p>
        </div>
    </section>

    <input type="hidden" id="label" name="label" value=""  role="input" />  <!-- Field value populated by JavaScript -->


        <input type="hidden" name="rank" id="rank" value="${newRank}" role="input" />
    
        <p class="submit">
            <input type="hidden" name = "editKey" value="${editKey}" role="input" />
            <input type="submit" id="submit" value="${i18n().add_editor}" role="button" role="input" />
            
            <span class="or"> ${i18n().or} </span>
            
            <a id="returnLink" class="cancel" href="${cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
            <img id="indicatorTwo" alt="${i18n().processing_indicator}" class="indicator hidden" src="${urls.base}/images/indicatorWhite.gif" />
        </p>

        <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>
</form>

<script type="text/javascript">
var customFormData = {
    rankPredicate: '${rankPredicate}',
    acUrl: '${urls.base}/autocomplete?type=',
    tokenize: '&tokenize=true',
    personUrl: 'http://xmlns.com/foaf/0.1/Person',
    reorderUrl: '${urls.base}/edit/reorder'
};
var i18nStrings = {
    editorNameWrapperTitle: '${i18n().drag_drop_reorder_editors}',
    reorderEditorsAlert: '${i18n().reordering_editors_failed}',
    removeEditorshipMessage: '${i18n().confirm_editor_removal}',
    removeEditorshipAlert: '${i18n().error_processing_editor_request}',
    editorTypeText: '${i18n().editor_capitalized}',
    helpTextSelect: '${i18n().select_an_existing}',
    helpTextAdd: '${i18n().or_add_new_one}'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />',
					'<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />',
					'<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/autocomplete.css" />',
					'<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/manageDragDropList.css" />')}


${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/addEditorsToInformationResource.js"></script>')}