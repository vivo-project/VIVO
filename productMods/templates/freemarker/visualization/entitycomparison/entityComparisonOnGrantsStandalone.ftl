<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->

<#assign currentParameter = "grant">

<#include "entityComparisonSetup.ftl">

<#assign temporalGraphDownloadFileLink = '${temporalGraphDownloadCSVCommonURL}&vis=entity_grant_count'>
<#assign temporalGraphDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=entity_grant_count&uri=${organizationURI}&vis_mode=json">

<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
<!--

/*
This is used in util.js to print grant temporal graph links for all sub-organizations.
*/    
var subOrganizationTemporalGraphURL = "${subOrganizationGrantTemporalGraphCommonURL}";

$(document).ready(function () {

    $.blockUI.defaults.overlayCSS = { 
        backgroundColor: '#fff', 
        opacity:         1.0 
    };
    
    $.blockUI.defaults.css.width = '500px';
    $.blockUI.defaults.css.border = '0px';
     
    $("div#body").block({
        message: '<h3><img src="' + loadingImageLink + '" />&nbsp;Loading data for <i>${organizationLabel}</i></h3>'
    });

    $.ajax({
        url: '${temporalGraphDataURL}',
        dataType: "json",
        success: function (data) {

            if (data.error) {
                $("#error-container").show();
                $("#body").remove();
            } else {
                temporalGraphProcessor.initiateTemporalGraphRenderProcess(graphContainer, data);
                $("#error-container").remove();
            }
        }
    });

    // unblock when ajax activity stops    
    $(document).ajaxStop($("div#body").unblock());

});

// -->
</script>

<#assign currentParameterObject = grantParameter>

<#include "entityComparisonBody.ftl">

<#-- 
Right now we include the error message by default becuae currently I could not devise any more smarted solution. By default
the CSS of the #error-container is display:none; so it will be hidden unless explicitly commanded to be shown which we do in 
via JavaScript.
-->
<#include "entityGrantComparisonError.ftl">