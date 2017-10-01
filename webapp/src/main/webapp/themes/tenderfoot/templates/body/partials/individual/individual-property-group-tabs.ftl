<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for property listing on individual profile page -->

<#import "lib-properties.ftl" as p>
<#assign subjectUri = individual.controlPanelUrl()?split("=") >
<#assign tabCount = 1 >
<#assign sectionCount = 1 >

<div class="row">
    <div class="col-md-12">
        <ul class="nav nav-tabs">
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
                    <li data-toggle="tab" groupName="${groupNameHtmlId?replace("/","-")}" class="active" href="#${groupNameHtmlId?replace("/","-")}Group"><a href="#">${groupName?capitalize}</a></li>
					<#assign tabCount = 2>
				<#else>
                    <li data-toggle="tab" groupName="${groupNameHtmlId?replace("/","-")}" href="#${groupNameHtmlId?replace("/","-")}Group"><a href="#">${groupName?capitalize}</a></li>
				</#if>
			</#if>
		</#list>
		<#if (propertyGroups.all?size > 1) >
            <li id="viewAllTab" data-toggle="tab" groupName="viewAll" href="#viewAll"><a href="#">${i18n().view_all_capitalized}</a></li>
		</#if>
        </ul>
    </div>
</div>
<div class="row">
    <div class="col-md-12 tab-content">
		<#list propertyGroups.all as group>
			<#if (group.properties?size > 0)>
				<#assign groupName = group.getName(nameForOtherGroup)>
				<#assign groupNameHtmlId = p.createPropertyGroupHtmlId(groupName) >
				<#assign verbose = (verbosePropertySwitch.currentValue)!false>

                <div id="${groupNameHtmlId?replace("/","-")}Group"
                     class="tab-pane <#if (sectionCount == 1) >active</#if>"
                     role="tabpanel">
					<#-- Display the group heading -->
					<#if groupName?has_content>
						<#--the function replaces spaces in the name with underscores, also called for the property group menu-->
						<#assign groupNameHtmlId = p.createPropertyGroupHtmlId(groupName) >
                        <h2 id="${groupNameHtmlId?replace("/","-")}" pgroup="tabs" class="hidden">${groupName?capitalize}</h2>
					<#else>
                        <h2 id="properties" pgroup="tabs" class="hidden">${i18n().properties_capitalized}</h2>
					</#if>
					<#-- List the properties in the group   -->
					<#include "individual-properties.ftl">
                </div>
				<#assign sectionCount = 2 >
			</#if>
		</#list>
    </div>
</div>
<script>
    var individualLocalName = "${individual.localName}";
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-property-groups.css" />')}
${headScripts.add('<script type="text/javascript" src="${urls.base}/js/amplify/amplify.store.min.js"></script>')}
${scripts.add('<script src="${urls.theme}/js/propertyGroupControls-bootstrap.js"></script>')}
