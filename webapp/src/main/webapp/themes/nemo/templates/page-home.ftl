<#-- $This file is distributed under the terms of the license in /doc/license.txt$	-->

<@widget name="login" include="assets" />

<#--
	With release 1.6, the home page no longer uses the "browse by" class group/classes display. 
	If you prefer to use the "browse by" display, replace the import statement below with the
	following include statement:

		<#include "browse-classgroups.ftl">
		
	Also ensure that the homePage.geoFocusMaps flag in the runtime.properties file is commented
	out.
-->
<#import "lib-home-page.ftl" as lh>

<!DOCTYPE html>
<html lang="en">
	<head>
		<#include "head.ftl">
		<#if geoFocusMapsEnabled >
			<#include "custom-geoFocusMapScripts.ftl">
		</#if>
		<script type="text/javascript" src="${urls.theme}/js/homePageUtils.js?version=x"></script>
	</head>
	
	<#-- REMOVE <body class="${bodyClasses!}" onload="${bodyOnload!}">-->
	
	<body class="no-logo fae">
		<#-- supplies the faculty count to the js function that generates a random row number for the search query -->
		<@lh.facultyMemberCount vClassGroups! />
		
		<#include "identity.ftl">
		<div class="col-md-12">
			<#-- Hero image with search on top -->
			<div class="row hero">
				<div class="theme-showcase">
					<div class="col-md-12">
						<div class="container" role="main">
							<div class="jumbotron">
								<h1>${i18n().intro_title}</h1>
							</div>
							<form id="search-homepage" 
								action="${urls.search}" 
								name="search-home" 
								role="search" 
								method="post" 
								class="form-horizontal">
								<fieldset>
									<div class="form-group pull-left" style="margin-right: 5px;">
										<select class="form-control" id="classgroup" name="classgroup">
											<option value="">${i18n().all_capitalized}</option>
											<#list vClassGroups as group>
												<#if (group.individualCount > 0)>
													<option value="${group.uri}">${group.displayName?capitalize}</option>
												</#if>
											</#list>
										</select>
									</div>
									<div class="form-group">
										<div class="input-group">
											<input 
												type="text" 
												name="querytext" 
												class="form-control" 
												value="" 
												placeholder="${i18n().search_form}" autocapitalize="off" />
											<span class="input-group-btn">
												<button class="btn btn-default" type="submit">
													<span class="icon-search">${i18n().search_button}</span>
												</button>
											</span>
										</div>
									</div>
								</fieldset>
							</form>
						</div>
					</div>
					<div class="col-md-12">
						<div class="container">
							<div class="jumbotron">
								<p>${i18n().intro_para1}</p>
								<p>${i18n().intro_para2}</p>
							</div>
						</div>
					</div>
				</div>
			</div>

			
			<#-- Moved menu nav below hero search
			<#include "menu.ftl"> -->
			<#-- <@widget name="login" /> -->
			

			<!-- 
				################################################################################
				The large icons below hero search
				Take third of viewport for each icon on medium devices
				################################################################################
			-->
			<#--
			<div class="row icons-home">
				<div class="col-md-4">
					<a href="/s/search.html?collection=vivo-site&form=sitesimple#collaborator" id="icon1"> 
						<span class="glyphicon glyphicon-user"></span>
						<h3>Search for a collaborator</h3>
					</a>
				</div>
				<div class="col-md-4">
					<a href="/s/search.html?collection=vivo-site&form=sitesimple#supervisor" id="icon2">
						<span class="glyphicon glyphicon-pencil"></span>
						<h3>Find a supervisor</h3>
					</a>
				</div>
				<div class="col-md-4">
				<a href="/s/search.html?collection=vivo-site&form=sitesimple#consultancy" id="icon3">
					<span class="glyphicon glyphicon-education"></span>
					<h3>Locate expert opinion</h3>
				</a>
				</div>
			</div>
			-->

			
			<!-- 
				################################################################################
				Carousel to showcase faculty members
				################################################################################
			-->
			<div class="row faculty-home">
				<div class="container">
					<div class="col-md-12">
						<h2 class="h1">Meet our faculty</h2>
						<div class="gap20"></div>
						<p>${i18n().home_faculty_para1}</p>
						<!-- Use bootstrap carousel to showcase faculty members, edited in lib-home-page.ftl and homePageUtils.js -->
						<@lh.facultyMbrHtml />
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-9">
					<div class="row">
						<!-- 
							################################################################################
							List of research classes: e.g., articles, books, collections, conference papers 
							################################################################################
						-->
						<div class="row research-count">
							<div class="col-md-6">
								<h2 class="h1">Research</h2>
								<div class="gap20"></div>
								<p>${i18n().home_research_para1}</p>
								<#--
									<p>Place for wording about our Research</p>
									<div class="gap30"></div>
									<button type="button" class="btn btn-default">
										<a href="http://vivo.school.edu/placeholder">Read more about our Research</a>
									</button>
								-->
							</div>
							<div class="col-md-6" id="research-classes">
								<@lh.researchClasses />
							</div>
						</div>
						

						<!-- 
							################################################################################
							List of randomly selected academic departments
							################################################################################
						-->
						<div class="row department-count">
							<div class="col-md-6">
								<h2 class="h1">Departments</h2>
								<div class="gap20"></div>
								<p>${i18n().home_departments_para1}</p>
							</div>
							<div class="col-md-6" id="academic-depts">
								<@lh.academicDeptsHtml />
							</div>
						</div>
						
						<#-- builds a json object that is used by js to render the academic departments section -->
						<@lh.listAcademicDepartments />
						
						
						<!-- 
							################################################################################
							Geographic Focus Map Section
							################################################################################
						-->
						<#if geoFocusMapsEnabled >
						<div class="row geo-home">
							<div class="container-fluid">
								<div class="col-md-12 hidden-xs hidden-sm" id="geo-map">
									<#-- Map display of researchers' areas of geographic focus. Must be enabled in runtime.properties -->
										<@lh.geographicFocusHtml />
								</div>
							</div>
						</div>
						</#if>

						
						<!-- 
							################################################################################
							Site Statistics Section
							################################################################################
						-->
						<!-- Statistical information relating to property groups and their classes; displayed horizontally, not vertically -->
						<!-- <@lh.allClassGroups vClassGroups! /> -->
						
						<!-- Statistical information relating to property groups and their classes; displayed horizontally, not vertically -->
						<div class="row research-count">
							<div class="col-md-6">
								<h2 class="h1">Site Statistics</h2>
								<div class="gap20"></div>
								<p>${i18n().home_site_stats_para1}</p>
								<#--
									<p>Place for wording about our Site Statistics</p>
									<div class="gap30"></div>
									<button type="button" class="btn btn-default">
										<a href="http://vivo.school.edu/placeholder">Read more about our Site Statistics</a>
									</button>
								-->
							</div>
							<div class="col-md-6" id="research-classes">
								<@lh.allClassGroups vClassGroups! />
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-3">
					<!--
						<a class="twitter-timeline" data-width="250" data-height="600" data-dnt="true" data-theme="dark" href="https://twitter.com/VIVOcollab?ref_src=twsrc%5Etfw">Tweets by VIVOcollab</a> <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>
					-->
					<div style="margin:0px 5px 0px 5px;width:100%;">
						<a class="twitter-timeline" data-height="1250" data-dnt="true" data-theme="light" href="https://twitter.com/VIVOcollab?ref_src=twsrc%5Etfw">Tweets by VIVOcollab</a> <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>
					</div>
				</div>
			</div>
		</div>
		<!-- 
			################################################################################
			Footer - Include the footer template after the statistics
			################################################################################
		-->
		<div class="row"><div class="container">
		<#include "footer.ftl">
	
		<script>	   
			var i18nStrings = {
				researcherString: '${i18n().researcher}',
				researchersString: '${i18n().researchers}',
				currentlyNoResearchers: '${i18n().currently_no_researchers}',
				countriesAndRegions: '${i18n().countries_and_regions}',
				countriesString: '${i18n().countries}',
				regionsString: '${i18n().regions}',
				statesString: '${i18n().map_states_string}',
				stateString: '${i18n().map_state_string}',
				statewideLocations: '${i18n().statewide_locations}',
				researchersInString: '${i18n().researchers_in}',
				inString: '${i18n().in}',
				noFacultyFound: '${i18n().no_faculty_found}',
				placeholderImage: '${i18n().placeholder_image}',
				viewAllFaculty: '${i18n().view_all_faculty}',
				viewAllString: '${i18n().view_all}',
				viewAllDepartments: '${i18n().view_all_departments}',
				noDepartmentsFound: '${i18n().no_departments_found}'
			};
			// set the 'limmit search' text and alignment
			if	( $('input.search-homepage').css('text-align') == "right" ) {		
				 $('input.search-homepage').attr("value","${i18n().limit_search} \u2192");
			}  
		</script>
	</body>
</html>
