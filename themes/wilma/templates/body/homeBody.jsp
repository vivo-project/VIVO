<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>
<%-- This is a temporary file and will be removed once we have completed the transition to freemarker --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.web.*" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page errorPage="/error.jsp"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet" %>

<% 
FreemarkerHttpServlet.getFreemarkerComponentsForJsp(request);
%>

<%
  VitroRequest vreq = new VitroRequest(request);  
  Portal portal = vreq.getPortal();
  
  String contextRoot = vreq.getContextPath();
  
  String themeDir = portal != null ? portal.getThemeDir() : Portal.DEFAULT_THEME_DIR_FROM_CONTEXT;
  themeDir = contextRoot + '/' + themeDir;
%>


<c:set var="portal" value="${requestScope.portalBean}"/>
<c:set var="themeDir"><c:out value="${themeDir}" /></c:set>
<c:set var="bodyJsp"><c:out value="${requestScope.bodyJsp}" default="/debug.jsp"/></c:set>
<c:set var="title"><c:out value="${requestScope.title}" /></c:set>


<section id="intro">
    <h3>What is VIVO?</h3>
    
    <p>VIVO is an open source semantic web application originally developed and implemented at Cornell. When installed and populated with researcher interests, activities, and accomplishments, it enables the discovery of research and scholarship across disciplines at that institution. VIVO supports browsing and a search function which returns faceted results for rapid retrieval of desired information. Content in any local VIVO installation may be maintained manually,  brought into VIVO in automated ways from local systems of record, such as HR, grants, course, and faculty activity databases, or from database providers such as publication aggregators and funding agencies. <a href="#">More<span class="pictos-arrow-14"> 4</span></a></p>
    <section id="search-home">
        <h3>Search VIVO</h3>
        
        <fieldset>
            <legend>Search form</legend>
            <form id="search-home-vivo" action="<%=contextRoot%>/search" method="post" name="search">
                <div id="search-home-field">
                    <input name="search-home-vivo" class="search-home-vivo" id="search-home-vivo"  type="text" />
                    <a class ="submit" href="#">Search</a>
                </div>
            </form>
        </fieldset>
    </section> <!-- #search-home -->
</section> <!-- #intro -->

<%-- Couldn't get the login to work on the home page after refactoring in JSP/FTL mashup world, so disabling for now --%>
<%-- ${ftl_login} --%>

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
            <img src="<%=themeDir%>images/visual-graph.jpg" />
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
                <img  class="individual-photo" src="<%=themeDir%>images/person-thumbnail-sample.jpg" width="80" height="80" />
                <p class="fn">Hayworth, Rita<span class="title">Actress, dancer</span><span class="org">Sabbatic year for ever</span></p>
            </a>
        </article>
        
        <article class="featured-people vcard">
            <a href="#">
                <img  class="individual-photo" src="<%=themeDir%>images/person-thumbnail-sample-2.jpg" width="80" height="80" />
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