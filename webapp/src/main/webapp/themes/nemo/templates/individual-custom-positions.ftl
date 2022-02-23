<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- List of positions for the individual -->
<#assign positions = propertyGroups.pullProperty("${core}relatedBy", "${core}Position")!>
<#if positions?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <#assign localName = positions.localName>
   <a 
   		id="${localName}" 
   		data-toggle="collapse"
   		data-target="#individual-personInPosition"
   		class="mainPropGroup h4" 
   		title="${positions.publicDescription!}"
   	>
   		${positions.name?capitalize} <@p.addLink positions editable /> <@p.verboseDisplay positions />
   		<span class="caret"></span>
   	</a>
    <ul id="individual-personInPosition" class="collapse in" role="list">
        <@p.objectProperty positions editable />
    </ul> 
</#if> 
