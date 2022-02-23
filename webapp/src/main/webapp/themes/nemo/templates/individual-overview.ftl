<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Overview on individual profile page -->

<#assign overview = propertyGroups.pullProperty("${core}overview")!> 
<#if overview?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <@p.addLinkWithLabel overview editable />
    <#list overview.statements as statement>
        <div class="individual-overview">
            <div class="overview-value">
                ${statement.value}
            </div>
            <@p.editingLinks "${overview.name}" "" statement editable />
        </div>
    </#list>
</#if>
