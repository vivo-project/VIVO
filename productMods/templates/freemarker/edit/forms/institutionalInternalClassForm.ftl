<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
Institutional Internal Class Form 
To be associated later (upon completion of N3 Refactoring) with 
edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.InstitutionalInternalClassForm.
-->

<h2>${i18n().institutional_internal_class}</h2>

<section id="introMessage" role="region">
    <p>${i18n().internal_class_intro_one}</p>
    <p>${i18n().internal_class_intro_two}</p>
</section>

<section role="region">
    <form method="POST" action="${formUrl}" class="customForm">
        <input type="hidden" name="submitForm" id="submitForm" value="true" />
        
        <#--If no local ontologies, display message for user to create a local ontology-->
        <#if ontologiesExist = false>
        <section id="noLocalOntologyExists" role="region">
            <h4>${i18n().no_local_oncologies}</h4>
            <#if defaultNamespace?has_content && defaultNamespace?contains("individual/")>
                <#assign localOntologyNamespace = defaultNamespace?replace("individual/", "") />
                <p>${i18n().namespace_must_use_this_pattern}:</p>
                <blockquote>${localOntologyNamespace}ontology/<em>yourOntologyName</em></blockquote>
            </#if>
            <p>${i18n().please_create} <a href='${urls.base}/editForm?controller=Ontology' title="${i18n().new_local_ontology}">${i18n().new_local_oncology}</a> ${i18n().return_here_to_define_class}</p>
        </section>
        
        <#--else if local ontologies exist and local classes exist, show drop-down of local classes-->
        <#elseif useExistingLocalClass?has_content> 
        <section id="existingLocalClass" role="region">
            <#--Populated based on class list returned-->
            <label for="existingLocalClasses">${i18n().select_existing_local_class}</label>
       
            <select id="existingLocalClasses" name="existingLocalClasses" role="combobox"<strong></strong>>
                <#assign classUris = existingLocalClasses?keys />
                
                <#--If internal class exists, check against value in drop-down and select option-->
                <#--<option value="" role="option">${i18n().select_one}</option>-->
                <#list classUris as localClassUri>
                    <option value="${localClassUri}" role="option" <#if existingInternalClass = localClassUri>selected</#if> >${existingLocalClasses[localClassUri]}</option>
                </#list>
            </select>
            
            <p>${i18n().cannot_find_class} <a href="${formUrl}?cmd=createClass" title="${i18n().create_new_class}">${i18n().create_new_one}</a>.</p>
        </section>

        <#--if parameter to create new class passed or if there are local ontologies but no local classes, show create new class page-->
        <#elseif createNewClass?has_content>
        <section id="createNewLocalClass" role="region">
            <h3>${i18n().create_new_class}</h3>
        
            <label for="localClassName">${i18n().name_capitalized}<span class="requiredHint"> *</span></label>
            <input type="text" id="localClassName" name="localClassName" value="" role="input" />
            <p class="note">${i18n().use_capitals_each_word}</p>
    
            <#--If more than one local namespace, generate select-->
            <#if multipleLocalNamespaces = true>
                <label for="existingLocalNamespace">${i18n().local_namespace}<span class="requiredHint"> *</span></label>
                
                <select id="existingLocalNamespaces" name="existingLocalNamespaces" role="combobox">
                    <#assign namespaceUris = existingLocalNamespaces?keys /> 
                    <#list namespaceUris as existingNamespace>
                        <option value="${existingNamespace}" role="option">${existingLocalNamespaces[existingNamespace]}</option>
                    </#list>
            </select>
            <#else>
            <input type="hidden" id="existingLocalNamespaces" name="existingLocalNamespaces" value="${existingLocalNamespace}" />
            </#if>
        </section>
    
        <#--this case is an error case-->
        <#else>
            <p>${i18n().problematic_section_error}</p>
        </#if>

        <#--only show submit and cancel if ontologies exist-->
        <#if ontologiesExist = true>
            <input type="submit" name="submit-internalClass" value="${submitAction}" class="submit" /> ${i18n().or} <a class="cancel" href="${cancelUrl}" title="${i18n().cancel_title}">${i18n().cancel_link}</a>
        </#if>
    </form>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/institutionalInternalClass.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}