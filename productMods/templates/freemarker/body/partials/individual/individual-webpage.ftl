<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- This snippet will be included in lib-vivo-properties.ftl, so users will be able to have a 
    different view when extending wilma theme

    <#assign webpage = propertyGroups.pullProperty("${core}webpage")!>
    <@p.objectPropertyListing webpage editable />
    
    
-->
    <#assign webpage = propertyGroups.pullProperty("${core}webpage")!>
    <#if webpage?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <#if !editable && individual.person() > 
            <ul style="font-size:0.9em;padding-bottom:4px"><li><strong>Websites</strong></li></ul>
        </#if>
        <@p.addLinkWithLabel webpage editable "Websites"/>
        <#assign localName = webpage.localName>
        <ul id="individual-${localName}" class="individual-urls" role="list" <#if individual.organization() && !editable>style="font-size:1.15em"</#if>>
            <@p.objectProperty webpage editable />
        </ul>
    </#if>

