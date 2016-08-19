<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The "quick view" individual profile page template for foaf:Person individuals -->
<!--[if IE 7]>
<link rel="stylesheet" href="${urls.base}/css/individual/ie7-quick-view.css" />
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
<#assign individualImage>
    <@p.image individual=individual 
              propertyGroups=propertyGroups 
              namespaces=namespaces 
              editable=editable 
              showPlaceholder="always"/>
</#assign>

<#-- 
     the display in this template is driven by whether the individual has a web page,
     so set this variable now
-->
<#assign hasWebpage = false>
<#assign web = individual.propertyList.getProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#URL")!>
<#if web?? >
<#if editable >
    <#if web.first()?? >
        <#assign hasWebpage = true>
    <#else>
        <#assign hasWebpage = false>
    </#if>
<#else>
    <#if (web?size > 0) >
        <#assign hasWebpage = true>
    <#else>
        <#assign hasWebpage = false>
    </#if>
</#if>
</#if>
<section id="individual-intro" class="vcard person" role="region"><span itemscope itemtype="http://schema.org/Person">
    <section id="label-title" <#if editable>style="width:45%"</#if> >
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} ${i18n().for} ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}" title="${i18n().return_to(relatedSubject.name)}">&larr; ${i18n().return_to(relatedSubject.name)}</a></p>
            <#else> 
                <#-- Image  -->
                <div id="photo-wrapper">${individualImage}</div>
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
                        <h2 id="preferredTitle">${title.name?capitalize!}</h2>
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
                    <@p.mostSpecificTypesPerson individual editable /> 
                </#if>
            </#if>       
        </header>
    </section> <!-- end label-title  -->
    <#include "individual-adminPanel.ftl">

    <span id="iconControlsRightSide" class="<#if editable >iconControlsEditable<#else>iconControlsNotEditable</#if>">
        <#include "individual-iconControls.ftl">
    </span>
    <#if editable && profilePageTypesEnabled >
        <div id="profileTypeContainer" <#if editable>style="margin-top:22px"</#if>>
            <h2>${i18n().profile_type}</h2>
            <select id="profilePageType">
                <option value="standard" <#if profileType == "standard" || profileType == "none">selected</#if> >${i18n().standard_view}</option>
                <option value="quickView" <#if profileType == "quickView">selected</#if> >${i18n().quick_view}</option>
            </select>
        </div>
    </#if>

    <#-- 
        If this individual has a web page or pages, then we highlight them on the left-hand side
        of the profile page against a shaded background. If not, all the right-hand content shifts
        left and displays across the full width of the page.
    -->
    <#if hasWebpage >
        <section id="qv-share-contact" class="share-contact" role="region" <#if !editable>style="padding-top:12px"</#if>> 
            <img id="webpage-popout-top" src="${urls.images}/individual/webpage-popout-top.png"  alt="${i18n().background_top_image}"/>
            <div id="webpage-wrapper" >
                <#assign webpage = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#URL")!>            
                <#if webpage?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
                    <#if editable>
                        <h2 class="websites" >${i18n().websites} <@p.addLink webpage editable ""/></h2>
                    </#if>
                    <@p.verboseDisplay webpage />
                    <#assign localName = webpage.localName>
                    <ul id="individual-${localName}" class="individual-webpage" role="list">
                        <@p.objectProperty webpage editable "propStatement-webpage-quickview.ftl"/>
                    </ul>
                </#if>
            </div>
            <img id="webpage-popout-bottom" src="${urls.images}/individual/webpage-popout-bottom.png"  alt="${i18n().background_top_image}"  />
        </section> <!-- end share-contact -->
    </#if>
    <section id="individual-info" class="qv-individual-info" role="region" style=" <#if !editable>padding-top:12px;</#if><#if hasWebpage>width:53%<#else>width:100%;clear:left</#if>;">       
        <!-- Positions -->
        <#include "individual-positions.ftl">

        <!-- Research Areas -->
        <#include "individual-researchAreas.ftl">

        <!-- Geographic Focus -->
        <#assign geographicFocus = propertyGroups.pullProperty("${core}geographicFocus")!> 
        <#if geographicFocus?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
            <h2 id="geoFocus" class="mainPropGroup">${i18n().geographic_focus} <@p.addLink geographicFocus editable ""/></h2>
            <@p.verboseDisplay geographicFocus />
            <#assign localName = geographicFocus.localName>

            <#if geographicFocus.subclasses?has_content >
                <#assign subclasses = geographicFocus.subclasses>
                <#list subclasses as subclass>
                    <#assign subclassName = subclass.name!>
                    <ul id="individual-${localName}" role="list">
                        <@p.objectPropertyList geographicFocus editable subclass.statements geographicFocus.template/>
                    </ul>
                </#list>
            <#else>
                <ul id="individual-${localName}" role="list">
                    <@p.objectProperty geographicFocus editable />
                </ul>
            </#if>
        </#if>   

        <#-- If the individual does not have webpages and we're in edit mode, provide the opportunity to add webpages -->
        <#if editable && !hasWebpage >
            <!-- Webpages -->
            <#assign webpage = propertyGroups.pullProperty("http://purl.obolibrary.org/obo/ARG_2000028","http://www.w3.org/2006/vcard/ns#URL")!>            
            <#if webpage?has_content>
                <h2 id="webpage" class="mainPropGroup">${i18n().websites} <@p.addLink webpage editable ""/></h2>
                <@p.verboseDisplay webpage />
                <#assign localName = webpage.localName>
                <ul id="individual-${localName}" role="list">
                    <@p.objectProperty webpage editable />
                </ul>
            </#if>
        </#if>
        <#include "individual-visualizationQuickView.ftl">

		<#include "individual-openSocial.ftl">
    </section> <!-- end individual-info -->
</span></section> <!-- end end individual-intro -->
<!-- we need these 3 lines of html to provide proper spacing and alignment -->
<p style="clear:both">
    <br />
</p>
<span id="fullViewLink">
    <a href="${urls.base}/display/${individual.localName}?destination=standardView" >
        <img id="fullViewIcon" src="${urls.images}/individual/fullViewIcon.png" alt="${i18n().full_view_icon}"/>
    </a>
</span>
<#if !editable>
<script type="text/javascript">
    var title = $('div#titleContainer').width();
    var name = $('h1.vcard').width();
    var total = parseInt(title,10) + parseInt(name,10);
    if ( name < 400 && total > 730 ) {
        var diff = total - 730;
        $('div#titleContainer').width(title - diff);
    }
    else if ( name > 399 && name + title > 730 ) {
        $('div#titleContainer').width('720');
    }
</script>
</#if>
<script>
    var individualLocalName = "${individual.localName}";
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
    quickviewTooltip: '${i18n().quickview_tooltip}',
    researchAreaTooltipOne: '${i18n().research_area_tooltip_one}',
    researchAreaTooltipTwo: '${i18n().research_area_tooltip_two}'
};
var i18nStringsUriRdf = {
    shareProfileUri: '${i18n().share_profile_uri}',
    viewRDFProfile: '${i18n().view_profile_in_rdf}',
    closeString: '${i18n().close}'
};
</script>
<#if editable>
    <script>
        // until the web service is implemented, the bottom portion of the web page "insert"
        // will not align correctly. This fixes that issue.
        $('ul#individual-webpage li').children('a').each( function() {
            if ( $(this).attr('title') == "link text" ) {
                $('img#webpage-popout-bottom').css("margin-top","8px");
                return false;
            }
        });
    </script>
</#if>
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/individual/individual-vivo.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/individual/individual-quick-view.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/amplify/amplify.store.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/json2.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.truncator.js"></script>')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualQtipBubble.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/moreLessController.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualProfilePageType.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>')}
