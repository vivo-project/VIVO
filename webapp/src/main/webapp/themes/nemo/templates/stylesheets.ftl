<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for loading stylesheets in the head -->

<!-- vitro base styles (application-wide) -->
<#-- <link rel="stylesheet" href="${urls.base}/css/vitro.css" /> -->


<!-- <link rel="stylesheet" href="${urls.theme}/css/bootstrap.min.css" /> -->
<!-- <link rel="stylesheet" href="${urls.theme}/css/bootstrap-theme.css" /> -->

<!-- Bootstrap - Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" 
  integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" 
  crossorigin="anonymous">

<!-- Bootstrap - Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" 
  integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" 
  crossorigin="anonymous">

<link rel="stylesheet" href="${urls.theme}/css/theme.css" />
<link rel="stylesheet" href="${urls.theme}/css/homepage.css" />
<link rel="stylesheet" href="${urls.theme}/css/individual.css" />

<#--temporary until edit controller can include this when needed -->
<#--<link rel="stylesheet" href="${urls.base}/css/edit.css" />-->

${stylesheets.list()}
