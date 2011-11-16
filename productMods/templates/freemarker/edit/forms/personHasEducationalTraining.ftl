<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#assign subjectName=""/>
<#assign roleActivityUri="mysteryRoleActivityURI"/>
<#assign orgLabel="mysteryOrgLabel"/>

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#--Retrieve variables needed-->
<#assign orgTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgType")/>
<#assign orgLabelValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "orgLabel") />
<#assign deptValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "dept") />
<#assign infoValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "info") />
<#assign majorFieldValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "majorField") />
<#assign degreeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "degree") />

<#if editMode == "edit">    
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Educational Training">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Educational Training">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />


<#--
<#if editMode = “ERROR”>
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<#else>
-->

<h2>${titleVerb}&nbsp;educational training entry for ${subjectName}${editConfiguration.subjectName}</h2>

<#--
<#if errorTypeFieldIsEmpty??>
    <#assign errorMessage = "Select a type of organization." />
</#if>

<#if errorNameFieldIsEmpty??>
    <#assign errorMessage = "Enter a name for the organization." />
</#if>
-->

<#if errorMessage?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>${errorMessage}</p>
    </section>
</#if>

<section id="personHasEducationalTraining" role="region">        
    
    <form id="personHasEducationalTraining" class="customForm noIE67" action="${submitUrl}"  role="add/edit educational training">

    
    <p class="inline">    
        <label for="orgType">Organization Type ${requiredHint}</label>
        <#assign orgTypeOpts = editConfiguration.pageData.orgType />
        <select id="typeSelector" name="orgType"  >
            <option value="" selected="selected">Select one</option>                
            <#list orgTypeOpts?keys as key>             
                <#if orgTypeValue = key>
                    <option value="${key}"  selected >${orgTypeOpts[key]}</option>     
                <#else>
                    <option value="${key}">${orgTypeOpts[key]}</option>
                </#if>             
            </#list>
        </select>   
    </p>     
    
    <p>
        <label for="relatedIndLabel">### Name ${requiredHint}</label>
        <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="orgLabel" value="${orgLabelValue}" />
    </p>
    
    <#--Store values in hidden fields-->
    <#if editMode="edit">
    	<input type="hidden" name="orgType" id="orgType" value="${orgTypeValue}"/>
    	<input type="hidden" name="orgLabel" id="orgLabel" value="${orgLabelValue}"/>
    </#if>
    
    <div class="acSelection">
        <p class="inline">
            <label>Selected Organization:</label>
            <span class="acSelectionInfo"></span>
            <a href="/vivo/individual?uri=" class="verifyMatch">(Verify this match)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="org" name="org" value="" /> <!--Field populated by javascript-->

        <input class="acLabelReceiver" type="hidden" id="existingOrgLabel" name="existingOrgLabel" value="" />
    </div>
    
    <p>
        <label for="dept">Department or School Name within the ###</label>
        <input  size="50"  type="text" id="dept" name="dept" value="${deptValue}" />
    </p>
    
    <div class="entry">
      <label for="degreeUri">Degree</label>      
    
      <#assign degreeOpts = editConfiguration.pageData.degree />  
      <select name="degree" id="degreeUri" >
        <option value="" <#if degreeValue = "">selected</#if>>Select one</option>        
       
        <#list degreeOpts?keys as key>                 
        <option value="${key}" <#if degreeValue = key>selected</#if>>${degreeOpts[key]}</option>                    
        </#list>                                
      </select>    
    </div>
    
    <p>    
        <label for="majorField">Major Field of Degree</label>
        <input type="text" id="majorField" name="majorField" size="30" value="${majorFieldValue}"/>   
    </p>   
          
    <p>    
        <label for="info">Supplemental Information</label>
        <input  size="50"  type="text" id="info" name="info" value="${infoValue}" />
        <br />e.g., <em>Postdoctoral training</em> or <em>Transferred</em>    
    </p>
    
    <#--Need to draw edit elements for dates here-->
     <#if htmlForElements?keys?seq_contains("startField")>
							<label for="startField">Start Year ${yearHint}</label>
							${htmlForElements["startField"]}
     </#if>
     <#if htmlForElements?keys?seq_contains("endField")>
		 							<label for="endField">Start Year ${yearHint}</label>
		 							${htmlForElements["startField"]}
     </#if>
                                    
  	<#--End draw elements-->
    
    <input type="hidden" id="editKey" name="editKey" value="${editKey}"/>
    <p class="submit">
        <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> or </span>
        <a class="cancel" href="${cancelUrl}">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

</form>


<script type="text/javascript">
var customFormData  = {
    acUrl: '${urls.base}/autocomplete?tokenize=true&stem=true',
    editMode: '${editMode}',
    submitButtonTextType: 'compound',
    defaultTypeName: 'organization'
};
</script>

</section>
 
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customFormWithAutocomplete.css" />')}


${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/extensions/String.js"></script>',
             '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.bgiframe.pack.js"></script>',
             '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}


