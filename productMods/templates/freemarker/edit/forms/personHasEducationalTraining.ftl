<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this is in request.subject.name -->

<#-- leaving this edit/add mode code in for reference in case we decide we need it -->

<#import "lib-vivo-form.ftl" as lvf>

<#assign subjectName=""/>
<#assign roleActivityUri="mysteryRoleActivityURI"/>
<#assign orgLabel="mysteryOrgLabel"/>

<#if editConfiguration.object?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

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
                <#if editConfiguration.objectUri?has_content && editConfiguration.objectUri = key>
                    <option value="${key}"  selected >${orgTypeOpts[key]}</option>     
                <#else>
                    <option value="${key}">${orgTypeOpts[key]}</option>
                </#if>             
            </#list>
        </select>   
    </p>     
    
    <p>
        <label for="relatedIndLabel">### Name ${requiredHint}</label>
        <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="orgLabel" value="" />
    </p>
    
    <div class="acSelection">
        <p class="inline">
            <label>Selected Organization:</label>
            <span class="acSelectionInfo"></span>
            <a href="/vivo/individual?uri=" class="verifyMatch">(Verify this match)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="${roleActivityUri}" name="org" value="" />

        <input class="acLabelReceiver" type="hidden" id="existingOrgLabel" name="existingOrgLabel" value="${orgLabel}" />
    </div>
    
    <p>
        <label for="dept">Department or School Name within the ###</label>
        <input  size="50"  type="text" id="dept" name="dept" value="" />
    </p>
    
    <div class="entry">
      <label for="degreeUri">Degree</label>      
    
      <#assign degreeOpts = editConfiguration.pageData.degree />  
      <select name="degree" id="degreeUri" >
        <option value="" selected="selected">Select one</option>        
       
        <#list degreeOpts?keys as key>                 
        <option value="${key}">${degreeOpts[key]}</option>                    
        </#list>                                
      </select>    
    </div>
    
    <p>    
        <label for="majorField">Major Field of Degree</label>
        <input type="text" id="majorField" name="majorField" size="30" value=""/>   
    </p>   
          
    <p>    
        <label for="info">Supplemental Information</label>
        <input  size="50"  type="text" id="info" name="info" value="" />
        <br />e.g., <em>Postdoctoral training</em> or <em>Transferred</em>    
    </p>
                                    
    <label for="startField">Start Year ${yearHint}</label>
    <fieldset class="dateTime">              
        <input class="text-field" name="startField-year" id="startField-year" type="text" value="" size="4" maxlength="4" />
    </fieldset>

    <label for="endField">End Year ${yearHint}</label>
    <fieldset class="dateTime">              
        <input class="text-field" name="endField-year" id="endField-year" type="text" value="" size="4" maxlength="4" />
    </fieldset>
    
    <input type="hidden" id="editKey" name="editKey" value="${editConfiguration.editKey}"/>
    <p class="submit">
        <input type="submit" id="submit" value="editConfiguration.submitLabel"/><span class="or"> or </span>
        <a class="cancel" href="${editConfiguration.cancelUrl}">Cancel</a>
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


