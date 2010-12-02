<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<@widget name="login" include="assets" /> 
                       
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <!-- Google Chrome Frame open source plug-in brings Google Chrome's open web technologies and speedy JavaScript engine to Internet Explorer-->
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        
        <title>${title}</title>
        <meta name="description" content="" />
        <meta name="keywords" content="" />

        <#-- ${stylesheets.addFromTheme("/css/screen.css")} -->
        <#include "stylesheets.ftl">
        <link rel="stylesheet" href="${urls.theme}/css/screen.css" />
        
        <#include "headScripts.ftl">

        <!--[if lt IE 7]>
        <link rel="stylesheet" href="css/ie6.css" />
        <![endif]-->

        <!--[if IE 7]>
        <link rel="stylesheet" href="css/ie7.css" />
        <![endif]-->

    </head>
    
    <body>
        <header id="branding" role="banner">
            <h1 class="vivo-logo"><a href="${urls.home}"><span class="displace">${siteName}</span></a></h1>
            <!-- Since we are using a graphic text for the tagline, we won't render ${siteTagline}
            <#if siteTagline?has_content>
                <em>${siteTagline}</em>
            </#if>-->

            <nav role="navigation">
                <ul id="header-nav" role="list">
                    <#if user.loggedIn>
                        <li role="listitem"><span class="pictos-arrow-10">U</span> ${user.loginName}</li>
                        <li role="listitem"><a href="${urls.logout}">Log out</a></li>
                        <#if user.hasSiteAdminAccess>
                            <li role="listitem"><a href="${urls.siteAdmin}">Site Admin</a></li>
                        </#if>
                    <#else>
                        <li role="listitem"><a title="log in to manage this site" href="${urls.login}">Log in</a></li>
                    </#if>
                    <#-- List of links that appear in submenus, like the header and footer. -->

                    <li role="listitem"><a href="${urls.about}">About</a></li>
                    <#if urls.contact??>
                        <li role="listitem"><a href="${urls.contact}">Contact Us</a></li>
                    </#if> 
                    <li role="listitem"><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
                </ul>
            </nav>
                
            <section id="search" role="region">
                <fieldset>
                    <legend>Search form</legend>

                    <form id="search-form" action="${urls.search}" name="search" role="search"> 
                       <#if user.showFlag1SearchField>
                           <select id="search-form-modifier" name="flag1" class="form-item" >
                               <option value="nofiltering" selected="selected">entire database (${user.loginName})</option>
                               <option value="${portalId}">${siteTagline!}</option>
                           </select>
                       <#else>
                           <input type="hidden" name="flag1" value="${portalId}" />
                       </#if> 
                       <div id="search-field">
                           <input type="text" name="querytext" class="search-vivo" value="${querytext!}" />
                           <input type="submit" value="Search" class="submit">
                       </div>
                    </form>
                 </fieldset>
            </section>
        </header>
            
        <nav role="navigation">
            <ul id="main-nav" role="list">
                <#list tabMenu.items as item>
                    <li role="listitem"><a href="${item.url}" <#if item.active> class="selected" </#if>>${item.linkText}</a></li>          
                </#list>
            </ul>
        </nav>
       
        <div id="wrapper-content" role="main">
            <#if flash?has_content>
                <section id="flash-message" role="alert">
                    ${flash}
                </section>
            </#if>
        
            <section id="intro" role="region">
                <h2>What is VIVO?</h2>
                
                <p>VIVO is an open source semantic web application originally developed and implemented at Cornell. When installed and populated with researcher interests, activities, and accomplishments, it enables the discovery of research and scholarship across disciplines at that institution. VIVO supports browsing and a search function which returns faceted results for rapid retrieval of desired information. Content in any local VIVO installation may be maintained manually,  brought into VIVO in automated ways from local systems of record, such as HR, grants, course, and faculty activity databases, or from database providers such as publication aggregators and funding agencies. <a href="#">More<span class="pictos-arrow-14"> 4</span></a></p>
                <section id="search-home" role="region">
                    <h3>Search VIVO</h3>
                    
                    <fieldset>
                        <legend>Search form</legend>
                        <form id="search-home-vivo" action="${urls.search}" method="post" name="search-home" role="search">
                            <#if user.showFlag1SearchField>
                                <select id="search-form-modifier" name="flag1" class="form-item" >
                                    <option value="nofiltering" selected="selected">entire database (${user.loginName})</option>
                                    <option value="${portalId}">${siteTagline!}</option>
                                </select>
                            <#else>
                                <input type="hidden" name="flag1" value="${portalId}" />  
                           </#if> 
                            <div id="search-home-field">
                                <input type="text" name="querytext" class="search-home-vivo" value="${querytext!}" />
                                <input type="submit" value="Search" class="submit">
                            </div>
                        </form>
                    </fieldset>
                </section> <!-- #search-home -->
            </section> <!-- #intro -->
          
            <@widget name="login" />

            <section id="browse" role="region">
                <h4>Browse</h4>
                
                <ul id="browse-classgroups" role="list">
                    <li role="listitem"><a  class="selected" href="#">People<span class="count-classes"> (1,280)</span></a></li>
                    <li role="listitem"><a href="#">Courses<span class="count-classes"> (1,300)</span></a></li>
                    <li role="listitem"><a href="#">Activities<span class="count-classes"> (980)</span></a></li>
                    <li role="listitem"><a href="#">Topics<span class="count-classes"> (345)</span></a></li>
                    <li role="listitem"><a href="#">Events<span class="count-classes"> (670)</span></a></li>
                    <li role="listitem"><a href="#">Organizations<span class="count-classes"> (440)</span></a></li>
                    <li role="listitem"><a href="#">Publications<span class="count-classes"> (670)</span></a></li>
                    <li role="listitem"><a href="#">Locations<span class="count-classes"> (903)</span></a></li>
                </ul>
                
                <section id="browse-classes" role="navigation">
                    <nav>
                        <ul id="classgroup-list" role="list">
                            <li role="listitem"><a href="#">Faculty Member<span class="count-individuals"> (18,080)</span></a></li>
                            <li role="listitem"><a class="selected"  href="#">Graduate Student<span class="count-individuals"> (2,550)</span></a></li>
                            <li role="listitem"><a href="#">Librarian <span class="count-individuals"> (1,280)</span></a></li>
                            <li role="listitem"><a href="#">Non-Academic <span class="count-individuals"> (280)</span></a></li>
                            <li role="listitem"><a href="#">Non-Faculty Academic <span class="count-individuals"> (2,380)</span></a></li>
                            <li role="listitem"><a href="#">Person<span class="count-individuals"> (2,480)</span></a></li>
                            <li role="listitem"><a href="#">Postdoc <span class="count-individuals"> (1,380)</span></a></li>
                            <li role="listitem"><a href="#">Professor Emeritus<span class="count-individuals"> (680)</span></a></li>
                            <li role="listitem"><a href="#">Undergraduate Student<span class="count-individuals"> (880)</span></a></li>
                        </ul>
                    </nav>
                    
                    <section id="visual-graph" role="region">
                        <h4>Visual Graph</h4>
                        <img src="${urls.theme}/images/visual-graph.jpg" alt=""/>
                    </section>
                </section> <!-- #browse-classes -->
            </section> <!-- #browse -->
            
            <section id="highlights" role="navigation">
                <h4>Highlights</h4>
                
                <section id="featured-people" class="global-highlights" role="region">
                    <h5>FEATURED PEOPLE</h5>
                    
                    <!--use Hs-->
                    <article class="featured-people vcard" role="article">
                        <a href="#">
                            <img  class="individual-photo" src="${urls.theme}/images/person-thumbnail-sample.jpg" alt="" width="80" height="80" />
                            <p class="fn">Hayworth, Rita<span class="title">Actress, dancer</span><span class="org">Sabbatic year for ever</span></p>
                        </a>
                    </article>
                    
                    <article class="featured-people vcard" role="article">
                        <a href="#">
                            <img  class="individual-photo" src="${urls.theme}/images/person-thumbnail-sample-2.jpg" alt="" width="80" height="80" />
                            <p class="fn">Wiedmann, Martin <span class="title">Associate Professor</span><span class="org">Cornell faculty</span></p>
                        </a>
                    </article>
                </section> <!-- #featured-people -->
                
                <section id="upcoming-events" class="global-highlights" role="region">
                    <h5>UPCOMING EVENTS</h5>
                    
                    <article class="vevent" role="article">
                        <time class="dtstart" datetime="2010-02-13T20:00Z">21<span>Dec</span></time>
                        <p class="summary">Understanding Patent Writing <time>3:30 PM</time></p>
                    </article>
                    
                    <article class="vevent" role="article">
                        <time class="dtstart" datetime="2010-02-13T20:00Z">20<span>Nov</span></time>
                        <p class="summary">Voters, Dictators, and Peons <time>4:30 PM</time></p>
                    </article>
                    
                    <article class="vevent" role="article">
                        <time class="dtstart" datetime="2010-02-13T20:00Z">19<span>Nov</span></time>
                        <p class="summary">Proton-Coupled Electron Transfer II <time>5:30 PM</time></p>
                    </article>
                    
                    <p class="view-all"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
                </section> <!-- #upcoming-events -->
                
                <section id="latest-publications" class="global-highlights" role="region">
                    <h5>LATEST PUBLICATIONS</h5>
                    
                    <article role="article">
                        <p class="publication-content">Solar masses<span><em>Journal</em> <time datetime="2010-02-13T20:00Z"> | December 2010</time></p>
                    </article>
                    
                    <article role="article">
                        <p class="publication-content">Link data and the Web<span><em>Article</em> <time datetime="2010-02-13T20:00Z"> | December 2010</time></p>
                    </article>
                    
                    <article role="article">
                        <p class="publication-content">Building a community<span><em>Book</em> <time datetime="2010-02-13T20:00Z"> | November 2010</time></p>
                    </article>
                    
                    <article role="article">
                        <p class="publication-content">Biology 101<span><em>Series</em> <time datetime="2010-02-13T20:00Z"> | November 2010</time></p>
                    </article>
                    
                    <p class="view-all"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
                </section> <!-- #latest-publications -->
            </section> <!-- $highlights -->
        </div> <!-- #wrapper-content -->
        <footer role="contentinfo">
            <#if copyright??>
                <p class="copyright"><small>&copy;${copyright.year?c}
                <#if copyright.url??>
                    <a href="${copyright.url}">${copyright.text}</a>  
                <#else>
                    ${copyright.text}
                </#if> 
                    All Rights Reserved | <a class="terms" href="${urls.termsOfUse}">Terms of Use</a></small> | Powered by <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank"><strong>VIVO</strong></a> 
                    <#if user.hasRevisionInfoAccess>
                        | Version <a href="${version.moreInfoUrl}">${version.label}</a>
                    </#if>
                </p>
            </#if>
            <nav role="navigation">
                <ul id="footer-nav" role="list">
                    <li role="listitem"><a href="${urls.about}">About</a></li>
                <#if urls.contact??>
                    <li role="listitem"><a href="${urls.contact}">Contact Us</a></li>
                </#if> 
                    <li role="listitem"><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
                </ul>
            </nav>
        </footer>

        ${scripts.tags}
        
        <!--[if lt IE 7]>
        <script type="text/javascript" src="${urls.base}/js/jquery_plugins/supersleight.js"></script>
        <script type="text/javascript" src="${urls.theme}/js/utils.js"></script>
        <![endif]-->

        <!--[if (gte IE 6)&(lte IE 8)]>
        <script type="text/javascript" src="${urls.base}/js/selectivizr.js"></script>
        <![endif]-->
        
        <#include "googleAnalytics.ftl">
    </body>
</html>
