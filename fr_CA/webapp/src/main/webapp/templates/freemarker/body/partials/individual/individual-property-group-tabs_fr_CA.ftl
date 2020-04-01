<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- Template for property listing on individual profile page -->

<#import "lib-properties.ftl" as p>
<#assign subjectUri = individual.controlPanelUrl()?split("=") >
<#assign tabCount = 1 >
<#assign sectionCount = 1 >
<!-- ${propertyGroups.all?size} -->
<ul class="propertyTabsList">
    <li  class="groupTabSpacer">&nbsp;</li>
<#list propertyGroups.all as groupTabs>
    <#if ( groupTabs.properties?size > 0 ) >
        <#assign groupName = groupTabs.getName(nameForOtherGroup)>
        <#if groupName?has_content>
		    <#--the function replaces spaces in the name with underscores, also called for the property group menu-->
    	    <#assign groupNameHtmlId = p.createPropertyGroupHtmlId(groupName) >
        <#else>
            <#assign groupName = "${i18n().properties_capitalized}">
    	    <#assign groupNameHtmlId = "${i18n().properties}" >
        </#if>
        <#if tabCount = 1 >
            <li class="selectedGroupTab clickable" groupName="${groupNameHtmlId?replace("/","-")}">${groupName?capitalize}</li>
            <li class="groupTabSpacer">&nbsp;</li>
            <#assign tabCount = 2>
        <#else>
            <li class="nonSelectedGroupTab clickable" groupName="${groupNameHtmlId?replace("/","-")}">${groupName?capitalize}</li>
            <li class="groupTabSpacer">&nbsp;</li>
        </#if>
    </#if>
</#list>
<#if (propertyGroups.all?size > 1) >
    <li  class="nonSelectedGroupTab clickable" groupName="viewAll">${i18n().view_all_capitalized}</li>
    <li  class="groupTabSpacer">&nbsp;</li>
</#if>
</ul>
<#list propertyGroups.all as group>
    <#if (group.properties?size > 0)>
        <#assign groupName = group.getName(nameForOtherGroup)>
        <#assign groupNameHtmlId = p.createPropertyGroupHtmlId(groupName) >
        <#assign verbose = (verbosePropertySwitch.currentValue)!false>
        <section id="${groupNameHtmlId?replace("/","-")}" class="property-group" role="region" style="<#if (sectionCount > 1) >display:none<#else>display:block</#if>">
        <nav id="scroller" class="scroll-up hidden" role="navigation">
            <a href="#branding" title="${i18n().scroll_to_menus}" >
                <img src="${urls.images}/individual/scroll-up.gif" alt="${i18n().scroll_to_menus}" />
            </a>
        </nav>

        <#-- Display the group heading -->
        <#if groupName?has_content>
		    <#--the function replaces spaces in the name with underscores, also called for the property group menu-->
    	    <#assign groupNameHtmlId = p.createPropertyGroupHtmlId(groupName) >
            <h2 id="${groupNameHtmlId?replace("/","-")}" pgroup="tabs" class="hidden">${groupName?capitalize}</h2>
        <#else>
            <h2 id="properties" pgroup="tabs" class="hidden">${i18n().properties_capitalized}</h2>
        </#if>
        <div id="${groupNameHtmlId?replace("/","-")}Group" >
            <#-- List the properties in the group   -->
            <#include "individual-properties.ftl">
        </div>
        </section> <!-- end property-group -->
        <#assign sectionCount = 2 >
    </#if>
</#list>
<script>
    var individualLocalName = "${individual.localName}";
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-property-groups.css" />')}
${headScripts.add('<script type="text/javascript" src="${urls.base}/js/amplify/amplify.store.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/propertyGroupControls.js"></script>')}

