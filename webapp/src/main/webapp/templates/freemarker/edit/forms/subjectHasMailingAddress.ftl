<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#if editConfiguration.objectUri?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#--Retrieve variables needed-->
<#assign streetAddressValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "streetAddress") />
<#assign localityValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "locality") />
<#assign regionValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "region") />
<#assign postalCodeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "postalCode") />
<#assign countryValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "country") />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit">    
        <#assign titleVerb="${i18n().edit_capitalized}">        
        <#assign submitButtonText="${i18n().save_changes}">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="${i18n().create_capitalized}">        
        <#assign submitButtonText="${i18n().create_mailing_address}">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />

<h2>${titleVerb}&nbsp;${i18n().mailing_address_for} ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="${i18n().error_alert_icon}" />
        <p>
        <#--Checking if any required fields are empty-->
         <#if lvf.submissionErrorExists(editSubmission, "country")>
 	        ${i18n().enter_a_country}<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "streetAddress")>
 	        ${i18n().enter_street_address}<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "locality")>
 	        ${i18n().enter_a_locality}<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "postalCode")>
 	        ${i18n().enter_postal_code}
        </#if>
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="personHasMailingAddress" role="region">        
    
    <form id="personHasMailingAddress" class="customForm noIE67" action="${submitUrl}"  role="add/edit mailing address">

        <p>
            <label for="streetAddress">${i18n().street_address} 1 ${requiredHint}</label>
            <input  size="40"  type="text" id="streetAddressOne" name="streetAddressOne" value="" />
        </p>
    
        <p>
            <label for="streetAddress">${i18n().street_address} 2</label>
            <input  size="40"  type="text" id="streetAddressTwo" name="streetAddressTwo" value="" />
            <input  type="hidden" id="streetAddress" name="streetAddress" value="${streetAddressValue}" />
        </p>

        <p>
            <label for="locality">${i18n().city_locality} ${requiredHint}</label>
            <input  size="40"  type="text" id="city" name="locality" value="${localityValue}" />
        </p>

        <p>
            <label for="region" id="stateLabel">${i18n().region}</label>
            <input  size="40"  type="text" id="state" name="region" value="${regionValue}" />
        </p>

        <p>
            <label for="postalCode" id="postalCodeLabel">${i18n().postal_code} ${requiredHint}</label>
            <input  size="20"  type="text" id="postalCode" name="postalCode" value="${postalCodeValue}" />
        </p>

        <p>    
            <label for="country" style="margin-bottom:-4px">${i18n().country} ${requiredHint}</label>
            <input  size="20"  type="text"  id="countryEditMode" name="country" value="${countryValue}" />
        </p>
    
        <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

        <p class="submit">
            <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> ${i18n().or} </span>
            <a class="cancel" href="${cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
        </p>

        <p id="requiredLegend" class="requiredHint">* ${i18n().required_fields}</p>

    </form>

</section>

<script type="text/javascript">
 $(document).ready(function(){
    mailingAddressUtils.onLoad('${editMode}');
}); 
</script>
 
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/mailingAddressUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>')}


