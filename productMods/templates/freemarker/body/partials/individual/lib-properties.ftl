<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-----------------------------------------------------------------------------
    Macros and functions for working with properties and property lists
------------------------------------------------------------------------------>

<#-- Return true iff there are statements for this property -->
<#function hasStatements propertyGroups propertyName>

    <#local property = propertyGroups.getProperty(propertyName)!>
    
    <#-- First ensure that the property is defined
    (an unpopulated property while logged out is undefined) -->
    <#if ! property?has_content>
        <#return false>
    </#if>
    
    <#if property.collatedBySubclass!false> <#-- collated object property-->
        <#return property.subclasses?has_content>
    <#else>
        <#return property.statements?has_content> <#-- data property or uncollated object property -->
    </#if>
</#function>


<#-----------------------------------------------------------------------------
    Macros for generating property lists
------------------------------------------------------------------------------>

<#macro dataPropertyList property editable>
    <#list property.statements as statement>
        <@propertyListItem property statement editable>${statement.value}</@propertyListItem>
    </#list> 
</#macro>

<#macro objectProperty property editable template=property.template>
    <#if property.collatedBySubclass> <#-- collated -->
        <@collatedObjectPropertyList property editable template />
    <#else> <#-- uncollated -->
        <#-- We pass property.statements and property.template even though we are also
             passing property, because objectPropertyList can get other values, and
             doesn't necessarily use property.statements and property.template -->
        <@objectPropertyList property editable property.statements template />
    </#if>
</#macro>

<#macro collatedObjectPropertyList property editable template=property.template>
    <#assign subclasses = property.subclasses>
    <#list subclasses?keys as subclass>
        <#if subclass?has_content>
            <li class="subclass" role="listitem">
                <h3>${subclass?lower_case}</h3>
                <ul class="subclass-property-list">
                    <@objectPropertyList property editable subclasses[subclass] template/>
                </ul>
            </li>
        <#else>
            <#-- If not in a subclass, list the statements in the top level ul, not nested -->
            <@objectPropertyList property editable subclasses[subclass] template/>
        </#if>
    </#list>
</#macro>

<#-- Full object property listing, including heading and ul wrapper element. 
Assumes property is non-null. -->
<#macro objectPropertyListing property editable template=property.template>
    <#local localName = property.localName>
    <h2 id="${localName}">${property.name?capitalize} <@addLink property editable /> <@verboseDisplay property /></h2>    
    <ul id="individual-${localName}" role="list">
        <@objectProperty property editable />
    </ul>
</#macro>

<#macro objectPropertyList property editable statements=property.statements template=property.template>
    <#list statements as statement>
        <@propertyListItem property statement editable><#include "${template}"></@propertyListItem>
    </#list>
</#macro>

<#-- Some properties usually display without a label. But if there's an add link, 
we need to also show the property label. If no label is specified, the property
name will be used as the label. -->
<#macro addLinkWithLabel property editable label="${property.name?capitalize}" extraParams="">
    <#local addLink><@addLink property editable label extraParams /></#local>
    <#local verboseDisplay><@verboseDisplay property /></#local>
    <#if addLink?has_content || verboseDisplay?has_content>
        <h2 id="${property.localName}">${label} ${addLink!} ${verboseDisplay!}</h2>         
    </#if>
</#macro>

<#macro addLink property editable label="${property.name}" extraParams="">
    <#if editable>
        <#local url = property.addUrl>
        <#if url?has_content>
            <@showAddLink property.localName label addParamsToEditUrl(url, extraParams) />
        </#if>
    </#if>
</#macro>

<#function addParamsToEditUrl url extraParams="">
    <#if extraParams?is_hash_ex>
        <#list extraParams?keys as key>
            <#local url = "${url}&${key}=${extraParams[key]?url}">
        </#list>
    </#if>
    <#return url>
</#function>

<#macro showAddLink propertyLocalName label url>
    <a class="add-${propertyLocalName}" href="${url}" title="Add new ${label?lower_case} entry"><img class="add-individual" src="${urls.images}/individual/addIcon.gif" alt="add" /></a>
</#macro>

<#macro propertyLabel property label="${property.name?capitalize}">
    <h2 id="${property.localName}">${label} <@verboseDisplay property /></h2>     
</#macro>

<#macro propertyListItem property statement editable>
    <li role="listitem">    
        <#nested>        
        <@editingLinks "${property.localName}" statement editable />
    </li>
</#macro>

<#macro editingLinks propertyLocalName statement editable extraParams="">
    <#if editable>
        <@editLink propertyLocalName statement extraParams />
        <@deleteLink propertyLocalName statement extraParams />
    </#if>
</#macro>

<#macro editLink propertyLocalName statement extraParams="">
    <#local url = statement.editUrl>
    <#if url?has_content>
        <@showEditLink propertyLocalName addParamsToEditUrl(url, extraParams) />
    </#if>
</#macro>

<#macro showEditLink propertyLocalName url>
    <a class="edit-${propertyLocalName}" href="${url}" title="edit this entry"><img class="edit-individual" src="${urls.images}/individual/editIcon.gif" alt="edit" /></a>
</#macro>

<#macro deleteLink propertyLocalName statement extraParams=""> 
    <#local url = statement.deleteUrl>
    <#if url?has_content>
        <@showDeleteLink propertyLocalName addParamsToEditUrl(url, extraParams) />
    </#if>
</#macro>

<#macro showDeleteLink propertyLocalName url>
    <a class="delete-${propertyLocalName}" href="${url}" title="delete this entry"><img  class="delete-individual" src="${urls.images}/individual/deleteIcon.gif" alt="delete" /></a>
</#macro>

<#macro verboseDisplay property>
    <#local verboseDisplay = property.verboseDisplay!>
    <#if verboseDisplay?has_content>       
        <section class="verbosePropertyListing">
            <a class="propertyLink" href="${verboseDisplay.propertyEditUrl}">${verboseDisplay.localName}</a> 
            (<span>${property.type?lower_case}</span> property);
            order in group: <span>${verboseDisplay.displayRank};</span> 
            display level: <span>${verboseDisplay.displayLevel};</span>
            update level: <span>${verboseDisplay.updateLevel}</span>
        </section>
    </#if>
</#macro>

<#-----------------------------------------------------------------------------
    Macros for specific properties
------------------------------------------------------------------------------>

<#-- Vitro namespace links

     Currently the page displays the vitro namespace links properties. Future versions 
     will use the vivo core ontology links property, eliminating the need for special handling.
     
     Note that this macro has a side-effect in the calls to propertyGroups.pullProperty().
-->
<#macro vitroLinks propertyGroups namespaces editable linkListClass="individual-urls">
    <#local primaryLink = propertyGroups.pullProperty("${namespaces.vitro}primaryLink")!>
    <#local additionalLinks = propertyGroups.pullProperty("${namespaces.vitro}additionalLink")!>

    <#if (primaryLink?has_content || additionalLinks?has_content)> <#-- true when the property is in the list, even if not populated (when editing) -->
        <nav role="navigation">
            <#local primaryLinkLabel = "Primary Web Page">            
            <#if primaryLink.statements?has_content> <#-- if there are any statements -->
                <#if editable><@propertyLabel primaryLink primaryLinkLabel /></#if>
                <ul class="${linkListClass}" id="links-primary" role="list">
                    <@objectPropertyList primaryLink editable />
                </ul>
            <#else>
                <#-- Show add link only if there isn't a primaryLink already (displayLimitAnnnot not 
                supported for object properties). -->
                <@addLinkWithLabel primaryLink editable primaryLinkLabel /> 
            </#if>
            <@addLinkWithLabel additionalLinks editable "Additional Web Pages" />
            <#if additionalLinks.statements?has_content> <#-- if there are any statements -->
                <ul class="${linkListClass}" id="links-additional" role="list">
                    <@objectPropertyList additionalLinks editable />
                </ul>
            </#if>
        </nav>
    </#if>
</#macro>

<#-- Image 

     Values for showPlaceholder: "always", "never", "with_add_link" 
     
     Note that this macro has a side-effect in the call to propertyGroups.pullProperty().
-->
<#macro image individual propertyGroups namespaces editable showPlaceholder="never" placeholder="">
    <#local mainImage = propertyGroups.pullProperty("${namespaces.vitroPublic}mainImage")!>
    <#local extraParams = "">
    <#if placeholder?has_content>
        <#local extraParams = { "placeholder" : placeholder } >
    </#if>
    <#local thumbUrl = individual.thumbUrl!>
    <#-- Don't assume that if the mainImage property is populated, there is a thumbnail image (though that is the general case).
         If there's a mainImage statement but no thumbnail image, treat it as if there is no image. -->
    <#if (mainImage.statements)?has_content && thumbUrl?has_content>
        <a href="${individual.imageUrl}"><img class="individual-photo" src="${thumbUrl}" title="click to view larger image" alt="${individual.name}" width="160" /></a>
        <@editingLinks "${mainImage.localName}" mainImage.first editable extraParams />
    <#else>
        <#local imageLabel><@addLinkWithLabel mainImage editable "Photo" extraParams /></#local>
        ${imageLabel}
        <#if placeholder?has_content>
            <#if showPlaceholder == "always" || (showPlaceholder="with_add_link" && imageLabel?has_content)>
                <img class="individual-photo" src="${placeholder}" title = "no image" alt="placeholder image" width="160" />
            </#if>
        </#if>
    </#if>
</#macro>

<#-- Label -->
<#macro label individual editable>
    <#local label = individual.nameStatement>
    ${label.value?capitalize}
    <@editingLinks "label" label editable />
</#macro>