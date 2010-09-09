<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#if visualization.url??>
    <div id="vis_container">&nbsp;</div>
    
    <#assign visContainer = "vis_container">
    <#assign visUrl = "${visualization.url}&container=${visContainer}">
    
    ${stylesheets.addFromTheme("/visualization/visualization.css")}
    ${scripts.add("/js/visualization/sparkline.js")}
    
    <script type="text/javascript">
    var visualizationData = {
        url: '${visUrl}',
        container: '${visContainer}'
    };
    </script>
</#if>