<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#assign visualizationLink>
    
    <#assign temporalGraphIcon = '${urls.images}/visualization/temporal_vis_small_icon.jpg'>
    
	<div id="temporal-graph-link-container">
		<div class="collaboratorship-icon">
            <a class="temporal-graph-link" href="#"><img src="${temporalGraphIcon}" alt="Temporal Graph"/></a>
        </div>
        <div class="collaboratorship-link">
            <h3><a  class="temporal-graph-link" href="#">Temporal Graph</a></h3>
        </div>
	</div>
	
    ${stylesheets.add("css/visualization/visualization.css")}
    
    <script type="text/javascript">
        var contextPath = '${contextPath}';
        
        $.ajax({
			url: contextPath + "/visualizationAjax",
			data: ({vis: "utilities", vis_mode: "HIGHEST_LEVEL_ORGANIZATION"}),
			dataType: "text",
			success:function(data){
				if (data != null && data != "") {
						$("#temporal-graph-link-container .temporal-graph-link").attr("href", data);
						$("#temporal-graph-link-container").show();
					}
			}
		});
	
    </script>
    
    
</#assign>

<#include "menupage.ftl">