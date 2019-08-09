<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--
    Individual profile page template for foaf:Person individuals. This is the default template for foaf persons
    in the Wilma theme and should reside in the themes/wilma/templates directory.
-->

<#include "individual-setup.ftl">
<#import "lib-vivo-properties.ftl" as vp>
<#--Number of labels present-->
<#if !labelCount??>
	<#assign labelCount = 0 >
</#if>
<#--Number of available locales-->
<#if !localesCount??>
	<#assign localesCount = 1>
</#if>
<#--Number of distinct languages represented, with no language tag counting as a language, across labels-->
<#if !languageCount??>
	<#assign languageCount = 1>
</#if>
<#assign visRequestingTemplate = "foaf-person-tenderfoot">

<#--add the VIVO-ORCID interface -->
<#include "individual-orcidInterface.ftl">

<section id="individual-admin">
	<div class="row">
		<div class="col-md-12">
			<#include "individual-adminPanel.ftl">
		</div>
	</div>
</section>

<section id="individual-intro" class="vcard person" role="region"><span itemscope itemtype="http://schema.org/Person">
	<div class="row row-eq-height">
		<div class="col-md-2 photo-wrapper">
			<!-- Image -->
		<#assign individualImage>
			<@p.image individual=individual
			propertyGroups=propertyGroups
			namespaces=namespaces
			editable=editable
			showPlaceholder="always" />
		</#assign>
		<#if ( individualImage?contains('<img class="individual-photo"') )>
			<#assign infoClass = 'class="withThumb"'/>
		</#if>

            <!-- div id="photo-wrapper">${individualImage}</div -->
		${individualImage}
		</div>
		<div class="col-xs-10 person-details">
			<div class="row title">
				<div class="col-md-12">
					<span id="iconControlsRightSide">
						<img id="uriIcon" title="${individual.uri}" src="${urls.images}/individual/uriIcon.gif" alt="${i18n().uri_icon}"/>
						<#if checkNamesResult?has_content >
							<img id="qrIcon"  src="${urls.images}/individual/qr_icon.png" alt="${i18n().qr_icon}" />
								<span id="qrCodeImage" class="hidden">${qrCodeLinkedImage!}
									<a class="qrCloseLink" href="#"  title="${i18n().qr_code}">${i18n().close_capitalized}</a>
								</span>
						</#if>
					</span>
					<section class="vcard person">
						<h1 class="foaf-person">
							<#-- Label -->
								<span itemprop="name" class="fn"><@p.label individual editable labelCount localesCount/></span>
						</h1>
						<section id="preferredTitle">
							<#--  Display preferredTitle if it exists; otherwise mostSpecificTypes -->
							<#assign title = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#Title")!>
							<#if title?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
								<#if (title.statements?size < 1) >
									<@p.addLinkWithLabel title editable />
								<#elseif editable>
									<h2>${title.name?capitalize!}</h2>
									<@p.verboseDisplay title />
								</#if>
								<#list title.statements as statement>
									<span itemprop="jobTitle" class="display-title<#if editable>-editable</#if>">${statement.preferredTitle}</span>
									<@p.editingLinks "${title.localName}" "${title.name}" statement editable title.rangeUri />
								</#list>
							</#if>
							<#-- If preferredTitle is unpopulated, display mostSpecificTypes -->
							<#if ! (title.statements)?has_content>
								<@p.mostSpecificTypes individual />
							</#if>
						</section>
					</section>
				</div>
			</div>
			<div class="row person-details">
				<div class="col-md-12">
					<!-- Positions -->
					<#include "individual-positions.ftl">
					<!-- Research Areas -->
					<#include "individual-researchAreas.ftl">
				</div>
			</div>
		</div>
	</div>
</span></section>

<section itemscope itemtype="http://schema.org/Person" id="individual-intro" class="vcard person" role="region">
    <section id="individual-info" ${infoClass!} role="region">
		<#if editable>
			<#if claimSources?size &gt; 0>
				${i18n().claim_publications_by}
				<#if claimSources?seq_contains("doi")>
					<form action="${urls.base}/createAndLink/doi" method="get" style="display: inline-block;">
						<input type="hidden" name="profileUri" value="${individual.uri}" />
						<input type="submit" class="submit" value="${i18n().claim_publications_by_doi}" />
					</form>
				</#if>
				<#if claimSources?seq_contains("pmid")>
					<form action="${urls.base}/createAndLink/pmid" method="get" style="display: inline-block;">
						<input type="hidden" name="profileUri" value="${individual.uri}" />
						<input type="submit" class="submit" value="${i18n().claim_publications_by_pmid}" />
					</form>
				</#if>
			</#if>
		</#if>
    </section>
</section>

<#assign nameForOtherGroup = "${i18n().other}">

<#-- Ontology properties -->
<#if !editable>
<#-- We don't want to see the first name and last name unless we might edit them. -->
	<#assign skipThis = propertyGroups.pullProperty("http://xmlns.com/foaf/0.1/firstName")!>
	<#assign skipThis = propertyGroups.pullProperty("http://xmlns.com/foaf/0.1/lastName")!>
</#if>

<div class="row">
	<div id="property-tabs" class="col-md-8">
		<#include "individual-property-group-tabs.ftl">
	</div>
	<div class="col-md-4">
		<#include "individual-visualizationFoafPerson.ftl">

		<!-- Contact Info -->
		<#include "individual-contactInfo.ftl">

		<!-- Websites -->
		<#include "individual-webpage.ftl">
	</div>
</div>

<#assign rdfUrl = individual.rdfUrl>

<#if rdfUrl??>
<script>
    var individualRdfUrl = '${rdfUrl}';
</script>
</#if>
<script>
    var imagesPath = '${urls.images}';
    var individualUri = '${individual.uri!}';
    var individualPhoto = '${individual.thumbNail!}';
    var exportQrCodeUrl = '${urls.base}/qrcode?uri=${individual.uri!}';
    var baseUrl = '${urls.base}';
    var i18nStrings = {
        displayLess: '${i18n().display_less}',
        displayMoreEllipsis: '${i18n().display_more_ellipsis}',
        showMoreContent: '${i18n().show_more_content}',
        verboseTurnOff: '${i18n().verbose_turn_off}',
        researchAreaTooltipOne: '${i18n().research_area_tooltip_one}',
        researchAreaTooltipTwo: '${i18n().research_area_tooltip_two}'
    };
    var i18nStringsUriRdf = {
        shareProfileUri: '${i18n().share_profile_uri}',
        viewRDFProfile: '${i18n().view_profile_in_rdf}',
        closeString: '${i18n().close}'
    };
</script>

${stylesheets.add(
	'<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />',
	'<link rel="stylesheet" href="${urls.base}/css/individual/individual-vivo.css" />',
	'<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.12.1.css" />',
	'<link rel="stylesheet" type="text/css" href="${urls.base}/css/jquery_plugins/qtip/jquery.qtip.min.css" />'
)}

${headScripts.add(
	'<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>',
	'<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip.min.js"></script>',
	'<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.truncator.js"></script>'
)}

${scripts.add(
	'<script async type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.12.1.min.js"></script>',
	'<script async type="text/javascript" src="${urls.base}/js/individual/individualUtils.js"></script>',
	'<script async type="text/javascript" src="${urls.base}/js/individual/individualQtipBubble.js"></script>',
	'<script async type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>',
    '<script async type="text/javascript" src="${urls.base}/js/individual/moreLessController.js"></script>',
	'<script async type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>',
	'<script async type="text/javascript" src="https://d1bxh8uas1mnw7.cloudfront.net/assets/embed.js"></script>',
	'<script async type="text/javascript" src="//cdn.plu.mx/widget-popup.js"></script>'
)}
