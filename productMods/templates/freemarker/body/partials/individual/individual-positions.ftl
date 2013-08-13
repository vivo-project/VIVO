<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- List of positions for the individual --> 
<#assign positions = propertyGroups.pullProperty("${core}personInPosition")!>
<#if positions?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <#assign localName = positions.localName>
    <h2 id="${localName}" class="mainPropGroup">${positions.name?capitalize} <@p.addLink positions editable /> <@p.verboseDisplay positions /></h2>
    <ul id="individual-${localName}" role="list">
        <@p.objectProperty positions editable />
    </ul> 
</#if> 