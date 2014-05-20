<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
    Individual profile page template for foaf:Person individuals. This is the default template for foaf persons
    in the Wilma theme and should reside in the themes/wilma/templates directory. 
-->
 
<#include "individual-setup.ftl">
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
<#assign visRequestingTemplate = "foaf-person-wilma">

<#--add the VIVO-ORCID interface -->
<#include "individual-orcidInterface.ftl">  
        
<section id="individual-intro" class="vcard person" role="region">

    <section id="share-contact" role="region"> 
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

        <div id="photo-wrapper">${individualImage}</div>
        <!-- Contact Info -->
        <div id="individual-tools-people">
            <span id="iconControlsLeftSide">
                <img id="uriIcon" title="${individual.uri}" src="${urls.images}/individual/uriIcon.gif" alt="${i18n().uri_icon}"/>  
                <@qr.renderCode "qr_icon.png" />
            </span>
        </div>
        <#include "individual-contactInfo.ftl">  
                
        <!-- Websites -->
        <#include "individual-webpage.ftl">
    </section>

    <section id="individual-info" ${infoClass!} role="region"> 
    <section id="right-hand-column" role="region">
        <#include "individual-visualizationFoafPerson.ftl">    
        </section>
        <#include "individual-adminPanel.ftl">
        
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} ${i18n().indiv_foafperson_for} ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}" title="${i18n().indiv_foafperson_return}">&larr; ${i18n().indiv_foafperson_return} ${relatedSubject.name}</a></p>
            <#else>                
                <h1 class="vcard foaf-person">
                    <#-- Label -->
                    <span class="fn"><@p.label individual editable labelCount localesCount/></span>

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
                            <span class="display-title<#if editable>-editable</#if>">${statement.preferredTitle}</span>
                            <@p.editingLinks "${title.localName}" "${title.name}" statement editable title.rangeUri />
                        </#list>
                    </#if>
                    <#-- If preferredTitle is unpopulated, display mostSpecificTypes -->
                    <#if ! (title.statements)?has_content>
                        <@p.mostSpecificTypes individual />
                    </#if>                        
                </h1>
            </#if>
            <!-- Positions -->   
            <#include "individual-positions.ftl">
        </header>
         
        <!-- Overview -->
        <#include "individual-overview.ftl">
        
        <!-- Research Areas -->
        <#include "individual-researchAreas.ftl">

        <!-- Geographic Focus -->
        <#include "individual-geographicFocus.ftl">

		<#include "individual-openSocial.ftl">
    </section>
    
</section>

<#assign nameForOtherGroup = "${i18n().other}"> 

<#-- Ontology properties -->
<#if !editable>
	<#-- We don't want to see the first name and last name unless we might edit them. -->
	<#assign skipThis = propertyGroups.pullProperty("http://xmlns.com/foaf/0.1/firstName")!> 
	<#assign skipThis = propertyGroups.pullProperty("http://xmlns.com/foaf/0.1/lastName")!> 
</#if>

<!-- Property group menu or tabs -->
<#-- 
     With release 1.6 there are now two types of property group displays: the original property group
     menu and the horizontal tab display, which is the default. If you prefer to use the property
     group menu, simply substitute the include statement below with the one that appears after this
     comment section.
     
     <#include "individual-property-group-menus.ftl">
-->

<#include "individual-property-group-tabs.ftl">

<#assign rdfUrl = individual.rdfUrl>

<#if rdfUrl??>
    <script>
        var individualRdfUrl = '${rdfUrl}';
    </script>
</#if>
<script>
    var imagesPath = '${urls.images}';
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

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/individual/individual-vivo.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.truncator.js"></script>')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualQtipBubble.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>')}
