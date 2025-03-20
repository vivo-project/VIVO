<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for loading stylesheets in the head -->

<!-- vitro base styles (application-wide) -->
<#-- <link rel="stylesheet" href="${urls.base}/css/vitro.css" /> -->
<link rel="stylesheet" href="${urls.base}/css/tooltip.css" />



<!-- Bootstrap - Latest compiled and minified CSS -->
<link rel="stylesheet" type="text/css" href="${urls.base}/js/bootstrap/css/bootstrap.min.css"/>

<!-- Bootstrap - Optional theme -->
<link rel="stylesheet" type="text/css" href="${urls.base}/js/bootstrap/css/bootstrap-theme.min.css"/>

<link rel="stylesheet" href="${urls.theme}/css/theme.css" />
<link rel="stylesheet" href="${urls.theme}/css/homepage.css" />
<link rel="stylesheet" href="${urls.theme}/css/individual.css" />

<#--temporary until edit controller can include this when needed -->
<#--<link rel="stylesheet" href="${urls.base}/css/edit.css" />-->

${stylesheets.list()}
