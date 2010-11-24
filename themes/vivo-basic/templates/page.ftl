<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#include "doctype.html">

${stylesheets.addFromTheme("/css/screen.css")}
<#-- print stylesheet commented out for now until we can add a media parameter to stylesheets.add method -->
<#-- ${stylesheets.addFromTheme("/css/print.css")} -->

<#include "head.ftl">

<body>
    <div id="wrap" class="container">
        <div id="header">
        
            <#include "identity.ftl">
            
            <#-- Note to UI team: do not change this div without also making the corresponding change in menu.jsp -->
            <div id="navAndSearch" class="block">
                <#include "menu.ftl">  
                <#include "search.ftl">                
            </div> <!-- navAndSearch --> 
            
            <#include "breadcrumbs.ftl">         
        </div> <!-- header --> 

        <hr class="hidden" />

        <div id="contentwrap">
            <#if flash?has_content>
                <section id="flash-message" role="alert">
                    ${flash}
                </section>
            </#if> 
            
            <div id="content"> 
                ${body}
            </div> <!-- content -->
        </div> <!-- contentwrap -->
    
        <#include "footer.ftl">
                                      
    </div> <!-- wrap --> 
    
    <#include "scripts.ftl"> 
</body>
</html>