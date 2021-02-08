<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#assign googleJSAPI = 'https://www.gstatic.com/charts/loader.js'>

${headScripts.add(googleJSAPI)}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>')}

<#include "personPublicationSparklineContent.ftl">
