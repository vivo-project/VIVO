<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#include "menupage-checkForData.ftl">

<#if !noData>
    <section id="menupage-intro" role="region">
        <h3>${page.title}</h3>
        
        <section id="menupage-content" class="people" role="region">
            <h4>Visual Graph</h4>
            
            <#include "menupage-vClassesInClassgroup.ftl">
            
            <section id="menupage-graph" class="people" role="region">
                <img src="${urls.images}/menupage/visual-graph.jpg" alt="" />
            </section>
        </section>
        
        <section id="find-by" role="region">
            <nav role="navigation">
                <h3>Find By</h3>
                
                <ul id="find-filters">
                    <#assign subjectAreaUri = "http://vivoweb.org/ontology/core#SubjectArea" />
                    <#assign departmentUri = "http://vivoweb.org/ontology/core#Department" />
                    <#assign courseUri = "http://vivoweb.org/ontology/core#Course" />
                    
                    <li><a href="${urls.base}/individuallist?vclassId=${subjectAreaUri?url}">Subject Area</a></li>
                    <li><a href="${urls.base}/individuallist?vclassId=${departmentUri?url}">Department</a></li>
                    <li><a href="${urls.base}/individuallist?vclassId=${courseUri?url}">Courses</a></li>
                </ul>
            </nav>
        </section>
    </section>
    
    <section id="network-stats" role="region">
        <h3>Network stats</h3>
        
        <p>(n) Persons | (n) with authorship | (n) researchers | (n) are principal investigators | (n) with awards | (n) are teaching | (n) have positions in organization</p>
    </section>
    
    <#include "menupage-browse.ftl">
    
    ${stylesheets.add("/css/menupage/menupage.css")}
    
    <#include "menupage-scripts.ftl">
    
    ${scripts.add("/js/menupage/browseByVClassPeople.js")}
<#else>
    ${noDataNotification}
</#if>