<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Individual profile page template for foaf:Person individuals -->
<!--[if IE 7]>
<link rel="stylesheet" href="${urls.base}/css/individual/ie7-standard-view.css" />
<![endif]-->
<#-- <#include "individual-setup.ftl"> -->
<#import "individual-qrCodeGenerator.ftl" as qr>
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
<#assign qrCodeIcon = "qr-code-icon.png">
<#assign visRequestingTemplate = "foaf-person-2column">
<#--add the VIVO-ORCID interface -->
<#include "individual-orcidInterface.ftl">  
<section id="individual-intro" class="vcard person" role="region"><span itemscope itemtype="http://schema.org/Person">
    <section id="share-contact" role="region"> 
        <#-- Image -->           
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

        <div id="photo-wrapper" >${individualImage}</div>
        
        <#include "individual-visualizationFoafPerson.ftl">
    </section> <!-- end share-contact -->
    <section id="individual-info" ${infoClass!} role="region">
        <#include "individual-adminPanel.ftl">
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} ${i18n().for} ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}" title="${i18n().return_to(relatedSubject.name)}">&larr; ${i18n().return_to(relatedSubject.name)}</a></p>
            <#else>  
                <h1 itemprop="name" class="vcard foaf-person fn" <#if !editable>style="float:left;border-right:1px solid #A6B1B0;"</#if>> 
                    <#-- Label -->
                    <@p.label individual editable labelCount localesCount/>
                </h1>
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
                        <#if !editable >
                            <div id="titleContainer"><span itemprop="jobTitle" class="display-title-not-editable">${statement.preferredTitle}</span></div>
                        <#else>
                            <span itemprop="jobTitle" class="display-title-editable">${statement.preferredTitle}</span>
                            <@p.editingLinks "${title.localName}" "${title.name}" statement editable title.rangeUri />
                        </#if>
                    </#list>
                </#if>
                <#-- If preferredTitle is unpopulated, display mostSpecificTypes -->
                <#if ! (title.statements)?has_content>
                    <@p.mostSpecificTypesPerson individual editable/>
                </#if>
            </#if>        
            <span id="iconControlsRightSide" class="<#if editable>iconControlsEditable<#else>iconControlsNotEditable</#if>" <#if !user.hasSiteAdminAccess>style="top:5px"</#if>>
                <#include "individual-iconControls.ftl">
            </span>
            <#if editable && profilePageTypesEnabled >
                <#include "individual-profilePageTypes.ftl">
            </#if>
        </header>     
        <!-- Positions -->
        <#include "individual-positions.ftl">
        
        <!-- Overview -->
        <#if !editable>
            <p></p>
        </#if>
        <#include "individual-overview.ftl">
        
        <!-- Research Areas -->
        <#include "individual-researchAreas.ftl">

        <!-- Geographic Focus -->
        <#include "individual-geographicFocus.ftl">

        <!-- Contact and Webpages -->
        <div id="contactsWebpages">
            <div id="contactContainer" >
                <#include "individual-contactInfo-2column.ftl">
            </div> <!-- contactContainer -->
            <div id="webpagesContainer">
                <#assign webpage = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#URL")!>
                <#if webpage?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
                    <h2 id="webpage" class="mainPropGroup">${i18n().websites} <@p.addLink webpage editable ""/></h2>
                    <@p.verboseDisplay webpage />
                    <#assign localName = webpage.localName>
                    <ul id="individual-${localName}" role="list">
                        <@p.objectProperty webpage editable />
                    </ul>
                </#if>
            </div> <!-- webpagesContainer -->
        </div> <!-- contactsWebpages -->
        <#include "individual-openSocial.ftl">
    </section> <!-- end individual-info -->
</span></section> <!-- end individual-intro -->

<#assign nameForOtherGroup = "${i18n().other}">
<#if !editable>
	<#-- We don't want to see the first name and last name unless we might edit them. -->
	<#assign skipThis = propertyGroups.pullProperty("http://xmlns.com/foaf/0.1/firstName")!> 
	<#assign skipThis = propertyGroups.pullProperty("http://xmlns.com/foaf/0.1/lastName")!> 
</#if>

<#include "individual-property-group-tabs.ftl">

<#if profilePageTypesEnabled && (targetedView?has_content || user.loggedIn) >
<span id="quickViewLink" >
    <a href="${urls.base}/display/${individual.localName}?destination=quickView" >
        <img id="quickViewIcon" src="${urls.images}/individual/quickViewIcon.png" alt="${i18n().quick_view_icon}"/>
    </a>
</span>
</#if>
<#if !editable>
<script>
    var title = $('div#titleContainer').width();
    var name = $('h1.vcard').width();
    var total = parseInt(title,10) + parseInt(name,10);
    if ( name < 280 && total > 600 ) {
        var diff = total - 600;
        $('div#titleContainer').width(title - diff);
    }
    else if ( name > 279 && name + title > 600 ) {
        $('div#titleContainer').width('620');
    }
</script>
</#if>
<script>
    var imagesPath = '${urls.images}';
</script>
<#assign rdfUrl = individual.rdfUrl>

<#if rdfUrl??>
    <script>
        var individualRdfUrl = '${rdfUrl}';
    </script>
</#if>
<script type="text/javascript">
var individualUri = '${individual.uri!}';
var individualPhoto = '${individual.thumbNail!}';
var exportQrCodeUrl = '${urls.base}/qrcode?uri=${individual.uri!}';
var baseUrl = '${urls.base}';
var profileTypeData = {
    processingUrl: '${urls.base}/edit/primitiveRdfEdit',
    individualUri: '${individual.uri!}',
    defaultProfileType: '${profileType!}'
};
var i18nStrings = {
    errorProcessingTypeChange: '${i18n().error_processing_type_change}',
    displayLess: '${i18n().display_less}',
    displayMoreEllipsis: '${i18n().display_more_ellipsis}',
    showMoreContent: '${i18n().show_more_content}',
    verboseTurnOff: '${i18n().verbose_turn_off}',
    standardviewTooltipOne: '${i18n().standardview_tooltip_one}',
    standardviewTooltipTwo: '${i18n().standardview_tooltip_two}',
    researchAreaTooltipOne: '${i18n().research_area_tooltip_one}',
    researchAreaTooltipTwo: '${i18n().research_area_tooltip_two}'
};
var i18nStringsUriRdf = {
    shareProfileUri: '${i18n().share_profile_uri}',
    viewRDFProfile: '${i18n().view_profile_in_rdf}',
    closeString: '${i18n().close}'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/individual/individual-vivo.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/individual/individual-2column-view.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/json2.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.truncator.js"></script>')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualQtipBubble.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualUtils.js?vers=1.5.1"></script>',
			  '<script type="text/javascript" src="${urls.base}/js/individual/moreLessController.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualProfilePageType.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>')}
