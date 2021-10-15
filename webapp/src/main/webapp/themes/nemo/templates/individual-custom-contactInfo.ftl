<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Contact info on individual profile page -->

<#assign phone = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#Telephone")!>
<#assign primaryEmail = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#Work")!>
<#assign addlEmail = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#Email")!>
<#assign mailingAddress = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#Address")!>


<#-- <#if phone?has_content || primaryEmail?has_content || addlEmail?has_content >
    <h5 class="contactInfoHeading">
        ${i18n().contact_info}
    </h5>

</#if> -->

<#-- Wrapped the emails in a div so we can style them together -->
<div class="emailsContainer" style="width:100%;text-align: left;font-size:small;">
    <h5>
        <span class="glyphicon glyphicon-envelope" aria-hidden="true"></span> <strong>E-mail</strong>
    </h5>
    <#-- Primary Email -->    
    <@emailLinks "primaryEmail" primaryEmail />

    <#-- Additional Emails --> 
    <@emailLinks "email" addlEmail />
</div>
  
<#-- Phone --> 
<div class="phoneContainer" style="width:100%;text-align: left;font-size:small;">
<#if phone?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <@p.addLinkWithLabel phone editable />
    <#if phone.statements?has_content> <#-- if there are any statements -->
        <ul id="individual-phone" role="list" <#if editable>style="list-style:none;margin-left:0;"</#if>>
            <h5>
                <span class="glyphicon glyphicon-phone-alt" aria-hidden="true"></span> <strong>Phone</strong>
            </h5>
            <#list phone.statements as statement>
                <li class="list-group-item" role="listitem" style="padding-top:2px;padding-bottom:2px;">
                    <span itemprop="telephone">${statement.number!}</span>
                    <@p.editingLinks "${phone.localName}" "${phone.name}" statement editable phone.rangeUri />
                </li>
            </#list>
        </ul>
    </#if>
</#if>
</div>

<#-- 
<#if mailingAddress?has_content>
	<@p.addLinkWithLabel mailingAddress editable />
	<#if mailingAddress.statements?has_content>
		<div id="individual-address" role="list" <#if editable>style="list-style:none;margin-left:0;"</#if>>
			<h4>
				<span class="glyphicon glyphicon-home" aria-hidden="true"></span>
			</h4>
			<#list mailingAddress.statements as statement>
				${statement.street!}<br/>
				${statement.locality!},
				${statement.region!}
				${statement.postalCode!}
				${statement.country!}<br/>
				<@p.editingLinks "${mailingAddress.localName}" "${mailingAddress.name}" statement editable mailingAddress.rangeUri />
			</#list>
		</div>
	</#if>
</#if>
 -->

<div class="addressContainer" style="width:100%;text-align: left;font-size:small;">
<#if mailingAddress?has_content>
    <@p.addLinkWithLabel mailingAddress editable />
    <#if mailingAddress.statements?has_content> <#-- if there are any statements -->	
	    <div id="individual-address" role="list" <#if editable>style="list-style:none;margin-left:0;"</#if>>
		    <h5>
			    <span class="glyphicon glyphicon-briefcase" aria-hidden="true"></span> <strong>Office Address</strong>
		    </h5>
		    <@p.objectProperty mailingAddress editable />
	    </div>
    </#if>
</#if>
</div>

<#if officeHours?has_content>
	<@p.addLinkWithLabel officeHours editable />
	<#if officeHours.statements?has_content> <#-- if there are any statements -->	
		<div id="individual-officehours" role="list" <#if editable>style="list-style:none;margin-left:0;"</#if>>
			<h4>
				<span class="glyphicon glyphicon-time" aria-hidden="true" data-toggle="tooltip" title="Office Hours"></span>
			</h4>
			<#list officeHours.statements as statement>
				${statement.value!}
				<@p.editingLinks "${officeHours.localName}" "${officeHours.name}" statement editable officeHours.rangeUri />
			</#list>
		</div>
	</#if>
</#if>


<#macro emailLinks property email>
    <#if property == "primaryEmail">
        <#local listId = "primary-email">
        <#local label = "${i18n().primary_email_capitalized}">
    <#else>
        <#local listId = "additional-emails">
        <#local label = "${i18n().additional_emails_capitalized}">
    </#if>     
    <#if email?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <@p.addLinkWithLabel email editable label/>
        <#if email.statements?has_content> <#-- if there are any statements -->
            <ul id="${listId}" class="individual-emails" role="list" <#if editable>style="list-style:none;margin-left:0;"</#if>>
                <#list email.statements as statement>
                    <li class="list-group-item" role="listitem" style="padding-top:2px;padding-bottom:2px;">
                        <a itemprop="email" class="email" href="mailto:${statement.emailAddress!}" title="${i18n().email}">
                            ${statement.emailAddress!}
                        </a>
                        <@p.editingLinks "${email.localName}" "${email.name}" statement editable email.rangeUri />
                    </li>
                </#list>
            </ul>
        </#if>
    </#if>
</#macro>