<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Contact info on individual profile page -->

<#assign phone = propertyGroups.pullProperty("${core}phoneNumber")!>
<#assign primaryEmail = propertyGroups.pullProperty("${core}primaryEmail")!>
<#assign addlEmail = propertyGroups.pullProperty("${core}email")!>

<#if phone?has_content || primaryEmail?has_content || addlEmail?has_content >
    <ul style="font-size:1em;padding-bottom:4px"><li><strong>Contact Info</strong></li></ul>
</#if>

<#-- Primary Email -->    
<@emailLinks "${core}primaryEmail" primaryEmail />

<#-- Additional Emails --> 
<@emailLinks "${core}email" addlEmail />   
  
<#-- Phone --> 

<#if phone?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <@p.addLinkWithLabel phone editable />
    <#if phone.statements?has_content> <#-- if there are any statements -->
        <ul id="individual-phone" role="list" <#if editable>style="list-style:none;margin-left:0;"</#if>>
            <#list phone.statements as statement>
                <li role="listitem">
                    ${statement.value}
                    <@p.editingLinks "${phone.localName}" statement editable />
                </li>
            </#list>
        </ul>
    </#if>
</#if>

<#macro emailLinks property email>
    <#if property == "${core}primaryEmail">
        <#local listId = "primary-email">
        <#local label = "Primary Email">
    <#else>
        <#local listId = "additional-emails">
        <#local label = "Additional Emails">
    </#if>     
    <#if email?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <@p.addLinkWithLabel email editable label/>
        <#if email.statements?has_content> <#-- if there are any statements -->
            <ul id="${listId}" class="individual-emails" role="list" <#if editable>style="list-style:none;margin-left:0;"</#if>>
                <#list email.statements as statement>
                    <li role="listitem">
                        <a class="email" href="mailto:${statement.value}" title="email">${statement.value}</a>
                        <@p.editingLinks "${email.localName}" statement editable />
                    </li>
                </#list>
            </ul>
        </#if>
    </#if>
</#macro>
