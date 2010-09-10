<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#if visualizationUrl??>
    <div id="vis_container">&nbsp;</div>
    
    ${stylesheets.addFromTheme("/visualization/visualization.css")}
    ${scripts.add("/js/visualization/sparkline.js")}
    
    <script type="text/javascript">
        var visualizationUrl = '${visualizationUrl}';
    </script>
</#if>