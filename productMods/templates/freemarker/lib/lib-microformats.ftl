<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Macros used to insert microformats into non-foaf person profiles via the individual-vitro.ftl template. -->
<#macro sectionSchema individual>
    <#if individual.organization() >
		<#list individual.mostSpecificTypes as type >
			<#assign mst = type />
			<#break>
		</#list>
        <@getItemType mst "organization"/>
    <#elseif individual.event()>
        itemscope itemtype="http://schema.org/Event"
	<#elseif individual.infoContentEntity()>
	   <#assign mst = "Document" />
		<#list individual.mostSpecificTypes as type >
			<#assign mst = type />
			<#break>
		</#list>
    	<@getItemType mst "infoContentEntity"/> 
    <#else>
        itemscope itemtype="http://schema.org/Thing"
    </#if>
</#macro>

<#macro getItemType type class >
	<#-- The itemscope and full itemtype element and value are included here because Freemarker appends leading
		 and trailing white space to the returned value. -->
	<#switch type>
	  <#case "Article">
		itemscope itemtype="http://schema.org/Article"
		<#break>
	  <#case "Academic Article">
		itemscope itemtype="http://schema.org/ScholarlyArticle"
		<#break>
	  <#case "Blog">
		itemscope itemtype="http://schema.org/Blog"
		<#break>
	  <#case "Blog Posting">
		itemscope itemtype="http://schema.org/BlogPosting"
		<#break>
	  <#case "Book">
		itemscope itemtype="http://schema.org/Book"
		<#break>
	  <#case "Dataset">
		itemscope itemtype="http://schema.org/Dataset"
		<#break>
	  <#case "Periodical">
		itemscope itemtype="http://schema.org/Periodical"
		<#break>
	  <#case "Review">
		itemscope itemtype="http://schema.org/Review"
		<#break>
	  <#case "Series">
		itemscope itemtype="http://schema.org/Series"
		<#break>
	  <#case "Webpage">
		itemscope itemtype="http://schema.org/WebPage"
		<#break>
	  <#case "Website">
		itemscope itemtype="http://schema.org/WebSite"
		<#break>
	  <#case "College">
		itemscope itemtype="http://schema.org/CollegeOrUniversity"
		<#break>
	  <#case "University">
		itemscope itemtype="http://schema.org/CollegeOrUniversity"
		<#break>
	  <#default>
		<#if class == "organization" >
			itemscope itemtype="http://schema.org/Organization"
		<#else>
			<#-- don't return anything if the info content entity has no matching subclass -->
		</#if>
	</#switch>
</#macro>