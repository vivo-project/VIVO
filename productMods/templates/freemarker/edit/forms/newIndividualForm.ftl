<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a new individual from the Site Admin page: VIVO version -->


<h2>Create a new ${subclassName}</h2>

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
 
    <#if isPersonType >       
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
            <input size="30"  type="text" id="name" name="name" value="" />
        </p>
    </#if>

    <p class="submit">
        <input type="hidden" name = "editKey" value="${???}"/>
        <input type="submit" id="submit" value="editConfiguration.submitLabel"/>
        <span class="or"> or <a class="cancel" href="${editConfiguration.cancelUrl}">Cancel</a>
    </p>

    <p id="requiredLegend" class="requiredHint">* required fields</p>

    </form>


</section>

