<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Default VIVO individual profile page template (extends individual.ftl in vitro) -->

<#include "individual-setup.ftl">

<#assign individualProductExtension>
    <#-- Remove invlude below when individual--foaf-organization.ftl is created -->
    <#include "individual-visualization.ftl">
    
    <#include "individual-overview.ftl">
        </section> <!-- #individual-info -->
    </section> <!-- #individual-intro -->
</#assign>

<#include "individual-vitro.ftl">

${stylesheets.add("/css/individual/individual-vivo.css")}

${headScripts.add("/js/jquery_plugins/jquery.truncator.js")}

${scripts.add("/js/individual/individualUtils.js")}