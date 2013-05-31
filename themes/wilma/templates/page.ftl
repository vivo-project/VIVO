<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-list.ftl" as l>

<!DOCTYPE html>
<html lang="en">
    <head>
        <#include "head.ftl">
    </head>
    
    <body class="${bodyClasses!}" onload="${bodyOnload!}">
        <#include "identity.ftl">
        
        <section id="search" role="region">
            <fieldset>
                <legend>${i18n().search_form}</legend>

                <form id="search-form" action="${urls.search}" name="search" role="search" accept-charset="UTF-8" method="POST"> 
                    <div id="search-field">
                        <input type="text" name="querytext" class="search-vivo" value="${querytext!}" autocapitalize="off" />
                        <input type="submit" value="${i18n().search_button}" class="search">
                    </div>
                </form>
            </fieldset>
        </section>
        
        <#include "menu.ftl">

		<#-- VIVO OpenSocial Extension by UCSF -->
		<#if openSocial??>
			<#if openSocial.visible>
            	<div id="gadgets-tools" class="gadgets-gadget-parent"></div>
            </#if>	
		</#if>	
        
        ${body}
        
        <#include "footer.ftl">
    </body>
</html>