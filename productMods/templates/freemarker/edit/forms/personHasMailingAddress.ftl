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
<#assign addrLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLabel") />
<#assign addressTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addressType") />
<#assign addrLineOneValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLineOne") />
<#assign addrLineTwoValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLineTwo") />
<#assign addrLineThreeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "addrLineThree") />
<#assign cityValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "city") />
<#assign stateValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "state") />
<#assign postalCodeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "postalCode") />
<#assign countryValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "country") />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#if editMode == "edit">    
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Mailing Address">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create Mailing Address">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />

<h2>${titleVerb}&nbsp;mailing address for ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--Checking if any required fields are empty-->
         <#if lvf.submissionErrorExists(editSubmission, "country")>
 	        Please select a country.<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "addrLineOne")>
 	        Please enter a value in the Address Line 1 field.<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "city")>
 	        Please enter a value in the City field.<br />
        </#if>
         <#if lvf.submissionErrorExists(editSubmission, "postalCode")>
 	        Please enter a value in the Postal Code field.
        </#if>
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base /> 

<section id="personHasMailingAddress" role="region">        
    
    <form id="personHasMailingAddress" class="customForm noIE67" action="${submitUrl}"  role="add/edit educational training">

    
    <p>    
      <label for="country" style="margin-bottom:-4px">Country ${requiredHint}</label>
      <#assign countryOpts = editConfiguration.pageData.country />
      <select id="country" name="country" >
          <#list countryOpts?keys as key>  
              <#assign countryName = countryOpts[key]?uncap_first?replace("the ", "")?cap_first >
              <option value="${countryName}" <#if countryName == countryValue>selected</#if>>
                ${countryName} 
              </option>            
          </#list>
      </select>
    </p>

    <input type="hidden" id="countryEditMode" name="countryEditMode" value="${countryValue}" />
    
  <div id="addressDetails" >
    <p>
        <label for="addrLineOne">Street Address 1 ${requiredHint}</label>
        <input  size="50"  type="text" id="addrLineOne" name="addrLineOne" value="${addrLineOneValue}" />
    </p>
    
    <p>
        <label for="addrLineTwo">Street Address 2</label>
        <input  size="50"  type="text" id="addrLineTwo" name="addrLineTwo" value="${addrLineTwoValue}" />
    </p>
                                    
    <p>
        <label for="addrLineThree">Street Address 3</label>
        <input  size="50"  type="text" id="addrLineThree" name="addrLineThree" value="${addrLineThreeValue}" />
    </p>

    <p>
        <label for="city">City ${requiredHint}</label>
        <input  size="40"  type="text" id="city" name="city" value="${cityValue}" />
    </p>

    <p>
        <label for="state" id="stateLabel">State</label>
        <input  size="40"  type="text" id="state" name="state" value="${stateValue}" />
        <select id="stateSelect" name="stateSelect">
            <option value="" <#if editMode == "add">selected</#if> >Select one</option>
            <option value="Alabama" <#if stateValue == "Alabama" || stateValue == "AL" >selected</#if> >Alabama</option>
            <option value="Alaska" <#if stateValue == "Alaska" || stateValue == "AK" >selected</#if> >Alaska</option>
            <option value="Arizona" <#if stateValue == "Arizona " || stateValue == "AZ" >selected</#if>>Arizona</option>
            <option value="Arkansas" <#if stateValue == "Arkansas " || stateValue == "AR" >selected</#if>>Arkansas</option>
            <option value="California" <#if stateValue == "California " || stateValue == "CA" >selected</#if>>California</option>
            <option value="Colorado" <#if stateValue == "Colorado " || stateValue == "CO" >selected</#if>>Colorado</option>
            <option value="Connecticut" <#if stateValue == "Connecticut " || stateValue == "CT" >selected</#if>>Connecticut</option>
            <option value="Delaware" <#if stateValue == "Delaware " || stateValue == "DE" >selected</#if>>Delaware</option>
            <option value="Florida" <#if stateValue == "Florida " || stateValue == "FA" >selected</#if>>Florida</option>
            <option value="Georgia" <#if stateValue == "Georgia " || stateValue == "GA" >selected</#if>>Georgia</option>
            <option value="Hawaii" <#if stateValue == "Hawaii" || stateValue == "HI" >selected</#if>>Hawaii</option>
            <option value="Idaho" <#if stateValue == "Idaho " || stateValue == "ID" >selected</#if>>Idaho</option>
            <option value="Illinois" <#if stateValue == "Illinois " || stateValue == "IL" >selected</#if>>Illinois</option>
            <option value="Indiana" <#if stateValue == "Indiana " || stateValue == "IN" >selected</#if>>Indiana</option>
            <option value="Iowa" <#if stateValue == "Iowa " || stateValue == "IA" >selected</#if>>Iowa</option>
            <option value="Kansas" <#if stateValue == "Kansas" || stateValue == "KS" >selected</#if>> Kansas</option>
            <option value="Kentucky" <#if stateValue == "Kentucky" || stateValue == "KY" >selected</#if>>Kentucky</option>
            <option value="Louisiana" <#if stateValue == "Louisiana" || stateValue == "LA" >selected</#if>>Louisiana</option>
            <option value="Maine" <#if stateValue == "Maine" || stateValue == "ME" >selected</#if>>Maine</option>
            <option value="Maryland" <#if stateValue == "Maryland" || stateValue == "MD" >selected</#if>>Maryland</option>
            <option value="Massachusetts" <#if stateValue == "Massachusetts" || stateValue == "MA" >selected</#if>>Massachusetts</option>
            <option value="Michigan" <#if stateValue == "Michigan" || stateValue == "MI" >selected</#if>>Michigan</option>
            <option value="Minnesota" <#if stateValue == "Minnesota" || stateValue == "MN" >selected</#if>>Minnesota</option>
            <option value="Mississippi" <#if stateValue == "Mississippi" || stateValue == "MS" >selected</#if>>Mississippi</option>
            <option value="Missouri" <#if stateValue == "Missouri" || stateValue == "MO" >selected</#if>>Missouri</option>
            <option value="Montana" <#if stateValue == "Montana" || stateValue == "MT" >selected</#if>>Montana</option>
            <option value="Nebraska" <#if stateValue == "Nebraska" || stateValue == "NE" >selected</#if>>Nebraska</option>
            <option value="Nevada" <#if stateValue == "Nevada" || stateValue == "NV" >selected</#if>>Nevada</option>
            <option value="New Hampshire" <#if stateValue == "New Hampshire" || stateValue == "NH" >selected</#if>>New Hampshire</option>
            <option value="New Jersey" <#if stateValue == "New Jersey" || stateValue == "NJ" >selected</#if>>New Jersey</option>
            <option value="New Mexico" <#if stateValue == "New Mexico" || stateValue == "NM" >selected</#if>>New Mexico</option>
            <option value="New York" <#if stateValue == "New York" || stateValue == "NY" >selected</#if>>New York</option>
            <option value="North Carolina<" <#if stateValue == "North Carolina" || stateValue == "NC" >selected</#if>>North Carolina</option>
            <option value="North Dakota" <#if stateValue == "North Dakota" || stateValue == "ND" >selected</#if>>North Dakota</option>
            <option value="Ohio" <#if stateValue == "Ohio" || stateValue == "OH" >selected</#if>>Ohio</option>
            <option value="Oklahoma" <#if stateValue == "Oklahoma" || stateValue == "OK" >selected</#if>>Oklahoma</option>
            <option value="Oregon" <#if stateValue == "Oregon" || stateValue == "OR" >selected</#if>>Oregon</option>
            <option value="Pennsylvania" <#if stateValue == "Pennsylvania" || stateValue == "PA" >selected</#if>>Pennsylvania</option>
            <option value="Rhode Island" <#if stateValue == "Rhode Island" || stateValue == "RI" >selected</#if>>Rhode Island</option>
            <option value="South Carolina" <#if stateValue == "South Carolina" || stateValue == "SC" >selected</#if>>South Carolina</option>
            <option value="South Dakota" <#if stateValue == "South Dakota" || stateValue == "SD" >selected</#if>>South Dakota</option>
            <option value="Tennessee" <#if stateValue == "Tennessee" || stateValue == "TN" >selected</#if>>Tennessee</option>
            <option value="Texas" <#if stateValue == "Texas" || stateValue == "TX" >selected</#if>>Texas</option>
            <option value="Utah" <#if stateValue == "Utah" || stateValue == "UT" >selected</#if>>Utah</option>
            <option value="Vermont" <#if stateValue == "Vermont" || stateValue == "VT" >selected</#if>>Vermont</option>
            <option value="Virginia" <#if stateValue == "Virginia" || stateValue == "VA" >selected</#if>>Virginia</option>
            <option value="Washington" <#if stateValue == "Washington" || stateValue == "WA" >selected</#if>>Washington</option>
            <option value="West Virginia" <#if stateValue == "West Virginia" || stateValue == "WV" >selected</#if>>West Virginia</option>
            <option value="Wisconsin" <#if stateValue == "Wisconsin" || stateValue == "WI" >selected</#if>>Wisconsin</option>
            <option value="Wyoming" <#if stateValue == "Wyoming" || stateValue == "WY" >selected</#if>>Wyoming</option>
        </select>
    </p>

    <p>
        <label for="postalCode" id="postalCodeLabel">Postal Code ${requiredHint}</label>
        <input  size="8"  type="text" id="postalCode" name="postalCode" value="${postalCodeValue}" />
    </p>

  </div>

    <input type="hidden" id="addrLabel" name="addrLabel" value="${addrLabelValue}" />
    <input type="hidden" id="addressType" name="addressType" value="${addressTypeValue}" />
    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>

    <p class="submit">
        <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> or </span>
        <a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

</form>

</section>


<script type="text/javascript">
$(document).ready(function(){
    mailingAddressUtils.onLoad("${editMode}","${countryValue}");
});
</script>

 
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/mailingAddressUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>')}


