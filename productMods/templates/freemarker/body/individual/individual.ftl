<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Default VIVO individual profile page template (extends individual.ftl in vitro) -->

<#include "individual-setup.ftl">
<#import "lib-vivo-properties.ftl" as vp>

<#assign individualProductExtension>
    <#-- Include for any class specific template additions -->
    ${classSpecificExtension!}
    <@vp.webpages propertyGroups editable />
    <!--PREINDIVIDUAL OVERVIEW.FTL-->
    <#include "individual-overview.ftl">
        </section> <!-- #individual-info -->
    </section> <!-- #individual-intro -->
    <!--postindiviudal overiew tfl-->
</#assign>

<#include "individual-vitro.ftl">

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-vivo.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.truncator.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualUtils.js"></script>')}
