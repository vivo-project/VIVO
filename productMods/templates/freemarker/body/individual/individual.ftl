<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Default individual profile page template -->

<#include "individual-setup.ftl">

<#if individual.showAdminPanel>
    <#include "individual-adminPanel.ftl">
</#if>

<section id="individual-intro" class="vcard" role="region">
    <section id="share-contact" role="region"> 
        <#-- Thumbnail -->
        <#if individual.thumbUrl??>
            <a href="${individual.imageUrl}"><img class="individual-photo2" src="${individual.thumbUrl}" title="click to view larger image" alt="${individual.name}" width="115" /></a>
        <#--<#elseif individual.person>
            <img class="individual-photo2" src="${urls.images}/placeholders/person.thumbnail.jpg" title = "no image" alt="placeholder image" width="115" />-->                                                       
        </#if>
    </section>

    <section id="individual-info" role="region">
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}">&larr; return to ${relatedSubject.name}</a></p>                
            <#else>                
                <h1 class="fn">
                    <#-- Label -->
                    <#assign label = individual.nameStatement>
                    ${label.value}
                    <@p.editingLinks label editing />
                        
                    <#-- Moniker -->
                    <#if individual.moniker?has_content>
                        <span class="preferred-title">${individual.moniker}</span>                  
                    </#if>
                </h1>
            </#if>
        </header>
         
        <#-- Overview -->
        <#include "individual-overview.ftl">
        
        <nav role="navigation">
            <ul id ="individual-tools" role="list">
                <#--<li role="listitem"><a class="picto-font picto-uri" href="#">j</a></li>
                <li role="listitem"><a class="picto-font picto-pdf" href="#">F</a></li>
                <li role="listitem"><a class="picto-font picto-share" href="#">R</a></li>-->
                <#assign rdfUrl = individual.rdfUrl>
                <#if rdfUrl??>
                    <li role="listitem"><a class="icon-rdf" href="${rdfUrl}">RDF</a></li>
                </#if>
            </ul>
        </nav>
                
        <#-- Links -->
        <@p.vitroLinks propertyGroups editing  />
    </section>
</section>

<section id="publications-visualization" role="region">
    <section id="sparklines-publications" role="region">
         <#include "individual-sparklineVisualization.ftl">

         <#-- RY Will we have an individual--foaf-organization.ftl template? If so, move this there and remove from here.
         Also remove the method IndividualTemplateModel.isOrganization(). -->
         <#if individual.organization >
            <section id="temporal-graph" role="region">
                <h3><img src="${urls.images}/visualization/temporal_vis_icon.jpg" width="25px" height="25px" /><a href="${urls.base}/visualization?vis=entity_comparison&uri=${individual.uri}">Temporal Graph</a></h3>
            </section>      
            <#--<div>VISMODE: ${individual.moniker}</div>-->
        </#if>
    </section>
</section>

<#assign nameForOtherGroup = "other"> <#-- used by both individual-propertyGroupMenu.ftl and individual-properties.ftl -->

<#-- Property group menu -->
<#include "individual-propertyGroupMenu.ftl">

<#-- Ontology properties -->
<#include "individual-properties.ftl">

<#-- Keywords -->
<#if individual.keywords?has_content>
    <p id="keywords">Keywords: ${individual.keywordString}</p>
</#if>

${stylesheets.add("/css/individual/individual.css")}
                           
<#-- RY Figure out which of these scripts really need to go into the head, and which are needed at all (e.g., tinyMCE??) -->
${headScripts.add("/js/jquery_plugins/getURLParam.js",                  
                  "/js/jquery_plugins/colorAnimations.js",
                  "/js/jquery_plugins/jquery.form.js",
                  "/js/tiny_mce/tiny_mce.js", 
                  "/js/controls.js",
                  "/js/toggle.js")}
                  
${scripts.add("/js/imageUpload/imageUploadUtils.js")}