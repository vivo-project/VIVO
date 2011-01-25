<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Individual profile page template for foaf:Person individuals -->

<#include "individual-setup.ftl">
    
<section id="individual-intro-person" class="vcard" role="region">

    <section id="share-contact" role="region"> 
        <#-- Image -->
        <@p.imageLinks individual=individual 
                       propertyGroups=propertyGroups 
                       namespaces=namespaces 
                       editable=editable 
                       showPlaceholder="always" 
                       placeholder="${urls.images}/placeholders/person.thumbnail.jpg" />
    
        <nav role="navigation">
            <ul id ="individual-tools-people" role="list">
                <li role="listitem"><a title="Individual URI" href="${individual.uri}"><img class="middle" src="${urls.images}/individual/uriIcon.gif" alt="uri icon" /></a></li>
    
                <#assign rdfUrl = individual.rdfUrl>
                <#if rdfUrl??>
                    <li role="listitem"><a title="View this individual in RDF format" class="icon-rdf" href="${rdfUrl}">RDF</a></li>
                </#if>
            </ul>
        </nav>
            
        <#-- Email -->    
        <#assign email = propertyGroups.getPropertyAndRemoveFromList("${core}email")!>      
        <#if email?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
            <@p.addLinkWithLabel email editable />
            <#if email.statements?has_content> <#-- if there are any statements -->
                <ul id="individual-email" role="list">
                    <#list email.statements as statement>
                        <li role="listitem">
                            <img class ="icon-email middle" src="${urls.images}/individual/emailIcon.gif" alt="email icon" /><a class="email" href="mailto:${statement.value}">${statement.value}</a>
                            <@p.editingLinks "${email.localName}" statement editable />
                        </li>
                    </#list>
                </ul>
            </#if>
        </#if>
          
        <#-- Phone --> 
        <#assign phone = propertyGroups.getPropertyAndRemoveFromList("${core}phoneNumber")!>
        <#if phone?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
            <@p.addLinkWithLabel phone editable />
            <#if phone.statements?has_content> <#-- if there are any statements -->
                <ul id="individual-phone" role="list">
                    <#list phone.statements as statement>
                        <li role="listitem">                           
                           <img class ="icon-phone  middle" src="${urls.images}/individual/phoneIcon.gif" alt="phone icon" />${statement.value}
                            <@p.editingLinks "${phone.localName}" statement editable />
                        </li>
                    </#list>
                </ul>
            </#if>
        </#if>      
                
        <#-- Links -->  
        <@p.vitroLinks propertyGroups namespaces editable "individual-urls-people" />
    </section>

    <section id="individual-info" role="region">
        <#include "individual-sparklineVisualization.ftl">    
        <#-- Disable for now until controller sends data -->
        <#--
        <section id="co-authors" role="region">
            <header>
                <h3><span class="grey">10 </span>Co-Authors</h3>
            </header>

            <ul role="list">
                <li role="listitem"><a href="#"><img class="co-author" src="" /></a></li>
                <li role="listitem"><a href="#"><img class="co-author" src="" /></a></li>
            </ul>

            <p class="view-all-coauthors"><a class="view-all-style" href="#">View All <img src="${urls.images}/arrowIcon.gif" alt="arrow icon" /></a></p>
        </section>
        -->
        
        <#if individual.showAdminPanel>
            <#include "individual-adminPanel.ftl">
        </#if>
        
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}">&larr; return to ${relatedSubject.name}</a></p>                
            <#else>                
                <h1 class="fn foaf-person">
                    <#-- Label -->
                    <@p.label individual editable />
                        
                    <#-- Moniker -->
                    <#if individual.moniker?has_content>
                        <span class="preferred-title">${individual.moniker}</span>                  
                    </#if>
                </h1>
            </#if>
               
            <#-- Positions -->
            <#assign positions = propertyGroups.getPropertyAndRemoveFromList("${core}personInPosition")!>
            <#if positions?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
                <h2 id="${positions.localName}">${positions.name?capitalize} <@p.addLink positions editable /></h2>
                <ul id="individual-positions" role="list">
                    <@p.objectProperty positions editable />
                </ul>
            </#if>
        </header>
         
        <#-- Overview -->
        <#include "individual-overview.ftl">
        
        <#-- Research Areas -->
        <#assign researchAreas = propertyGroups.getPropertyAndRemoveFromList("${core}hasResearchArea")!> 
        <#if researchAreas?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
            <h2 id="${researchAreas.localName}">${researchAreas.name?capitalize} <@p.addLink researchAreas editable /></h2>                
            <ul id="individual-areas" role="list">
                <@p.objectProperty researchAreas editable "propStatement-simple.ftl" />
            </ul>
        </#if>
    </section>
    
</section>
<#assign nameForOtherGroup = "other"> <#-- used by both individual-propertyGroupMenu.ftl and individual-properties.ftl -->

<#-- Property group menu -->
<#include "individual-propertyGroupMenu.ftl">

<#-- Ontology properties -->
<#include "individual-properties.ftl">


${stylesheets.add("/css/individual/individual.css")}
                           
<#-- RY Figure out which of these scripts really need to go into the head, and which are needed at all (e.g., tinyMCE??) -->
${headScripts.add("/js/jquery_plugins/getURLParam.js",                  
                  "/js/jquery_plugins/colorAnimations.js",
                  "/js/jquery_plugins/jquery.form.js",
                  "/js/tiny_mce/tiny_mce.js", 
                  "/js/controls.js",
                  "/js/toggle.js",
                  "/js/jquery_plugins/jquery.truncator.js")}
                  
${scripts.add("/js/imageUpload/imageUploadUtils.js")}
${scripts.add("/js/individual/individualUtils.js")}