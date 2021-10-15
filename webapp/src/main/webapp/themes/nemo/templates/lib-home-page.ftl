<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Macros used to build the statistical information on the home page -->

<#-- Get the classgroups so they can be used to qualify searches -->
<#macro allClassGroupNames classGroups>
	<#list classGroups as group>
		<#-- Only display populated class groups -->
		<#if (group.individualCount > 0)>
			<li role="listitem"><a href="" title="${group.uri}">${group.displayName?capitalize}</a></li>
		</#if>
	</#list>
</#macro>


<!-- 
	################################################################################
	Renders the html for the faculty member section on the home page.
	Works in conjunction with the homePageUtils.js file, which contains the ajax call.
	This macro has been edited to create a carousel using bootstrap, original macro commented below
	################################################################################
-->
<#macro facultyMbrHtml>
	<section id="faculty-carousel" class="carousel slide" data-ride="carousel" data-interval="4000">
		<div id="tempSpacing">
			<span>${i18n().loading_faculty}&nbsp;&nbsp;&nbsp;
				<img src="${urls.images}/indicatorWhite.gif">
			</span>
		</div>
		<div id="research-faculty-mbrs" class="carousel-inner" role="listbox">
			<!-- populated via an ajax call -->
		</div>
		<a class="left carousel-control" href="#faculty-carousel" role="button" data-slide="prev">
			<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
			<span class="sr-only">Previous</span>
		</a>
		<a class="right carousel-control" href="#faculty-carousel" role="button" data-slide="next">
			<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
			<span class="sr-only">Next</span>
		</a>
	</section>
</#macro>

<#-- Original Macro -->
<#-- 
	<#macro facultyMbrHtml>
		<section id="home-faculty-mbrs" class="home-sections">
			<h3>${i18n().faculty_capitalized}</h3>
			<div id="tempSpacing">
			<span>${i18n().loading_faculty}&nbsp;&nbsp;&nbsp;
				<img src="${urls.images}/indicatorWhite.gif">
			</span>
			</div>
			<div id="research-faculty-mbrs">
				<!-- populated via an ajax call
				<ul id="facultyThumbs">
				</ul>
			</div>
		</section>
	</#macro>
-->


<!-- 
	################################################################################
	Faculty Count
	We need the faculty count in order to randomly select faculty using a search query
	################################################################################
-->
<#macro facultyMemberCount classGroups>
	<#assign foundClassGroup = false />
	<#list classGroups as group>
		<#if (group.individualCount > 0) && group.uri?contains("people") >
			<#list group.classes as class>
				<#if (class.uri?contains("Person")) >
					<#assign foundClassGroup = true />
					<#if (class.individualCount > 0) >
						<script>var facultyMemberCount = ${class.individualCount?string?replace(",","")?replace(".","")};</script>
					<#else>
						<script>var facultyMemberCount = 0;</script>
					</#if>
				</#if>
			</#list>
		</#if>
	</#list>
	<#if !foundClassGroup>
		<script>var facultyMemberCount = 0;</script>
	</#if>
</#macro>


<!-- 
	################################################################################
	Site Statistics
	builds the "stats" section of the home page, i.e., class group counts
	################################################################################
-->
<#macro allClassGroups classGroups>
    <#-- Loop through classGroups first so we can account for situations when all class groups are empty -->
    <#assign selected = 'class="selected" ' />
    <#assign classGroupList>
        <section id="home-research" class="home-sections">
            <h4>Statistics</h4>
            <div id="home-sections-list" class="list-group">
                <#assign groupCount = 1>
                <#list classGroups as group>
                    <#if (groupCount > 6) >
                        <#break/>
                    </#if>
                    <#-- Only display populated class groups -->
                    <#if (group.individualCount > 0)>
                        <#-- Catch the first populated class group. Will be used later as the default selected class group -->
                        <#if !firstPopulatedClassGroup??>
                            <#assign firstPopulatedClassGroup = group />
                        </#if>
                        <#if !group.uri?contains("equipment") && !group.uri?contains("course") >
	                        <a href="${urls.base}/browse" 
	                        	class="list-group-item">${group.displayName?capitalize}<span class="badge">
                                    <#if (group.individualCount > 10000) >
                                        <#assign overTen = group.individualCount/1000>
                                        ${overTen?round}k
                                    <#elseif (group.individualCount > 1000)>
                                        <#assign underTen = group.individualCount/1000>
                                        ${underTen?string("0.#")}k
                                    <#else>
                                        ${group.individualCount}
                                    </#if>                        
	                        	</span>
	                        </a>
                            <#assign groupCount = groupCount + 1>
                        </#if>
                    </#if>
                </#list>
            </div>
        </section>
    </#assign>

    <#-- Display the class group browse only if we have at least one populated class group -->
    <#if firstPopulatedClassGroup??>
            ${classGroupList}
    <#else>
        <h3 id="noContentMsg">${i18n().no_content_create_groups_classes}</h3>
        
        <#if user.loggedIn>
            <#if user.hasSiteAdminAccess>
                <p>${i18n().you_can} <a href="${urls.siteAdmin}" title="${i18n().add_content_manage_site}">${i18n().add_content_manage_site}</a> ${i18n().from_site_admin_page}</p>
            </#if>
        <#else>
            <p>${i18n().please} <a href="${urls.login}" title="${i18n().login_to_manage_site}">${i18n().log_in}</a> ${i18n().to_manage_content}</p>
        </#if>
    </#if>
            
</#macro>


<!-- 
	################################################################################
	Research
	Renders the html for the research section on the home page.
	Works in conjunction with the homePageUtils.js file
	################################################################################
-->
<#macro researchClasses classGroups=vClassGroups>
<#assign foundClassGroup = false />
<section id="home-research" class="home-sections">
    <h4>${i18n().research_capitalized}</h4>
    <div id="home-sections-list" class="list-group">
        <#list classGroups as group>
            <#if (group.individualCount > 0) && group.uri?contains("publications") >
                <#assign foundClassGroup = true />
                <#list group.classes as class>
                    <#if (class.individualCount > 0) && (class.uri?contains("AcademicArticle") || class.uri?contains("Book") || class.uri?contains("Chapter") ||class.uri?contains("ConferencePaper") || class.uri?contains("Grant") || class.uri?contains("Report")) >
                            <a class="list-group-item" href='${urls.base}/individuallist?vclassId=${class.uri?replace("#","%23")!}'>
                                <#if class.name?substring(class.name?length-1) == "s">
                                    ${class.name}
                                <#else>
                                    ${class.name}s 
                                </#if>
                                <#-- Add bootstrap badge class to individual counts -->
                                &nbsp;
                                <span class="badge">${class.individualCount!}</span>
                            </a>
                    </#if>
                </#list>
                <a href="${urls.base}/research" alt="${i18n().view_all_research}">${i18n().view_all}</a>
            </#if>
        </#list>
        <#if !foundClassGroup>
            <p style="padding-left:1.2em">${i18n().no_research_content_found}</p> 
        </#if>
    </div>
</section>
</#macro>


<!-- 
	################################################################################
	Academic Departments
	Renders the html for the academic departments section on the home page.
	Works in conjunction with the homePageUtils.js file
	################################################################################
-->
<#-- Renders the html for the academic departments section on the home page. -->
<#-- Works in conjunction with the homePageUtils.js file -->
<#macro academicDeptsHtml>
    <section id="home-academic-depts" class="home-sections">
        <h4>${i18n().departments}</h4>
        <div id="academic-depts">
        </div>
    </section>        
</#macro>

<#-- builds the "academic departments" box on the home page -->
<#macro listAcademicDepartments>
<script>
var academicDepartments = [
<#if academicDeptDG?has_content>
    <#list academicDeptDG as resultRow>
        <#assign uri = resultRow["theURI"] />
        <#assign label = resultRow["name"] />
        {"uri": "${uri?url}", "name": "${label}"}<#if (resultRow_has_next)>,</#if>
    </#list>        
</#if>
];
var urlsBase = "${urls.base}";
</script>
</#macro>



<!-- 
	################################################################################
	Geographic Focus
	renders the "geographic focus" section on the home page. works in
	conjunction with the homePageMaps.js and latLongJson.js files, as well
	as the leaflet javascript library.
	################################################################################
-->
<#macro geographicFocusHtml>
    <section id="home-geo-focus" class="home-sections">
        <h2 class="h1">${i18n().geographic_focus}</h2>
        <#-- map controls allow toggling between multiple map types: e.g., global, country, state/province. -->
        <#-- VIVO default is for only a global display, though the javascript exists to support the other   -->
        <#-- types. See map documentation for additional information on how to implement additional types.  -->
        <#--
            <div id="mapControls">
                <a id="globalLink" class="selected" href="javascript:">${i18n().global_research}</a>&nbsp;|&nbsp;
                <a id="countryLink" href="javascript:">${i18n().country_wide_research}</a>&nbsp;|&nbsp;
                <a id="localLink" href="javascript:">${i18n().local_research}</a>  
            </div>  
        -->
        <div id="researcherTotal"></div>
        <div id="timeIndicatorGeo">
            <span>${i18n().loading_map_information}&nbsp;&nbsp;&nbsp;
                <img  src="${urls.images}/indicatorWhite.gif">
            </span>
        </div>
        <div id="mapGlobal" class="mapArea"></div>

       <#--  
            <div id="mapCountry" class="mapArea"></div>
            <div id="mapLocal" class="mapArea"></div> 
       -->
    </section>
</#macro>
