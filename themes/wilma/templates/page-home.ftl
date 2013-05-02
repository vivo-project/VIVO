<#-- $This file is distributed under the terms of the license in /doc/license.txt$  -->

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
            <#include "geoFocusMapScripts.ftl">
        </#if>
        <script type="text/javascript" src="${urls.base}/js/homePageUtils.js?version=x"></script>
    </head>
    
    <body class="${bodyClasses!}" onload="${bodyOnload!}">
    <#-- supplies the faculty count to the js function that generates a random row number for the solr query -->
        <@lh.facultyMemberCount  vClassGroups! />
        <#include "identity.ftl">

        <#include "menu.ftl">

        <section id="intro" role="region">
            <h2>Welcome to VIVO</h2>

            <p>VIVO is a research-focused discovery tool that enables collaboration among scientists across all disciplines.</p>
            <p>Browse or search information on people, departments, courses, grants, and publications.</p>

            <section id="search-home" role="region">
                <h3>Search VIVO <span class="search-filter-selected">filteredSearch</span></h3>
        
                <fieldset>
                    <legend>Search form</legend>
                    <form id="search-homepage" action="${urls.search}" name="search-home" role="search" method="post" > 
                        <div id="search-home-field">
                            <input type="text" name="querytext" class="search-homepage" value="" autocapitalize="off" />
                            <input type="submit" value="Search" class="search" />
                            <input type="hidden" name="classgroup" class="search-homepage" value="" autocapitalize="off" />
                        </div>
                
                        <a class="filter-search filter-default" href="#" title="Filter search">
                            <span class="displace">filter search</span>
                        </a>
                
                        <ul id="filter-search-nav">
                            <li><a class="active" href="">All</a></li>
                            <@lh.allClassGroupNames vClassGroups! />  
                        </ul>
                    </form>
                </fieldset>
            </section> <!-- #search-home -->
        
        </section> <!-- #intro -->
        
        <@widget name="login" />
        
        <!-- List of research classes: e.g., articles, books, collections, conference papers -->
        <@lh.researchClasses />
                
        <!-- List of four randomly selected faculty members -->
        <@lh.facultyMbrHtml />

        <!-- List of randomly selected academic departments -->
        <@lh.academicDeptsHtml />

        <#if geoFocusMapsEnabled >
            <!-- Map display of researchers' areas of geographic focus. Must be enabled in runtime.properties -->
            <@lh.geographicFocusHtml />
        </#if>
        
        <!-- Statistical information relating to property groups and their classes; displayed horizontally, not vertically-->
        <@lh.allClassGroups vClassGroups! />

        <#include "footer.ftl">
        <#-- builds a json object that is used by js to render the academic departments section -->
        <@lh.listAcademicDepartments />
    </body>
</html>