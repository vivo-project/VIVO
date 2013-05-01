<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<@widget name="login" include="assets" />
<#include "browse-classgroups.ftl">

<!DOCTYPE html>
<html lang="en">
    <head>
        <#include "head.ftl">
    </head>
    
    <body class="${bodyClasses!}" onload="${bodyOnload!}">
        <#include "identity.ftl">

        <#include "menu.ftl">


        <section id="intro" role="region">
            <h2>${i18n().intro_title}</h2>

            <p>${i18n().intro_para1}</p>
            <p>${i18n().intro_para2}Browse or search information on people, departments, courses, grants, and publications.</p>

            <section id="search-home" role="region">
                <h3>${i18n().intro_searchvivo} <span class="search-filter-selected">filteredSearch</span></h3>
        
                <fieldset>
                    <legend>Search form</legend>
                    <form id="search-homepage" action="${urls.search}" name="search-home" role="search" method="post" > 
                        <div id="search-home-field">
                            <input type="text" name="querytext" class="search-homepage" value="${querytext!}" autocapitalize="off" />
                            <input type="submit" value="Search" class="search" />
                        </div>
                
                        <a class="filter-search filter-default" href="#" title="${i18n().intro_filtersearch}"><span class="displace">${i18n().intro_filtersearch}</span></a>
                
                        <ul id="filter-search-nav">
                            <li><a class="active" href="">All</a></li>
                            <li><a href="">People</a></li>
                            <li><a href="">Organizations</a></li>
                            <li><a href="">Research</a></li>
                            <li><a href="">Events</a></li>
                            <li><a href="">Topics</a></li>
                        </ul>
                    </form>
                </fieldset>
            </section> <!-- #search-home -->
        
        </section> <!-- #intro -->
        
        <@widget name="login" />
        
        <#--<@allClassGroups vClassGroups! />-->
    
        <section id="home-stats">
            <h4>Stats</h4>

            <ul id="stats">
                <li><a href="#"><p class="stats-count">19<span>k</span></p><p class="stats-type">People</p></a></li>
                <li><a href="#"><p class="stats-count">128<span>k</span></p><p class="stats-type">Research</p></a></li>
                <li><a href="#"><p class="stats-count">22<span>k</span></p><p class="stats-type">Organizations</p></a></li>
                <li><a href="#"><p class="stats-count">29<span>k</span></p><p class="stats-type">Events</p></a></li>
                <li><a href="#"><p class="stats-count">1.9<span>k</span></p><p class="stats-type">Topics</p></a></li>
                <li><a href="#"><p class="stats-count">6.5<span>k</span></p><p class="stats-type">Activities</p></a></li>
            </ul>
        </section>
    
        <#include "footer.ftl">
    </body>
</html>