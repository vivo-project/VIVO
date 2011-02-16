<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<meta charset="utf-8" />

<title>${title}</title>
 
<#include "stylesheets.ftl">
<link rel="stylesheet" href="${urls.theme}/css/screen.css" media="screen" />
<link rel="stylesheet" href="${urls.theme}/css/print.css" media="print" />

<#include "headScripts.ftl">

<#-- Inject head content specified in the controller. Currently this is used only to generate an rdf link on 
an individual profile page. -->
${headContent!}