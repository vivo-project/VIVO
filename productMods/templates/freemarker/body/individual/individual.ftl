<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Default VIVO individual profile page template (extends individual.ftl in vitro) -->

<#include "individual-setup.ftl">

<#assign individualProductExtension>
    <#include "individual-overview.ftl">

    <#include "individual-visualization.ftl">
        </section> <!-- #individual-info -->
    </section> <!-- #individual-intro -->
</#assign>

<#include "individual-vitro.ftl">

${headScripts.add("/js/jquery_plugins/jquery.truncator.js")}

${scripts.add("/js/individual/individualUtils.js")}