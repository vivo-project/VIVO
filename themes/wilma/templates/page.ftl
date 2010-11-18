<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-list.ftl" as l>
            
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <!-- Google Chrome Frame open source plug-in brings Google Chrome's open web technologies and speedy JavaScript engine to Internet Explorer-->
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        
        <title>${title}</title>
        
        ${stylesheets.addFromTheme("/css/screen.css")}
        <#include "stylesheets.ftl">
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
            
            <section id="search" role="region">
                <fieldset>
                    <legend>Search form</legend>
                    
                    <form id="searchForm" action="${urls.search}" name="searchForm" role="search"> 
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
                        </div>
                        <!-- <input class ="submit" name="submit" type="submit"  value="Search" /> -->
                    </form>
                </fieldset>
            </section>
        </header>
        
        <nav role="navigation">
            <ul id="main-nav">
                <#list tabMenu.items as item>
                    <li><a href="${item.url}" <#if item.active> class="selected" </#if>>${item.linkText}</a></li>
                </#list>
            </ul>
        </nav>
 
        <div id="wrapper-content">        
            <#if flash?has_content>
                <div id="flash-message">
                    ${flash}
                </div>
            </#if>
   
            <#-- mb863: using for now until we have controllers page.ftl to render the templates commented below-->
            <#--<#include "menupage/menupage.ftl">-->
            <#--<#include "menupage/menupage--classgroup-people.ftl">-->
            <#--<#include "individual/individual--generic-class.ftl">-->
            <#--<#include "individual/individual--foaf-person.ftl">-->
            
            ${body}
        </div> <!-- #wrapper-content -->
        
        <footer role="contentinfo">
            <#if copyright??>
                <p class="copyright"><small>&copy;${copyright.year?c} 
                <#if copyright.url??>
                    <a href="${copyright.url}">${copyright.text}</a>  
                <#else>
                </#if>
                    All Rights Reserved | <a class="terms" href="${urls.termsOfUse}">Terms of Use</a></small> | Powered by <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank"><strong>VIVO</strong></a> 
                    <#if loginName??>
                        | Version <a href="${version.moreInfoUrl}">${version.label}</a>
                    </#if>
                </p>
            </#if>
               
            <nav role="navigation">
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
        
        <!--[if lt IE 7]>
        <script type="text/javascript" src="${urls.home}/js/jquery_plugins/supersleight.js"></script>
        <script type="text/javascript" src="${urls.theme}/js/utils.js"></script>
        <![endif]-->

        <!--[if (gte IE 6)&(lte IE 8)]>
        <script type="text/javascript" src="${urls.home}/js/selectivizr.js"></script>
        <![endif]-->
        
        <#include "googleAnalytics.ftl">
    </body>
</html>