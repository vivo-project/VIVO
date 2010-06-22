<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#include "partials/doctype.html">

<#include "partials/head.ftl">

<body>
    <div id="wrap" class="container">
        <div id="header">
        
            <#include "partials/identity.ftl">
            
            <#-- Note to UI team: do not change this div without also making the corresponding change in menu.jsp -->
            <div id="navAndSearch" class="block">
                <#include "partials/menu.ftl">  
                <#include "partials/search.ftl">                
            </div> <!-- navAndSearch --> 

            <#-- <#include "/partials/breadcrumbs.ftl"> -->         
        </div> <!-- header --> 

        <hr class="hidden" />

        <div id="contentwrap">      
            <div id="content">
                <#-- We don't do title here because some pages don't get a title, or it may not be the same as the <title> text.
                <h2>${title}</h2> -->               
                ${body} 
                <#-- <@dumpDataModel /> -->
            </div> <!-- content -->
        </div> <!-- contentwrap -->
    
        <#include "partials/footer.ftl">
                                    
    </div> <!-- wrap -->  

    <#include "partials/scripts.ftl"> 
</body>
</html>

<#-- Three ways to add a stylesheet:

A. In theme directory:
${stylesheets.addFromTheme("/sample.css");
${stylesheets.add(stylesheetDir + "/sample.css")}

B. Any location
${stylesheets.add("/edit/forms/css/sample.css"}

-->