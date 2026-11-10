<#-- $This file is distributed under the terms of the license in LICENSE$ -->


<#assign webpage = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#URL")!>
    <#if webpage?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <#if !editable && individual.person() >
            <p class="individual-section-label">${i18n().websites}</p>
        </#if>
        <@p.addLinkWithLabel webpage editable i18n().websites headingTag="p"/>
        <#assign localName = webpage.localName>
        <ul id="individual-${localName}" class="individual-urls" role="list" <#if individual.organization() && !editable>style="font-size:1.15em"</#if>>
            <@p.objectProperty webpage editable />
        </ul>
    </#if>
