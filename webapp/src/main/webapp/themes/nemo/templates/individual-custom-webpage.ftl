<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign webpage = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#URL")!>

<div class="websiteContainer" style="width:100%;text-align: left;font-size:small;">
    <#if webpage?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <#if !editable && individual.person() > 
            <h5><span class="glyphicon glyphicon-globe" aria-hidden="true"></span> <strong>${i18n().websites}</strong></h5>
        </#if>
        <@p.addLinkWithLabel webpage editable i18n().websites/> 
        <#assign localName = webpage.localName>
        <ul class="individual-urls" role="list" <#if individual.organization() && !editable>style="font-size:1.15em"</#if>>
            <@p.objectProperty webpage editable />
        </ul>
    </#if>
</div>