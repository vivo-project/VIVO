<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a new individual from the Site Admin page: VIVO version -->

<#--Retrieve certain edit configuration information-->
<#assign typeName = editConfiguration.pageData.typeName />
<#assign isPersonType = editConfiguration.pageData.isPersonType />

<h2>Create a new ${typeName}</h2>

<#if errorFirstNameIsEmpty??>
    <#assign errorMessage = "Enter a first name." />
</#if>

<#if errorLastNameIsEmpty??>
    <#assign errorMessage = "Enter a last name." />
</#if>

<#if errorNameFieldIsEmpty??>
    <#assign errorMessage = "Enter a name." />
</#if>

<#if errorMessage?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>${errorMessage}</p>
    </section>
</#if>

<#assign requiredHint = "<span class='requiredHint'> *</span>" />

<section id="newIndividual" role="region">        
    
    <form id="newIndividual" class="customForm noIE67" action="${submitUrl}"  role="add new individual">
 
    <#if isPersonType = "true">       
        <p>
            <label for="firstName">First Name ${requiredHint}</label>
            <input size="30"  type="text" id="firstName" name="firstName" value="" />
        </p>

        <p>
            <label for="lastName">Last Name ${requiredHint}</label>
            <input size="30"  type="text" id="lastName" name="lastName" value="" />
        </p>
    <#else>       
        <p>
            <label for="name">Name ${requiredHint}</label>
            <input size="30"  type="text" id="label" name="label" value="" />
        </p>
    </#if>

    <p class="submit">
        <input type="hidden" name = "editKey" value="${editKey}"/>
        <input type="submit" id="submit" value="Create ${typeName}"/>
        <span class="or"> or </span><a class="cancel" href="${urls.base}/siteAdmin">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

    </form>


</section>

