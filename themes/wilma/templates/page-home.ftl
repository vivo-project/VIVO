<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-list.ftl" as l>
<#import "widget-login.ftl" as login>
                
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>${title}</title>
        <@login.styles />
        ${stylesheets.addFromTheme("/css/screen.css")}
        ${stylesheets.tags}
        <!-- script for enabling new HTML5 semantic markup in IE browsers-->
        <script type="text/javascript" src="/js/html5.js"></script>
        ${headScripts.tags}

        <!--[if lt IE 7]>
        <script type="text/javascript" src="${themeDir}/js/jquery_plugins/supersleight.js"></script>
        <script type="text/javascript" src="${themeDir}/js/utils.js"></script>
        <link rel="stylesheet" href="css/ie6.css" />
        <![endif]-->

        <!--[if IE 7]>
        <link rel="stylesheet" href="css/ie7.css" />
        <![endif]-->

        <!--[if (gte IE 6)&(lte IE 8)]>
        <script type="text/javascript" src="${themeDir}/js/selectivizr.js"></script>
        <![endif]-->
    </head>
    
    <body>
        <header id="branding">
            <h2 class="vivo-logo"><a href="${urls.home}"><span class="displace">${siteName}</span></a></h2>
            <!-- Since we are using a graphic text for the tagline, we won't render ${siteTagline}
            <#if siteTagline?has_content>
                <em>${siteTagline}</em>
            </#if>-->

            <nav>
                <ul id="header-nav">
                    	
                <#if loginName??>
                    <li><span class="pictos-arrow-10">U</span> ${loginName}</li>
                    <li><a href="${urls.logout}">Log out</a></li>
                    <li><a href="${urls.siteAdmin}">Site Admin</a></li>
                <#else>
                     <li><a title="log in to manage this site" href="${urls.login}">Log in</a></li>
                </#if>
                <#-- List of links that appear in submenus, like the header and footer. -->

                    <li><a href="${urls.about}">About</a></li>
                <#if urls.contact??>
                    <li><a href="${urls.contact}">Contact Us</a></li>
                </#if> 
                    <li><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
                </ul>
            </nav>
                
            <section id="search">
                <fieldset>
                    <legend>Search form</legend>

                    <form id="searchForm" action="${urls.search}" name="searchForm"> 
                       <#if showFlag1SearchField??>
                           <select id="search-form-modifier" name="flag1" class="form-item" >
                               <option value="nofiltering" selected="selected">entire database (${loginName})</option>
                               <option value="${portalId}">${siteTagline!}</option>
                           </select>
                       <#else>
                           <input type="hidden" name="flag1" value="${portalId}" />
                       </#if> 
                       <div id="search-field">
                           <input type="text" name="querytext" class="search-vivo" value="${querytext!}" />
                           <a class ="submit" href="javascript:document.searchForm.submit();">Search</a>
                           <!-- <input class ="submit" name="submit" type="submit"  value="Search" /> -->
                       </div>
                    </form>
                 </fieldset>
            </section>
        </header>
            
        <nav>
            <ul id="main-nav">
                <#list tabMenu.items as item>
                    <li><a href="${item.url}" <#if item.active> class="selected" </#if>>${item.linkText}</a></li>          
                </#list>
            </ul>
        </nav>
            
        <div id="wrapper-content">
            <section id="intro">
                <h3>What is VIVO?</h3>
                
                <p>VIVO is an open source semantic web application originally developed and implemented at Cornell. When installed and populated with researcher interests, activities, and accomplishments, it enables the discovery of research and scholarship across disciplines at that institution. VIVO supports browsing and a search function which returns faceted results for rapid retrieval of desired information. Content in any local VIVO installation may be maintained manually,  brought into VIVO in automated ways from local systems of record, such as HR, grants, course, and faculty activity databases, or from database providers such as publication aggregators and funding agencies. <a href="#">More<span class="pictos-arrow-14"> 4</span></a></p>
                <section id="search-home">
                    <h3>Search VIVO</h3>
                    
                    <fieldset>
                        <legend>Search form</legend>
                        <form id="search-home-vivo" action="${urls.search}" method="post" name="search">
                            <div id="search-home-field">
                                <input name="search-home-vivo" class="search-home-vivo" id="search-home-vivo"  type="text" />
                                <a class ="submit" href="#">Search</a>
                            </div>
                        </form>
                    </fieldset>
                </section> <!-- #search-home -->
            </section> <!-- #intro -->
            
            <@login.markup />
            
            <section id="browse">
                <h2>Browse</h2>
                
                <ul id="browse-classGroups">
                    <li><a  class="selected" href="#">People<span class="count-classes"> (1,280)</span></a></li>
                    <li><a href="#">Courses<span class="count-classes"> (1,300)</span></a></li>
                    <li><a href="#">Activities<span class="count-classes"> (980)</span></a></li>
                    <li><a href="#">Topics<span class="count-classes"> (345)</span></a></li>
                    <li><a href="#">Events<span class="count-classes"> (670)</span></a></li>
                    <li><a href="#">Organizations<span class="count-classes"> (440)</span></a></li>
                    <li><a href="#">Publications<span class="count-classes"> (670)</span></a></li>
                    <li><a href="#">Locations<span class="count-classes"> (903)</span></a></li>
                </ul>
                
                <section id="browse-classes">
                    <nav>
                        <ul id="class-group-list">
                            <li><a href="#">Faculty Member<span class="count-individuals"> (18,080)</span></a></li>
                            <li><a class="selected"  href="#">Graduate Student<span class="count-individuals"> (2,550)</span></a></li>
                            <li><a href="#">Librarian <span class="count-individuals"> (1,280)</span></a></li>
                            <li><a href="#">Non-Academic 	  	 <span class="count-individuals"> (280)</span></a></li>
                            <li><a href="#">Non-Faculty Academic <span class="count-individuals"> (2,380)</span></a></li>
                            <li><a href="#">Person<span class="count-individuals"> (2,480)</span></a></li>
                            <li><a href="#">Postdoc <span class="count-individuals"> (1,380)</span></a></li>
                            <li><a href="#">Professor Emeritus<span class="count-individuals"> (680)</span></a></li>
                            <li><a href="#">Undergraduate Student<span class="count-individuals"> (880)</span></a></li>
                        </ul>
                    </nav>
                    
                    <section id="visual-graph">
                        <h4>Visual Graph</h4>
                        <img src="${themeDir}/images/visual-graph.jpg" />
                    </section>
                </section> <!-- #browse-classes -->
            </section> <!-- #browse -->
            
            <section id="highlights">
                <h2>Highlights</h2>
                
                <section id="featured-people" class="global-highlights">
                    <h3>FEATURED PEOPLE</h3>
                    
                    <!--use Hs-->
                    <article class="featured-people vcard">
                        <a href="#">
                            <img  class="individual-photo" src="${themeDir}/images/person-thumbnail-sample.jpg" width="80" height="80" />
                            <p class="fn">Hayworth, Rita<span class="title">Actress, dancer</span><span class="org">Sabbatic year for ever</span></p>
                        </a>
                    </article>
                    
                    <article class="featured-people vcard">
                        <a href="#">
                            <img  class="individual-photo" src="${themeDir}/images/person-thumbnail-sample-2.jpg" width="80" height="80" />
                            <p class="fn">Wiedmann, Martin <span class="title">Associate Professor</span><span class="org">Cornell faculty</span></p>
                        </a>
                    </article>
                </section> <!-- #featured-people -->
                
                <section id="upcoming-events" class="global-highlights">
                    <h3>UPCOMING EVENTS</h3>
                    
                    <article class="vevent">
                        <time class="dtstart" datetime="2010-02-13T20:00Z">21<span>Dec</span></time>
                        <p class="summary">Understanding Patent Writing <time>3:30 PM</time></p>
                    </article>
                    
                    <article class="vevent">
                        <time class="dtstart" datetime="2010-02-13T20:00Z">20<span>Nov</span></time>
                        <p class="summary">Voters, Dictators, and Peons <time>4:30 PM</time></p>
                    </article>
                    
                    <article class="vevent">
                        <time class="dtstart" datetime="2010-02-13T20:00Z">19<span>Nov</span></time>
                        <p class="summary">Proton-Coupled Electron Transfer II <time>5:30 PM</time></p>
                    </article>
                    
                    <p class="view-all"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
                </section> <!-- #upcoming-events -->
                
                <section id="latest-publications" class="global-highlights">
                    <h3>LATEST PUBLICATIONS</h3>
                    
                    <article class="latest-publications-item">
                        <p class="publication-content">Solar masses<span><em>Journal</em> <time datetime="2010-02-13T20:00Z"> | December 2010</time></p>
                    </article>
                    
                    <article class="latest-publications-item">
                        <p class="publication-content">Link data and the Web<span><em>Article</em> <time datetime="2010-02-13T20:00Z"> | December 2010</time></p>
                    </article>
                    
                    <article class="latest-publications-item">
                        <p class="publication-content">Building a community<span><em>Book</em> <time datetime="2010-02-13T20:00Z"> | November 2010</time></p>
                    </article>
                    
                    <article class="latest-publications-item">
                        <p class="publication-content">Biology 101<span><em>Series</em> <time datetime="2010-02-13T20:00Z"> | November 2010</time></p>
                    </article>
                    
                    <p class="view-all"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
                </section> <!-- #latest-publications -->
            </section> <!-- $highlights -->
        </div> <!-- #wrapper-content -->
        <footer>
            <#if copyright??>
                <p class="copyright"><small>&copy;${copyright.year?c} 
                <#if copyright.url??>
                    <a href="${copyright.url}">${copyright.text}</a>  
                <#else>
                    ${copyright.text}
                </#if> 
                All Rights Reserved | <a class="terms" href="${urls.termsOfUse}">Terms of Use</a></small> | Powered by <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank"><strong>VIVO</strong></a></p>
            </#if>
            <nav>
                <ul id="footer-nav">
                    <li><a href="${urls.about}">About</a></li>
                <#if urls.contact??>
                    <li><a href="${urls.contact}">Contact Us</a></li>
                </#if> 
                    <li><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
                </ul>
            </nav>
        </footer>

        ${scripts.add("/js/jquery.js")}
        ${scripts.tags}

        <#include "googleAnalytics.ftl">
    </body>
</html>
