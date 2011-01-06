<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for profile page for individuals of type foaf:Person -->

<#import "lib-list.ftl" as l>
<#import "lib-properties.ftl" as p>
<#assign core = "http://vivoweb.org/ontology/core#">

<#assign editingClass>
    <#if editStatus.showEditLinks>editing<#else></#if>
</#assign>

<#if editStatus.showAdminPanel>
    <#include "individual-adminPanel.ftl">
</#if>

<#assign propertyGroups = individual.propertyList>
    
<section id="individual-intro-person" class="vcard" role="region">

    <section id="share-contact" role="region"> 
        
        <#-- Thumbnail -->
        <#if individual.thumbUrl??>
            <a href="${individual.imageUrl}"><img class="individual-photo2" src="${individual.thumbUrl}" title="click to view larger image" alt="${individual.name}" width="115" /></a>
        <#elseif individual.person>
            <img class="individual-photo2" src="${urls.images}/placeholders/person.thumbnail.jpg" title = "no image" alt="placeholder image" width="115" />                                                        
        </#if>
        
        <nav role="navigation">
            <ul id ="individual-tools-people" role="list">
                <li role="listitem"><a class="picto-font picto-uri" href="#">j</a></li>
                <#--<li role="listitem"><a class="picto-font picto-pdf" href="#">F</a></li>-->
                <li role="listitem"><a class="picto-font picto-share" href="#">R</a></li>
                <li role="listitem"><a class="icon-rdf" href="#">RDF</a></li>
            </ul>
        </nav>
            
        <#-- Email -->    
        <#assign email = propertyGroups.getPropertyAndRemoveFromList("${core}email")!>
        <#if email?has_content>
            <ul id="individual-email" role="list">
                <#list email.statements as statement>
                    <li role="listitem"><a class="email" href="#"><span class ="picto-font  picto-email">M</span> ${statement.value}</a></li>
                </#list>
            </ul>
        </#if>
          
        <#-- Phone --> 
        <#assign phone = propertyGroups.getPropertyAndRemoveFromList("${core}phoneNumber")!>
        <#if phone?has_content>
            <ul id="individual-phone" role="list">
                <#list phone.statements as statement>
                    <li role="listitem"><a class="tel" href="#"><img class ="icon-phone" src="${urls.images}/individual/phone-icon.gif" alt="phone icon" />${statement.value}</a></li>
                </#list>
            </ul>
        </#if>      
                
        <#-- Links -->
        <nav role="navigation">
            <ul id ="individual-urls-people" role="list">
                <#list individual.links as link>                               
                    <li role="listitem"><a href="${link.url}">${link.anchor}</a></li>                                 
                </#list>         
            </ul>
        </nav>
    </section>
    
    <section id="individual-info" role="region">
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}">&larr; return to ${relatedSubject.name}</a></p>                
            <#else>                
                <h1 class="fn foaf-person">
                    <#-- Label -->
                    ${individual.name}
                        
                    <#-- Moniker -->
                    <#if individual.moniker?has_content>
                        <span class="preferred-title">${individual.moniker}</span>                  
                    </#if>
                </h1>
            </#if>
               
            <#-- Positions -->
            <#assign positions = propertyGroups.getPropertyAndRemoveFromList("${core}personInPosition")!>
            <#if positions?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
                <h2>Positions</h2>
                <ul id ="individual-positions" role="list">
                    <@p.objectPropertyList positions.statements positions.template />
                </ul>
            </#if>
        </header>
         
        <#-- Overview -->
        <#assign overview = propertyGroups.getPropertyAndRemoveFromList("${core}overview")!> 
        <#if overview?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
            <#list overview.statements as statement>
                <p class="individual-overview">${statement.value}</p>
            </#list>
        </#if>
        
        <#-- Research Areas -->
        <#assign researchAreas = propertyGroups.getPropertyAndRemoveFromList("${core}hasResearchArea")!> 
        <#if researchAreas?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
            <h2>Research Areas</h2>                   
            <ul id="individual-areas" role="list">
                <@p.simpleObjectPropertyList researchAreas />
            </ul>
        </#if>
                
    </section>
</section>

<section id="publications-visualization" role="region">
    <section id="sparklines-publications" role="region">
         <#include "individual-sparklineVisualization.ftl">
    </section>
    
    <section id="co-authors" role="region">
        <header>
            <h3><span class="grey">10 </span>Co-Authors</h3>
        </header>
        
        <ul role="list">
            <li role="listitem"><a href="#"><img class="co-author" src="${urls.images}/individual/Bacall.jpg" /></a></li>
            <li role="listitem"><a href="#"><img class="co-author" src="${urls.images}/individual/Bogart.jpg" /></a></li>
            <li role="listitem"><a href="#"><img class="co-author" src="${urls.images}/individual/Gable.jpg" /></a></li>
            <li role="listitem"><a href="#"><img class="co-author" src="${urls.images}/individual/Grant.jpg" /></a></li>
            <li role="listitem"><a href="#"><img class="co-author" src="${urls.images}/individual/Leigh.jpg" /></a></li>
            <li role="listitem"><a href="#"><img class="co-author" src="${urls.images}/individual/Welles.jpg" /></a></li>
        </ul>
        
        <p class="view-all-coauthors"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
    </section>
</section>

<#-- Property group menu -->
<#assign nameForOtherGroup = "other">
<#include "individual-propertyGroupMenu.ftl">

<#-- Ontology properties -->
<#include "individual-properties.ftl">

<#-- Keywords -->
<#if individual.keywords?has_content>
    <p id="keywords">Keywords: ${individual.keywordString}</p>
</#if>

${stylesheets.add("/css/individual/individual.css")}
                           
<#-- RY Figure out which of these scripts really need to go into the head, and which are needed at all (e.g., tinyMCE??) -->
${headScripts.add("/js/jquery_plugins/getUrlParam.js",                  
                  "/js/jquery_plugins/colorAnimations.js",
                  "/js/jquery_plugins/jquery.form.js",
                  "/js/tiny_mce/tiny_mce.js", 
                  "/js/controls.js",
                  "/js/toggle.js")}
                  
${scripts.add("/js/imageUpload/imageUploadUtils.js")}