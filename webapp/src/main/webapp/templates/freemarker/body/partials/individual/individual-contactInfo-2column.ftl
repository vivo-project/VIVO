<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Contact info on individual profile page -->
<#assign phone = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#Telephone")!>
<#assign pEmail = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#Work")!>
<#assign email = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#Email")!>

<#if editable || ( phone?has_content || pEmail?has_content || email?has_content ) >
    <h2 id="contactHeading" class="mainPropGroup">${i18n().contact_capitalized}</h2>
</#if>

<#-- the layout is different in edit mode -->
<#if !editable>
    <div id="contactOuterDiv">
        <div id="contactEmailDiv">
</#if>
<#-- Primary Email -->    
<@emailLinks "${core}primaryEmail" pEmail />

<#-- Additional Emails --> 
<@emailLinks "${core}email" email />   
<#if !editable>
        </div> <!-- contactEmailDiv -->
        <div id="contactPhoneDiv">
</#if>
<#-- Phone --> 
<@phoneLinks phone /> 
<#if !editable>
        </div> <!-- contactPhoneDiv -->
    </div> <!-- contactOuterDiv -->
</#if>
<#macro phoneLinks phone>
<#if phone?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <#if editable>
        <h3 id="contact">${i18n().phone} <@p.addLink phone editable />  </h3>
        <@p.verboseDisplay phone />
    </#if>
    <#if phone.statements?has_content> <#-- if there are any statements -->
        <ul id="phone-list" role="list">
            <#list phone.statements as statement>
                <li role="listitem" <#if editable>style="padding-left:10px;"</#if>>                           
                    <span itemprop="telephone">${statement.number!}</span>
                    <@p.editingLinks "${phone.localName}" "${phone.name}" statement editable phone.rangeUri />
                </li>
            </#list>
        </ul>
    </#if>
</#if>
</#macro>
<#macro emailLinks property email>        
    <#if property == "${core}primaryEmail">
        <#local listId = "primary-email">
        <#local label = "${i18n().primary_email}">
    <#else>
        <#local listId = "additional-emails">
        <#local label = "${i18n().additional_emails}">
    </#if>     
    <#if email?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <#if editable>
            <h3 id="contact" class="${listId}">${label}<@p.addLink email editable label/></h3>
            <@p.verboseDisplay email />
        </#if>
        <#if email.statements?has_content> <#-- if there are any statements -->
            <ul id="${listId}" role="list">
                <#list email.statements as statement>
                    <li role="listitem" <#if editable>style="padding-left:10px;"</#if>>
                        
                        <a itemprop="email" class="email" href="mailto:${statement.emailAddress!}" title="${i18n().email}">${statement.emailAddress!}</a>
                        <@p.editingLinks "${email.localName}" "${email.name}" statement editable email.rangeUri />
                    </li>
                </#list>
            </ul>
        </#if>
    </#if>
</#macro>
