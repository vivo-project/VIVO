<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<@widget name="login" include="assets" />
<#include "browse-classgroups.ftl">

<!DOCTYPE html>
<html lang="en">
    <head>
        <#include "head.ftl">
    </head>
    
    <body class="${bodyClasses!}">
        <#include "identity.ftl">

        <#include "menu.ftl">

        <section id="intro" role="region">
            <h2>Welcome to VIVO</h2>

            <p>VIVO is a research-focused discovery tool that enables collaboration among scientists across all disciplines.</p>
            <p>Browse or search information on people, departments, courses, grants, and publications.</p>
            
            <section id="search-home" role="region">
                <h3>Search VIVO</h3>
                
                <fieldset>
                    <legend>Search form</legend>
                    <form id="search-home-vivo" action="${urls.search}" method="post" name="search-home" role="search">
                        <div id="search-home-field">
                            <input type="text" name="querytext" class="search-home-vivo" value="${querytext!}" />
                            <input type="submit" value="Search" class="search">
                        </div>
                    </form>
                </fieldset>
            </section> <!-- #search-home -->
        </section> <!-- #intro -->
        
        <@widget name="login" />
        
        <@allClassGroups vClassGroups />
    
    <#include "footer.ftl">
    </body>
</html>