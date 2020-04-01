<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- List of research areas for the individual -->
<#assign geographicFoci = propertyGroups.pullProperty("${core}geographicFocus")!>
<#if geographicFoci?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <#assign localName = geographicFoci.localName>
    <h2 id="${localName}" class="mainPropGroup" style="clear:left"  title="${geographicFoci.publicDescription!}">
        ${geographicFoci.name
        }
        <@p.addLink geographicFoci editable /> <@p.verboseDisplay geographicFoci />
    </h2>
    <ul id="individual-hasResearchArea" role="list" >
        <@p.objectProperty geographicFoci editable />
    </ul>
</#if>
