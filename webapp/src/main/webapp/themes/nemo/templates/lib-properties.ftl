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

<#-- Return true iff there are statements for this property -->
<#function hasVisualizationStatements propertyGroups propertyName rangeUri>

    <#local property = propertyGroups.getProperty(propertyName, rangeUri)!>
    
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

<#macro dataPropertyListing property editable>
	<#if property?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
		<@addLinkWithLabel property editable />
		<@dataPropertyList property editable />
	</#if>
</#macro>


<#macro dataPropertyList property editable template=property.template>
	<#list property.statements as statement>
		<@propertyListItem property statement editable ><#include "${template}"></@propertyListItem>
	</#list>
</#macro>


<#macro objectProperty property editable template=property.template>
	<#if property.collatedBySubclass> <#-- collated -->
		<@collatedObjectPropertyList property editable template />
	<#else> <#-- uncollated -->
		<#-- 
			We pass property.statements and property.template even though we are also
			passing property, because objectPropertyList can get other values, and
			doesn't necessarily use property.statements and property.template 
		-->
		<@objectPropertyList property editable property.statements template />
	</#if>
</#macro>


<#macro collatedObjectPropertyList property editable template=property.template >
	<#local subclasses = property.subclasses>
	<#list subclasses as subclass>
		<#local subclassName = subclass.name!>
		<#if subclassName?has_content>
			<#if property.template == "propStatement-authorInAuthorship.ftl">
				<h4>${subclassName?capitalize}</h4>
				<button type="button" class="btn btn-default" data-toggle="collapse" data-target="#publicationFilterDiv">
					Filter
				</button>
				<div class="collapse" id="publicationFilterDiv">
					<div class="form-group">
						<input type="text" id="publicationFilter" placeholder="Narrow results..." class="form-control" aria-label="Filter publications">
					</div>
				</div>
				<table class="table table-hover">
					<th>Year</th>
					<th>Title</th>
					<th>Altmetric</th>
					<@objectPropertyList property editable subclass.statements template />
				</table>
			<#elseif property.template == "propStatement-organizationForPosition.ftl">
				<div class="panel panel-default">
				  <div class="panel-body bg-success"><h4>${subclassName?capitalize}</h4></div>
				</div>
				<div class="panel-group">
					<@objectPropertyList property editable subclass.statements template />
				</div>
			<#else> ${property.template}
				<ul class="list-group">
					<@objectPropertyList property editable subclass.statements template />
				</ul>
			</#if>
          <#--  </li> -->
		<#else>
			<#-- 
				If not in a real subclass, the statements are in a dummy subclass with an
				empty name. List them in the top level ul, not nested. 
			-->
			<@objectPropertyList property editable subclass.statements template/>
		</#if>
    </#list>
</#macro>


<#-- 
	Full object property listing, including heading and ul wrapper element. 
	Assumes property is non-null. 
-->
<#macro objectPropertyListing property editable template=property.template>
	<#local localName = property.localName>
	<h4 id="${localName}" class="mainPropGroup" title="${property.publicDescription!}">${property.name?capitalize} <@addLink property editable /> <@verboseDisplay property /></h4>
	<div id="individual-${localName}" role="list">
		<@objectProperty property editable />
	</div>
</#macro>


<#macro objectPropertyList property editable statements=property.statements template=property.template>
	<#if property.template == "propStatement-authorInAuthorship.ftl" >
		<#list statements as statement>
			<@propertyListItemTable property statement editable><#include "${template}"></@propertyListItemTable>
		</#list>
	<#elseif property.template == "propStatement-organizationForPosition.ftl" >
		<#list statements as statement>
			<@propertyListItem property statement editable><#include "${template}"></@propertyListItem>
		</#list>
	<#else>
		<#list statements as statement>
			<@propertyListItem property statement editable><#include "${template}"></@propertyListItem>
		</#list>
	</#if>
</#macro>


<#-- 
	Some properties usually display without a label. But if there's an add link, 
	we need to also show the property label. If no label is specified, the property
	name will be used as the label. 
-->
<#macro addLinkWithLabel property editable label="${property.name?capitalize}">
	<#local addLink><@addLink property editable label /></#local>
	<#local verboseDisplay><@verboseDisplay property /></#local>
	<#-- 
		Changed to display the label when user is in edit mode, even if there's no add link (due to 
		displayLimitAnnot, for example). Otherwise the display looks odd, since neighboring 
		properties have labels. 
		<#if addLink?has_content || verboseDisplay?has_content>
			<h4 id="${property.localName}" title="${property.publicDescription!}">${label}  ${addLink!} ${verboseDisplay!}</h4>         
		</#if>
	-->
	<#if editable> 
		<h4 id="${property.localName!}" title="${property.publicDescription!}">${label}  ${addLink!} ${verboseDisplay!}</h4>
	</#if>
</#macro>


<#macro addLink property editable label="${property.name}">    
	<#if property.rangeUri?? >
		<#local rangeUri = property.rangeUri />
	<#else>
		<#local rangeUri = "" />
	</#if>
	<#if property.domainUri?? >
		<#local domainUri = property.domainUri />
	<#else>
		<#local domainUri = "" />
	</#if>
	<#if editable>
		<#if property.addUrl?has_content>
			<#local url = property.addUrl>
			<@showAddLink property.localName label url rangeUri domainUri/>
		</#if>
	</#if>
</#macro>


<#macro showAddLink propertyLocalName label url rangeUri domainUri="">
	<#if (rangeUri?contains("Authorship") && domainUri?contains("IAO_0000030")) || (rangeUri?contains("Editorship") && domainUri?contains("IAO_0000030"))|| rangeUri?contains("URL") || propertyLocalName == "hasResearchArea">
		<a class="add-${propertyLocalName}" href="${url}" title="${i18n().manage_list_of} ${label?lower_case}">
		<img class="add-individual" src="${urls.images}/individual/manage-icon.png" alt="${i18n().manage}" /></a>
	<#else>
		<a class="add-${propertyLocalName}" href="${url}" title="${i18n().add_new} ${label?lower_case} ${i18n().entry}">
		<img class="add-individual" src="${urls.images}/individual/addIcon.gif" alt="${i18n().add}" /></a>
	</#if>
</#macro>


<#macro propertyLabel property label="${property.name?capitalize}">
	<h4 id="${property.localName}" title="${property.publicDescription!}">${label}  <@verboseDisplay property /></h4>
</#macro>


<#macro propertyListItemTable property statement editable >
	<#if property.rangeUri?? >
		<#local rangeUri = property.rangeUri /> 
	<#else>
		<#local rangeUri = "" /> 
	</#if>
	<tr class="publicationTableRow" role="listitem">
		<#nested>
		<@editingLinks "${property.localName}" "${property.name}" statement editable rangeUri/>
	</tr>
</#macro>


<#macro propertyListItem property statement editable >
	<#if property.rangeUri?? >
		<#local rangeUri = property.rangeUri />
	<#else>
		<#local rangeUri = "" />
	</#if>
	<li class="list-group-item" role="listitem" style="background: transparent;">
		<#nested> 
		<@editingLinks "${property.localName}" "${property.name}" statement editable rangeUri/>
	</li>
</#macro>


<#macro editingLinks propertyLocalName propertyName statement editable rangeUri="">
	<#if editable >
		<#if (!rangeUri?contains("Authorship") && !rangeUri?contains("URL") && !rangeUri?contains("Editorship") && propertyLocalName != "hasResearchArea")>
			<@editLink propertyLocalName propertyName statement rangeUri/>
			<@deleteLink propertyLocalName propertyName statement rangeUri/>
		</#if>    
	</#if>
</#macro>


<#macro editLink propertyLocalName propertyName statement rangeUri="">
	<#if propertyLocalName?contains("ARG_2000028")>
		<#if rangeUri?contains("Address")>
			<#local url = statement.editUrl + "&addressUri=" + "${statement.address!}">
		<#elseif rangeUri?contains("Telephone") || rangeUri?contains("Fax")>
			<#local url = statement.editUrl + "&phoneUri=" + "${statement.phone!}">
		<#elseif rangeUri?contains("Work") || rangeUri?contains("Email")>
			<#local url = statement.editUrl + "&emailUri=" + "${statement.email!}">
		<#elseif rangeUri?contains("Name")>
			<#local url = statement.editUrl + "&fullNameUri=" + "${statement.fullName!}">
		<#elseif rangeUri?contains("Title")>
			<#local url = statement.editUrl + "&titleUri=" + "${statement.title!}">
		</#if>
	<#else>
		<#local url = statement.editUrl>
	</#if>
	<#if url?has_content>
		<@showEditLink propertyLocalName url />
	</#if>
</#macro>


<#macro showEditLink propertyLocalName url>
	<a class="edit-${propertyLocalName}" href="${url}" title="${i18n().edit_entry}"><img class="edit-individual" src="${urls.images}/individual/editIcon.gif" alt="${i18n().edit_entry}" /></a>
</#macro>


<#macro deleteLink propertyLocalName propertyName statement rangeUri=""> 
	<#local url = statement.deleteUrl>
	<#if url?has_content>
	<#--We need to specify the actual object to be deleted as it is different from the object uri-->
	    <#if propertyLocalName?contains("ARG_2000028")>
		    <#if rangeUri?contains("Address")>
		        <#local url = url + "&deleteObjectUri=" + "${statement.address!}">
		    <#elseif rangeUri?contains("Telephone") || rangeUri?contains("Fax")>
		        <#local url = url + "&deleteObjectUri=" + "${statement.phone!}">
		    <#elseif rangeUri?contains("Work") || rangeUri?contains("Email")>
		        <#local url = url + "&deleteObjectUri=" + "${statement.email!}">
		    <#elseif rangeUri?contains("Name")>
		        <#local url = url + "&deleteObjectUri=" + "${statement.fullName!}">
		    <#elseif rangeUri?contains("Title")>
		        <#local url = url + "&deleteObjectUri=" + "${statement.title!}">
		    </#if>
		</#if>
        <@showDeleteLink propertyLocalName url />
    </#if>
</#macro>


<#macro showDeleteLink propertyLocalName url>
	<a class="delete-${propertyLocalName}" href="${url}" title="${i18n().delete_entry}"><img class="delete-individual" src="${urls.images}/individual/deleteIcon.gif" alt="${i18n().delete_entry}" /></a>
</#macro>


<#macro verboseDisplay property>
    <#local verboseDisplay = property.verboseDisplay!>
    <#if verboseDisplay?has_content>       
        <section class="verbosePropertyListing">
            <#if verboseDisplay.fauxProperty?has_content>
                 <a class="propertyLink" href="${verboseDisplay.fauxProperty.propertyEditUrl}" title="${i18n().name}">
                 ${verboseDisplay.fauxProperty.displayName}</a>
                 is a faux property of
            </#if>
            <a class="propertyLink" href="${verboseDisplay.propertyEditUrl}" title="${i18n().name}">${verboseDisplay.localName}</a> 
            (<span>${property.type?lower_case}</span> property);
            order in group: <span>${verboseDisplay.displayRank}</span>;
            display level: <span>${verboseDisplay.displayLevel}</span>;
            update level: <span>${verboseDisplay.updateLevel}</span>;
            publish level: <span>${verboseDisplay.publishLevel}</span>
        </section>
    </#if>
</#macro>

<#-----------------------------------------------------------------------------
    Macros for specific properties
------------------------------------------------------------------------------>

<#-- 
	Image 
     Values for showPlaceholder: "always", "never", "with_add_link" 
     Note that this macro has a side-effect in the call to propertyGroups.pullProperty().
-->
<#macro image individual propertyGroups namespaces editable showPlaceholder="never" imageWidth=190 >
    <#local mainImage = propertyGroups.pullProperty("${namespaces.vitroPublic}mainImage")!>
    <#local thumbUrl = individual.thumbUrl!>
    <#-- Don't assume that if the mainImage property is populated, there is a thumbnail image (though that is the general case).
         If there's a mainImage statement but no thumbnail image, treat it as if there is no image. -->
    <#if (mainImage.statements)?has_content && thumbUrl?has_content>
        <a href="${individual.imageUrl}" title="${i18n().alt_thumbnail_photo}">
        	<img class="img-rounded" src="${thumbUrl}" title="${i18n().click_to_view_larger}" alt="${individual.name}" width="${imageWidth!}" />
        </a>
        <@editingLinks "${mainImage.localName}" "" mainImage.first() editable />
    <#else>
        <#local imageLabel><@addLinkWithLabel mainImage editable "${i18n().photo}" /></#local>
        ${imageLabel}
        <#if showPlaceholder == "always" || (showPlaceholder="with_add_link" && imageLabel?has_content)>
            <img class="img-rounded" src="${placeholderImageUrl(individual.uri)}" title = "${i18n().no_image}" alt="${i18n().placeholder_image}" width="${imageWidth!}" />
        </#if>
    </#if>
</#macro>


<#-- Label -->
<#macro label individual editable labelCount localesCount=1 languageCount=1>
	<#assign labelPropertyUri = ("http://www.w3.org/2000/01/rdf-schema#label"?url) />
	<#assign useEditLink = false />
	<#--edit link used if in edit mode and only one label and one language-->
	<#--locales count may be 0 in case where no languages/selectable locales are specified-->
	<#if labelCount = 1 &&  editable && (localesCount >= 0) >
		<#assign useEditLink = true/>
	</#if>
    <#local label = individual.nameStatement>
    ${label.value}
    <#if useEditLink>
    	<@editingLinks "label" "" label editable ""/>
    <#elseif (editable && (labelCount > 0)) || (languageCount > 1)>
    	<#--We display the link even when the user is not logged in case of multiple labels with different languages-->
    	<#assign labelLink = ""/>
    	<#-- Manage labels now goes to generator -->
    	<#assign individualUri = individual.uri!""/>
    	<#assign individualUri = (individualUri?url)/>
    	<#assign individualProfileUrl = individual.profileUrl />
    	<#assign profileParameters = individualProfileUrl?substring(individualProfileUrl?index_of("?") + 1)/>
    	<#assign extraParameters = ""/>
    	<#if profileParameters?contains("uri=")>
    		<#assign extraParameters = profileParameters?replace("uri=" + individualUri, "") />
    	</#if>
    	<#--IF there are special parameters, then get those-->
    	<#if editable>
    		<#assign imageAlt = "${i18n().manage}" />
    		<#assign linkTitle = "${i18n().manage_list_of_labels}">
    		<#assign labelLink= "${urls.base}/editRequestDispatch?subjectUri=${individualUri}&editForm=edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.ManageLabelsGenerator&predicateUri=${labelPropertyUri}${extraParameters}">
    	<#else>
			<#assign linkTitle = "${i18n().view_list_of_labels}">
			<#assign imageAlt = "${i18n().view}" /> 
			<#assign labelLink= "${urls.base}/viewLabels?subjectUri=${individualUri}${extraParameters}">
    	</#if>
    	
        <span class="inline">
            <a class="add-label" href="${labelLink}"
             title="${linkTitle}">
        	<img class="add-individual" src="${urls.images}/individual/manage-icon.png" alt="${imageAlt}" /></a>
        </span>
    </#if>
</#macro>


<#-- Most specific types -->
<#macro mostSpecificTypes individual >
    <#list individual.mostSpecificTypes as type>
        <span class="label label-info">${type}</span>
    </#list>
</#macro>

<#macro mostSpecificTypesPerson individual editable>
    <#list individual.mostSpecificTypes as type>
        <div id="titleContainer"><span class="<#if editable>display-title-editable<#else>display-title-not-editable</#if>">${type}</span></div>
    </#list>
</#macro>



<#--Property group names may have spaces in them, replace spaces with underscores for html id/hash-->
<#function createPropertyGroupHtmlId propertyGroupName>
	<#return propertyGroupName?replace(" ", "_")>
</#function>

