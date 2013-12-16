<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#import "lib-list.ftl" as l>

<!DOCTYPE html>
<html lang="en">
    <head>
        <#include "head.ftl">
    </head>
    
    <body class="${bodyClasses!}" onload="${bodyOnload!}">
        <#include "identity.ftl">
        <#include "search.ftl" >
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