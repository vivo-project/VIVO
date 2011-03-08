<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this is in request.subject.name -->

<#assign subjectName = "Bogus, Al">

<#if editConfig.object?has_content>
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
<#assign requiredHint="<span class='requiredHint'> *</span>"/>

<div class="form">
<h2>${titleVerb}&nbsp;educational training entry for ${subjectName}</h2>

<form class="${editMode}" action="${urls.base}/${editConfig.submitToUrl}" >

    <div class="entry">
      <label for="degreeUri">Degree</label>      
      <select name="degreeUri" id="degreeUri" >
        <option value="" selected="selected">Select one</option>
        <@widget name="SelectList" fieldName="degree" />
      </select>    
    </div>
        
    <label for="majorField">Major Field of Degree ${requiredHint}</label>
    <input type="text" id="majorField" name="majorField" size="30" />   
       
    <@widget name="editElement" name="dateTime" />  
        
    <div class="relatedIndividual">
        <div class="existing">
          <label for="org">Organization Granting Degree ${requiredHint}</label>
          <select id="org" name="org">
            <@widget name="SelectList" fieldName="org" /> 
          </select>
          <span class="existingOrNew">or</span>
        </div>
        <div class="addNewLink">
              If your organization is not listed, please <a href="#">add a new organization</a>.    
        </div>
      
        <div class="new">            
            <h6>Add a New Organization</h6>
            <label for="newOrgName">Organization Name <span class='requiredHint'> *</span></label>
            <input  size="30"  type="text" id="newOrgName" name="newOrgName" value="" />
            
            <label for="newOrgType">Select Organization Type <span class='requiredHint'> *</span></label>
            <select   id="newOrgType" name="newOrgType">    
                <option value="" selected="selected">Select one</option>
                <@widget name="SelectList" fieldName="orgType" />                
            </select>   
        </div> 
    </div>
        
        
    <div class="entry"> 
        <label for="dept">Department or School Name within the Organization</label>
        <input  size="50"  type="text" id="dept" name="dept" value="" />
    
        <label for="info">Supplemental Information</label>
        <input  size="50"  type="text" id="info" name="info" value="" />
        <p>e.g., <em>Postdoctoral training</em> or <em>Transferred</em></p>    
    </div>
                                    
            
    <p class="submit">
        <input name="editKey" type="hidden" value="${editConfig.editKey}" />
        <input type="submit" id="submit" value="${submitButtonText}"/>
        
        <#assign cancelParams = "editKey=${editConfig.editKey}&cancel=true" >
        <span class="or">or</span><a class="cancel" href="${urls.base}/edit/postEditCleanUp.jsp?${cancelParams?url}" title="Cancel">Cancel</a>
    </p>
     
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<#assign acUrl="/autocomplete?tokenize=true&stem=true" >

<script type="text/javascript">
var customFormData  = {
    acUrl: '${acUrl?url}',
    editMode: '${editMode}',
    submitButtonTextType: 'compound',
    defaultTypeName: 'organization'
};
</script>

</div>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/edit/forms/css/personHasEducationalTraining.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}   

              

 