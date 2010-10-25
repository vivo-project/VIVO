<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<title>${title}</title>
<link rel="stylesheet" href="${themeDir}/css/style.css" />
${stylesheets.tags}
<!-- script for enabling new HTML5 semantic markup in IE browsers-->
${headScripts.add("/js/html5.js")}
${headScripts.tags}
</head>
<body>
<div id="wrapper">
  <header id="branding">
    <h2 class="vivo-logo"><a href="${urls.home}"><span class="displace">${siteName}</span></a></h2>
	<!-- Since we are using a graphic text for the tagline, we won't render ${siteTagline}
	<#if siteTagline?has_content>
		<em>${siteTagline}</em>
    </#if>-->
<#import "lib-list.ftl" as l>
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
               <input type="text" name="querytext" class="search-vivo" value="${querytext!}"  />
               <a class ="submit" href="javascript:document.searchForm.submit();">Search</a> </div>
                <!-- <input class ="submit" name="submit" type="submit"  value="Search" /> -->
           </form>
         </fieldset>
    </section>
  </header>
  <nav>
	    <ul id="main-nav">
	        <#list tabMenu.items as item>
	            <li>
	                <a href="${item.url}" <#if item.active> class="selected" </#if>>
	                    ${item.linkText}
	                </a>
	            </li>          
	        </#list>
	    </ul>
  </nav>
  <div id="wrapper-content">


<section id="intro">
     <h3>What is VIVO?</h3>
     <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus adipiscing ipsum et ligula accumsan aliquam. Vestibulum posuere mollis arcu quis condimentum. Sed rhoncus nibh vitae lectus mattis accumsan. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam elementum eleifend ante vel aliquam. Fusce non pellentesque leo. Nunc rhoncus vehicula metus, a pellentesque velit elementum ibh vitae lectus mattis accumsan. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam elem. <a href="#">More<span class="pictos-arrow-14"> 4</span></a></p>
     <section id="search-home">
       <h3>Search VIVO</h3>
       <fieldset>
         <legend>Search form</legend>
         <form id="search-home-vivo" action="#" method="post" name="search">
           <div id="search-home-field">
             <input name="search-home-vivo" class="search-home-vivo" id="search-home-vivo"  type="text" />
             <a class ="submit" href="#">Search</a> </div>
         </form>
       </fieldset>
     </section>
   </section>
   <!-- #intro -->
   <section id="log-in">
     <h3>Log in</h3>
     <form id="log-in-form" action="#" method="post" name="log-in-form" />
     <label for="email">Email</label>
     <div class="input-field">
       <input name="email" id="email" type="text" required />
     </div>
     <label for="password">Password</label>
     <div class="input-field">
       <input name="password" id="password" type="password" required />
     </div>
     <div id="wrapper-submit-remember-me"> <a class="green button" href="#">Log in</a>
       <div id="remember-me">
         <input class="checkbox-remember-me" name="remember-me" type="checkbox" value="" />
         <label class="label-remember-me"for="remember-me">Remember me</label>
       </div>
     </div>
     <p class="forgot-password"><a href="#">Forgot your password?</a></p>
     </form>
     <div id="request-account"> <a class="blue button" href="#">Request an account</a> </div>
   </section><!-- #log-in -->
   <section id="browse">
     <h2>Browse</h2>
     <ul id="browse-classGroups">
       <li><a  class="selected" href="#">class group 1<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 2<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 3<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 4<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 5<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 6<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group n<span class="count-classes"> (280)</span></a></li>
     </ul>
     <section id="browse-classes">
       <nav>
         <ul id="class-group-list">
           <li><a  class="selected" href="#">class 1<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 2<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 3<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 4<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 5<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 6<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 7<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 8<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 9<span class="count-individuals"> (280)</span></a></li>
         </ul>
       </nav>
       <section id="visual-graph">
         <h4>Visual Graph</h4>
         <img src="${themeDir}/images/visual-graph.jpg" /> </section>
     </section>
   </section><!-- Browse -->
   <section id="highlights">
     <h2>Highlights</h2>
     <section id="fearuted-people" class="global-highlights">
       <h3>FEATURED PEOPLE</h3>
       <!--use Hs-->
       <article class="featured-people vcard"> <img  class="individual-photo" src="${themeDir}/images/person-thumbnail.jpg" width="80" height="80" />
         <p class="fn">foaf:lastName, foaf:firstName <span class="title">core:preferredTitle</span><span class="org">currentPosition(s)</span></p>
       </article>
       <article class="featured-people vcard"> <img  class="individual-photo" src="${themeDir}/images/person-thumbnail.jpg" width="80" height="80" />
         <p class="fn">foaf:lastName, foaf:firstName <span class="title">core:preferredTitle</span><span class="org">currentPosition(s)</span></p>
       </article>
     </section><!-- featured-people -->
     <section id="upcoming-events" class="global-highlights">
       <h3>UPCOMING EVENTS</h3>
       <article class="vevent">
         <time class="dtstart" datetime="2010-02-13T20:00Z">20<span>Aug</span></time>
         <p class="summary">individualName. eventClass
           <time>9:30 AM</time>
         </p>
       </article>
       <article class="vevent">
         <time class="dtstart" datetime="2010-02-13T20:00Z">20<span>Aug</span></time>
         <p class="summary">individualName. eventClass
           <time>9:30 AM</time>
         </p>
       </article>
       <article class="vevent">
         <time class="dtstart" datetime="2010-02-13T20:00Z">20<span>Aug</span></time>
         <p class="summary">individualName. eventClass
           <time>9:30 AM</time>
         </p>
       </article>
       <p class="view-all"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
     </section><!-- upcoming-events -->
     <section id="latest-publications" class="global-highlights">
       <h3>LATEST PUBLICATIONS</h3>
       <article class="latest-publications-item">
         <p class="publication-content">individualName<span><em>Audio Content</em>
           <time datetime="2010-02-13T20:00Z"> | March 2010</time>
         </p>
       </article>
       <article class="latest-publications-item">
         <p class="publication-content">individualName<span><em>Audio Content</em>
           <time datetime="2010-02-13T20:00Z"> | March 2010</time>
         </p>
       </article>
       <article class="latest-publications-item">
         <p class="publication-content">individualName<span><em>Audio Content</em>
           <time datetime="2010-02-13T20:00Z"> | March 2010</time>
         </p>
       </article>
       <article class="latest-publications-item">
         <p class="publication-content">individualName<span><em>Audio Content</em>
           <time datetime="2010-02-13T20:00Z"> | March 2010</time>
         </p>
       </article>
       <p class="view-all"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
     </section><!-- latest-publications -->
   </section>
</div>
  <footer>
    <div id="footer-content">
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
    </div>
  </footer>
</div>



<script type="text/javascript" src="http://use.typekit.com/chp2uea.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>
${scripts.add("/js/jquery.js")}
${scripts.tags}


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

<#include "googleAnalytics.ftl">
</body>
</html>
