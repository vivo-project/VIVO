<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Default VIVO individual profile page template (extends individual.ftl in vitro) -->

<#include "individual-setup.ftl">
<#import "lib-vivo-properties.ftl" as vp>

<#assign altmetricBadgeType>medium-donut</#assign>
<#assign altmetricDetails>left</#assign>

<#assign individualProductExtensionPreHeader>
    <#include "individual-altmetric.ftl">
    <#include "individual-publons.ftl">
</#assign>

<#assign individualProductExtension>
    <#-- Include for any class specific template additions -->
    ${classSpecificExtension!}
    ${departmentalGrantsExtension!}
    <!--PREINDIVIDUAL OVERVIEW.FTL-->
    <#include "individual-vocabularyService.ftl">
    <#include "individual-webpage.ftl">
    <#include "individual-overview.ftl">
    ${affiliatedResearchAreas!}
        </section> <!-- #individual-info -->
    </section> <!-- #individual-intro -->
    <!--postindividual overiew ftl-->
</#assign>

<#if individual.conceptSubclass() >
    <#assign overview = propertyGroups.pullProperty("http://www.w3.org/2004/02/skos/core#broader")!>
    <#assign overview = propertyGroups.pullProperty("http://www.w3.org/2004/02/skos/core#narrower")!>
    <#assign overview = propertyGroups.pullProperty("http://www.w3.org/2004/02/skos/core#related")!>
</#if>

<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-microformats.ftl" as mf>

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
  <div class="row content">
    <div class="col-sm-9">
<#-- Default individual profile page template -->
<#--@dumpAll /-->
<section id="individual-intro" class="vcard" role="region" <@mf.sectionSchema individual/>>
    <section id="share-contact" role="region">
        <#-- Image -->
        <#assign individualImage>
        <@p.image individual=individual
            propertyGroups=propertyGroups
            namespaces=namespaces
            editable=editable
            showPlaceholder="with_add_link" />
        </#assign>

        <#if ( individualImage?contains('<img class="individual-photo"') )>
            <#assign infoClass = 'class="withThumb"'/>
        </#if>
        <div id="photo-wrapper">${individualImage}</div>
    </section>
    <!-- start section individual-info -->
    <section id="individual-info" ${infoClass!} role="region">
        <#include "individual-adminPanel.ftl">

     <#--   <#if individualProductExtensionPreHeader??>
            ${individualProductExtensionPreHeader}
        </#if> -->

        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}" title="${i18n().return_to(relatedSubject.name)}">&larr; ${i18n().return_to(relatedSubject.name)}</a></p>
            <#else>
                <h1 class="fn" itemprop="name">
                    <#-- Label -->
                    <@p.label individual editable labelCount localesCount languageCount/>

                    <#--  Most-specific types -->
                    <@p.mostSpecificTypes individual />
                    <span id="iconControlsVitro"><img id="uriIcon" title="${individual.uri}" class="middle" src="${urls.images}/individual/uriIcon.gif" alt="uri icon"/></span>
                </h1>
            </#if>
        </header>

    <#if individualProductExtension??>
        ${individualProductExtension}
    <#else>
            </section> <!-- individual-info -->
        </section> <!-- individual-intro -->
    </#if>
         <div id="readcube" class="embed-responsive embed-responsive-4by3">
            <iframe src="http://www.readcube.com/articles/10.1038/ni.3298" class="embed-responsive-item"></iframe>
         </div>
       </div>


       <div class="col-sm-3">
          <#if individualProductExtensionPreHeader??>
            ${individualProductExtensionPreHeader}
          </#if>

         <#assign nameForOtherGroup = "${i18n().other}">

<#include "individual-property-group-tabs.ftl">

<#assign rdfUrl = individual.rdfUrl>

<#if rdfUrl??>
    <script>
        var individualRdfUrl = '${rdfUrl}';
    </script>
</#if>
<script>
    var i18nStringsUriRdf = {
        shareProfileUri: '${i18n().share_profile_uri}',
        viewRDFProfile: '${i18n().view_profile_in_rdf}',
        closeString: '${i18n().close}'
    };
        var i18nStrings = {
            displayLess: '${i18n().display_less}',
            displayMoreEllipsis: '${i18n().display_more_ellipsis}',
            showMoreContent: '${i18n().show_more_content}',
        };

</script>



<script type="text/javascript">
    i18n_confirmDelete = "${i18n().confirm_delete}"
</script>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-vivo.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.truncator.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="https://d1bxh8uas1mnw7.cloudfront.net/assets/embed.js"></script>')}
