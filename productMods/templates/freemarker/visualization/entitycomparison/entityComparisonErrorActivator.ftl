<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign organizationURI ="${organizationURI?url}">
<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">

<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
	
var contextPath = "${urls.base}";

</script>

<div id="body">
	<p>This Organization has neither Sub-Organizations nor People. Please visit this Organization's <a href="${organizationVivoProfileURL}">profile page.</a></p>
</div>