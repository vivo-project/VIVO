<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Overview on individual profile page -->

<#assign overview = propertyGroups.getPropertyAndRemoveFromList("${core}overview")!> 
<#if overview?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <@p.addLinkWithLabel overview editable />
    <#list overview.statements as statement>
        <p class="individual-overview">${statement.value}</p>
        
        <@p.editingLinks "${overview.localName}" statement editable />
    </#list>
</#if>