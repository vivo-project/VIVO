<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#import "lib-list.ftl" as l>

<!DOCTYPE html>
<html lang="${country}">
    <head>
        <#include "head.ftl">
    </head>

    <body class="${bodyClasses!}" onload="${bodyOnload!}">
        <div class="container">
            <#include "identity.ftl">
            <#include "search.ftl"ß>
            <#include "menu.ftl">

            <div class="row">
                <div class="row-wrapper">
                <#-- VIVO OpenSocial Extension by UCSF -->
                <#if openSocial??>
                    <#if openSocial.visible>
                        <div id="gadgets-tools" class="gadgets-gadget-parent"></div>
                    </#if>
                </#if>

                ${body}
                </div>
            </div>

            <#include "footer.ftl">
        </div>
    </body>
</html>
