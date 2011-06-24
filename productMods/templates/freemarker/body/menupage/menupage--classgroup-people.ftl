<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#include "menupage-checkForData.ftl">
<#if !noData>
    <section id="menupage-intro" class="people" role="region">
        <h2>${page.title}</h2>
        
        <section id="find-by" role="region">
            <nav role="navigation">
                <h3>Find By: </h3>
                
                <#assign subjectAreaUri = "http://vivoweb.org/ontology/core#SubjectArea" />
                <#assign departmentUri = "http://vivoweb.org/ontology/core#Department" />
                <#assign courseUri = "http://vivoweb.org/ontology/core#Course" />
                
                <ul id="find-filters">
                    <li><a href="${urls.base}/individuallist?vclassId=${subjectAreaUri?url}">Subject Area</a></li>
                    <li><a href="${urls.base}/individuallist?vclassId=${departmentUri?url}">Department</a></li>
                    <li><a href="${urls.base}/individuallist?vclassId=${courseUri?url}">Courses</a></li>
                </ul>
            </nav>
        </section>
    </section>
    
    <#include "menupage-browse.ftl">
    
    ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/menupage/menupage.css" />')}
    
    <#include "menupage-scripts.ftl">
    
    ${scripts.add('<script type="text/javascript" src="${urls.base}/js/menupage/browseByVClassPeople.js"></script>')}

<#else>
    ${noDataNotification}
</#if>