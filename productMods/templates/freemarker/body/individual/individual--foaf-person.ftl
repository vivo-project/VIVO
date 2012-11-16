<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Individual profile page template for foaf:Person individuals -->
<#include "individual-setup.ftl">
<#import "individual-qrCodeGenerator.ftl" as qr>
<#import "lib-vivo-properties.ftl" as vp>
<#if !labelCount??>
    <#assign labelCount = 0 >
</#if>
<section id="individual-intro" class="vcard person" role="region">

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

        <div id="photo-wrapper">${individualImage}</div>
    
        <nav role="navigation">
        
            <ul id ="individual-tools-people" role="list">
                <li role="listitem"><img id="uriIcon" title="${individual.uri}" class="middle" src="${urls.images}/individual/uriIcon.gif" alt="uri icon"/></li>
                
                <@qr.renderCode />
            </ul>
        </nav>
            
            <#include "individual-contactInfo.ftl">  
                
        <#-- Links -->  
        <@vp.webpages propertyGroups editable "individual-urls-people" />
    </section>

    <section id="individual-info" ${infoClass!} role="region"> 
        <#include "individual-visualizationFoafPerson.ftl">    
        
        <#include "individual-adminPanel.ftl">
        
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}" title="return to">&larr; return to ${relatedSubject.name}</a></p>
            <#else>                
                <h1 class="vcard foaf-person">
                    <#-- Label -->
                    <span class="fn"><@p.label individual editable labelCount/></span>

                    <#--  Display preferredTitle if it exists; otherwise mostSpecificTypes -->
                    <#assign title = propertyGroups.pullProperty("${core}preferredTitle")!>
                    <#if title?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
                        <@p.addLinkWithLabel title editable />
                        <#list title.statements as statement>
                            <span class="display-title">${statement.value}</span>
                            <@p.editingLinks "${title.name}" statement editable />
                        </#list>
                    </#if>
                    <#-- If preferredTitle is unpopulated, display mostSpecificTypes -->
                    <#if ! (title.statements)?has_content>
                        <@p.mostSpecificTypes individual />
                    </#if>                        
                </h1>
            </#if>
               
            <#-- Positions -->
            <#assign positions = propertyGroups.pullProperty("${core}personInPosition")!>
            <#if positions?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
                <@p.objectPropertyListing positions editable />
            </#if> 
        </header>
         
        <#-- Overview -->
        <#include "individual-overview.ftl">
        
        <#-- Research Areas -->
        <#assign researchAreas = propertyGroups.pullProperty("${core}hasResearchArea")!> 
        <#if researchAreas?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
            <@p.objectPropertyListing researchAreas editable />
        </#if>   

		<#-- VIVO OpenSocial Extension by UCSF -->
		<#if openSocial??>
			<#if openSocial.visible>
			    <div id="openSocial">
			        <h2>OpenSocial</h2>
				    <#-- It would likely make sense to remove the #if logic as it is safe and -->
				    <#-- arguably better to just have both divs in all conditions -->
				    <#if editable>								  
	            	    <div id="gadgets-edit" class="gadgets-gadget-parent"></div>
	                <#else>
	            	    <div id="gadgets-view" class="gadgets-gadget-parent" ></div>
	                </#if>
	            </div>
            </#if>	
		</#if>
    </section>
    
</section>

<#assign nameForOtherGroup = "other"> <#-- used by both individual-propertyGroupMenu.ftl and individual-properties.ftl -->

<#-- Property group menu -->
<#-- With release 1.6 the property group is no longer used. The include statement
     remains in the event a particular VIVO site still wants to use it with the new
     collapsible groups.
     
     <#include "individual-propertyGroupMenu.ftl">
-->
<#-- Ontology properties -->
<#if !editable>
	<#-- We don't want to see the first name and last name unless we might edit them. -->
	<#assign skipThis = propertyGroups.pullProperty("http://xmlns.com/foaf/0.1/firstName")!> 
	<#assign skipThis = propertyGroups.pullProperty("http://xmlns.com/foaf/0.1/lastName")!> 
</#if>

<#include "individual-properties.ftl">

<#assign rdfUrl = individual.rdfUrl>

<#if rdfUrl??>
    <script>
        var individualRdfUrl = '${rdfUrl}';
    </script>
</#if>
<script>
    var individualLocalName = "${individual.localName}";
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/individual/individual-vivo.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/individual/individual-property-groups.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/amplify/amplify.store.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.truncator.js"></script>')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/propertyGroupControls.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>')}
              
