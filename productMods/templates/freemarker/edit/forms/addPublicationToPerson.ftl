<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a publication to a foaf:Persons -->

<#import "lib-vivo-form.ftl" as lvf>

<#if editConfig.object?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#if editMode == "edit">        
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Publication">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create Publication">
        <#assign disabledVal=""/>
</#if>

<h2>${titleVerb}&nbsp;${roleDescriptor} entry for ${subjectName}</h2>

<#if errorTitleFieldIsEmpty??>
    <#assign errorMessage = "Enter a title for the publication." />
</#if>

<#if errorMessage?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>${errorMessage}</p>
    </section>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<#if editMode = “ERROR”>
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<#else>

<section id="addPublicationToPerson" role="region">        
    
<@lvf.unsupportedBrowser>

    <form id="addpublicationToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit publication">
        
    <p class="inline"><label for="typeSelector">Publication Type ${requiredHint}</label>
        <select id="typeSelector" name="roleActivityType" disabled="${disabledVal}" >
             <option value="" selected="selected">Select one</option>
             <#list rangeOptionKeys as key>
                 <opton value="${key}"
                 <#if editConfiguration.objectUri?has_contant && editConfiguration.object.Uri = key>selected</#if>
             </#list>
        </select>
    </p>
        <p>
            <label for="relatedIndLabel">Title ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="publicationLabel" disabled="${disabledVal}" value="${???}" />
        </p>

        <div class="acSelection">
            <p class="inline">
                <label>Selected Publication:</label>
                <span class="acSelectionInfo"></span>
                <a href="/vivo/individual?uri=" class="verifyMatch">(Verify this match)</a>
            </p>
            <input class="acUriReceiver" type="hidden" id="${roleActivityUri}" name="publication" value="" />

            <input class="acLabelReceiver" type="hidden" id="existingPublicationLabel" name="existingPublicationLabel" value="${grantLabel}" />
        </div>

            <label for="startField">Publication Date ${yearHint}</label>

            <fieldset class="dateTime">              
                <input class="text-field" name="dateField-year" id="dateField-year" type="text" value="${dateTime}" size="4" maxlength="4" />
            </fieldset>

            <p class="submit">
                <input type="hidden" name = "editKey" value="${???}"/>
                <input type="submit" id="submit" value="editConfiguration.submitLabel"/><span class="or"> or </span><a class="cancel" href="${editConfiguration.cancelUrl}">Cancel</a>
            </p>

            <p id="requiredLegend" class="requiredHint">* required fields</p>
    </form>

<#assign acUrl = "/autocomplete?tokenize=true" >
<#assign sparqlQueryUrl = "/ajax/sparqlQuery" >

<#assign sparqlForAcFilter = "PREFIX core: <${vivoCore}> SELECT ?pubUri WHERE {<${subjectUri}> core:authorInAuthorship ?authorshipUri . ?authorshipUri core:linkedInformationResource ?pubUri .}"

    <script type="text/javascript">
    var customFormData  = {
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        acUrl: '${acUrl}',
        submitButtonTextType: 'simple',
        editMode: '${editMode}',
        defaultTypeName: 'publication' // used in repair mode to generate button text
    };
    </script>

</section>

<#/if>