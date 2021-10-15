<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for property listing on individual profile page -->

<#import "lib-properties.ftl" as p>
<#assign subjectUri = individual.controlPanelUrl()?split("=") >
<#assign tabCount = 1 >
<#assign sectionCount = 1 >

<!-- ${propertyGroups.all?size} -->

<div class="row">
	<div class="col-md-12">
		<ul class="nav nav-tabs user-nav" role="tablist" id="individualNavTabs">
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
					<li id="${groupNameHtmlId?replace("/","-")}Tab" role="presentation" class="active">
					   <a href="#${groupNameHtmlId?replace("/","-")}" aria-controls="${groupName?capitalize}" role="tab" data-toggle="tab">
						   ${groupName?capitalize}
					   </a>
					</li>
					<#assign tabCount = 2>
				<#else>
					<li id="${groupNameHtmlId?replace("/","-")}Tab" role="presentation">
					   <a href="#${groupNameHtmlId?replace("/","-")}" aria-controls="${groupName?capitalize}" role="tab" data-toggle="tab">
						   ${groupName?capitalize}
					   </a>
					</li>
				</#if>
			</#if>
		</#list>
		<#--
			<li id="visualisationTab" role="presentation">
				<a href="#visualisation" aria-controls="Visualisations" role="tab" data-toggle="tab">
					Visualisation
				</a>
			</li>
		-->
		</ul>
	</div>
	<div class="col-md-12 person-container">
		<div class="tab-content" id="individualNavTabsContent">
			<#list propertyGroups.all as group>
			<#if (group.properties?size > 0)>
				<#assign groupName = group.getName(nameForOtherGroup)>
				<#assign groupNameHtmlId = p.createPropertyGroupHtmlId(groupName) >
				<#assign verbose = (verbosePropertySwitch.currentValue)!false>
				<div id="${groupNameHtmlId?replace("/","-")}" class="tab-pane fade in <#if (sectionCount > 1) ><#else>active</#if>" role="tabpanel">
				<#-- Display the group heading -->
					<#if groupName?has_content>
						<#--the function replaces spaces in the name with underscores, also called for the property group menu-->
						<#assign groupNameHtmlId = p.createPropertyGroupHtmlId(groupName) >
						<h2 id="${groupNameHtmlId?replace("/","-")}" pgroup="tabs" class="hidden">${groupName?capitalize}</h2>
					<#else>
						<h2 id="properties" pgroup="tabs" class="hidden">${i18n().properties_capitalized}</h2>
						<#--<section id="${groupNameHtmlId?replace("/","-")}" class="property-group" role="region" style="<#if (sectionCount > 1) >display:none<#else>display:block</#if>"> -->
					</#if>
					<div id="${groupNameHtmlId?replace("/","-")}Group">
						<#-- List the properties in the group -->
						<#include "individual-properties.ftl">
					</div>
				</div>
				<!-- end property-group -->
				<#assign sectionCount = 2 >
			</#if>
			</#list>
			<#--
				<div id="visualisation" class="tab-pane fade" role="tabpanel">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">
								Co-Author Network
							</h3>
						</div>
						<div class="panel-body">
							<iframe src="${individual.coAuthorVisUrl()}?iframemode=yes" width="800" height="800" style="border: none;" target="_parent"></iframe>
						</div>
					</div>
				</div>
			-->
		</div>
	</div>
</div>
<script>
	var individualLocalName = "${individual.localName}";
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-property-groups.css" />')}
${headScripts.add('<script type="text/javascript" src="${urls.base}/js/amplify/amplify.store.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/propertyGroupControls.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.theme}/js/showcase.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.theme}/js/publonsAuthorWidget.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.theme}/js/resultsNav.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.theme}/js/readshare.js""></script>')}
