<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- this template is for adding a person's position to an organization -->

<#import "lib-vivo-form.ftl" as lvf>

<#if editConfig.object?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#if editMode == "edit">        
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Position">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create Position">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<#if editMode = “ERROR”>
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<#else>

<h2>${titleVerb}&nbsp;educational training entry for ${subjectName}</h2>

<#if errorTitleFieldIsEmpty??>
    <#assign errorMessage = "Enter a position title." />
</#if>

<#if errorTypeFieldIsEmpty??>
    <#assign errorMessage = "Select a position type." />
</#if>

<#if errorMessage?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>${errorMessage}</p>
    </section>
</#if>

<section id="organizationHasPositionHistory" role="region">        
    
<@lvf.unsupportedBrowser>

    <form id="organizationHasPositionHistory" class="customForm noIE67" action="${submitUrl}"  role="add/edit position history">

    <p>
        <label for="positionTitle">Position Title ${requiredHint}</label>
        <input size="30" type="text" id="positionTitle" name="positionTitle" value="${positionTitle}" />
    </p>
    <label for="positionType">Position Type ${requiredHint}</label>
    <select id="positionType" name="positionType">
         <option value="" selected="selected">Select one</option>
         <#list rangeOptionKeys as key>
             <opton value="${key}"
             <#if editConfiguration.objectUri?has_content && editConfiguration.object.Uri = key>selected</#if>
         </#list>
    </select>
    <p>
        <label for="relatedIndLabel">Person</label>
        <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="orgLabel" value="${orgLabel}" />
    </p>

    <div class="acSelection">
        <p class="inline">
            <label>Selected Person:</label>
            <span class="acSelectionInfo"></span>
            <a href="/vivo/individual?uri=" class="verifyMatch">(Verify this match)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="${roleActivityUri}" name="org" value="" />

        <input class="acLabelReceiver" type="hidden" id="existingPersonLabel" name="existingPersonLabel" value="${personLabel}" />
    </div>
    <label for="startField">Start Year ${yearHint}</label>

    <fieldset class="dateTime" >              
        <input class="text-field" name="startField-year" id="startField-year" type="text" value="${startYear}" size="4" maxlength="4" />
    </fieldset>

    <label for="endField">End Year ${yearHint}</label>
    <fieldset class="dateTime">              
        <input class="text-field" name="endField-year" id="endField-year" type="text" value="${endYear}" size="4" maxlength="4" />
    </fieldset>

    <p class="submit">
        <input type="hidden" name = "editKey" value="${???}"/>
        <input type="submit" id="submit" value="editConfiguration.submitLabel"/><span class="or"> or <a class="cancel" href="${editConfiguration.cancelUrl}">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

</form>

<#assign acUrl="/autocomplete?tokenize=true&stem=true" >

<script type="text/javascript">
var customFormData  = {
    acUrl: '${acUrl}?url',
    editMode: '${editMode}',
    submitButtonTextType: 'compound',
    defaultTypeName: 'person'
};
</script>

</section>

</#if>