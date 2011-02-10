<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->

<#assign currentParameter = "grant">

<#include "entityComparisonSetup.ftl">

<#assign temporalGraphDownloadFileLink = '${temporalGraphDownloadCSVCommonURL}&vis=entity_grant_count'>

<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">

/*
This is used in util.js to print grant temporal graph links for all sub-organizations.
*/    
var subOrganizationTemporalGraphURL = "${subOrganizationGrantTemporalGraphCommonURL}";

</script>

<#assign currentParameterObject = grantParameter>

<#include "entityComparisonBody.ftl">