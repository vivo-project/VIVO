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
                
                        <a class="filter-search filter-default" href="#" title="Filter search"><span class="displace">filter search</span></a>
                
                        <ul id="filter-search-nav">
                            <li><a class="active" href="">All</a></li>
                            <@lh.allClassGroupNames vClassGroups! />  
                        </ul>
                    </form>
                </fieldset>
            </section> <!-- #search-home -->
        
        </section> <!-- #intro -->
        
        <@widget name="login" />
        
        <section id="home-research" class="home-sections">
            <h4>Research</h4>
            <ul>
                <@lh.researchClasses />
                
            </ul>
        </section>

        <section id="home-faculty-mbrs" class="home-sections"  >
            <h4>Faculty</h4>
            <div id="tempSpacing">&nbsp;</div>
            <div id="research-faculty-mbrs">
                <!-- populated via an ajax call -->
                <ul id="facultyThumbs">
                </ul>

            </div>
        </section>

        <section id="home-academic-depts" class="home-sections">
            <h4>Departments</h4>
            <div id="academic-depts"></div>
        </section>        

        <#if geoFocusMapsEnabled >
            <section id="home-geo-focus" class="home-sections">
                <h4>Geographic Focus</h4>
                <div id="mapControls">
                    <a id="globalLink" class="selected" href="javascript:">Global Research</a>&nbsp;|&nbsp;
                    <a id="usLink" href="javascript:">US Research</a>&nbsp;|&nbsp;
                    <#-- <a id="stateLink" href="javascript:">NY Research</a>  -->
                </div>
                <div id="researcherTotal"></div>
                <div id="timeIndicator">
                    <span>Loading map information . . .&nbsp;&nbsp;&nbsp;
                        <img  src="${urls.images}/indicatorWhite.gif">
                    </span>
                </div>
                <div id="mapGlobal" class="mapArea"></div>
                <div id="mapUS" class="mapArea"></div>
                <div id="mapState" class="mapArea"></div>
            </section>
        </#if>
        
        <section id="home-stats" class="home-sections" >
            <h4>Statistics</h4>

            <ul id="stats">
                <@lh.allClassGroups vClassGroups! />
            </ul>
        </section>
    
        <#include "footer.ftl">
        <#-- builds a json object that is used by js to render the academic departments section -->
        <@lh.academicDepartments />
    </body>
</html>