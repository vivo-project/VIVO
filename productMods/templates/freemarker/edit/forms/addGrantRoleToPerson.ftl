<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a grant role, such as principal investigator, to a foaf:Persons -->
<#import "lib-vivo-form.ftl" as lvf>

<#if editConfig.object?has_content>
    <#assign editMode = "edit">
<#else>
    <#assign editMode = "add">
</#if>

<#if editMode == "edit">        
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Grant">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create Grant">
        <#assign disabledVal=""/>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<#if editMode = “ERROR”>
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<#else>

<h2>${titleVerb}&nbsp;${roleDescriptor} entry for ${subjectName}</h2>

<#if errorNameFieldIsEmpty??>
    <#assign errorMessage = "Enter a name for the grant." />
</#if>

<#if errorMessage?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>${errorMessage}</p>
    </section>
</#if>

<section id="addGrantRoleToPerson" role="region">        
    
<@lvf.unsupportedBrowser>


    <form id="addGrantRoleToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit grant role">
        
        <p>
            <label for="relatedIndLabel">Grant Name ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="grantLabel" disabled="${disabledVal}" value="${grantLabel}" />
        </p>

        <#-- DO WE NEED THIS??
            Store this value in a hidden field, because the displayed field is disabled and doesn't submit. This ensures that when
            returning from a validation error, we retain the value. 
        <#if editMode == "edit">
            <input type="hidden" id="grantLabel" />
        </#if>
        -->
        <div class="acSelection">
            <p class="inline">
                <label>Selected Grant:</label>
                <span class="acSelectionInfo"></span>
                <a href="/vivo/individual?uri=" class="verifyMatch">(Verify this match)</a>
            </p>
            <input class="acUriReceiver" type="hidden" id="${roleActivityUri}" name="grant" value="" />

            <input class="acLabelReceiver" type="hidden" id="existingGrantLabel" name="existingGrantLabel" value="${grantLabel}" />
        </div>

        <h4>Years of Participation in Grant</h4>
            <label for="startField">Start Year ${yearHint}</label>

            <fieldset class="dateTime">              
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


<#assign acUrl value="/autocomplete?tokenize=true" />
<#assign sparqlQueryUrl ="/ajax/sparqlQuery" />

<#assign sparqlForAcFilter = "PREFIX core: <${vivoCore}> SELECT ?grantUri WHERE {<${subjectUri}> <${predicateUri}> ?grantRole . ?grantRole core:roleIn ?grantUri .}>"
    
<script type="text/javascript">
var customFormData  = {
    sparqlForAcFilter: '${sparqlForAcFilter}',
    sparqlQueryUrl: '${sparqlQueryUrl}',
    acUrl: '${acUrl}',
    acType: 'http://vivoweb.org/ontology/core#Grant',
    editMode: 'add',
    submitButtonTextType: 'compound',
    typeName: 'Grant'         
};
</script>

</section>

</#if>
