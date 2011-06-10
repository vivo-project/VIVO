<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Default individual profile page template -->
<#--@dumpAll /-->

<#assign nameForOtherGroup = "other"> <#-- used by both individual-propertyGroupMenu.ftl and individual-properties.ftl -->

<#-- Menu Ontology properties -->
<#include "individual-menu-properties.ftl">

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/getURLParam.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/colorAnimations.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.form.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/controls.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/toggle.js"></script>')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>')}
