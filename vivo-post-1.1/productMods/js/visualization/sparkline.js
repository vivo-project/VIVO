/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/* Javascript for sparkline visualization on person profile page */

var visualization = {
	render: function(url) {
		var containerId = 'vis_container',
		    container = $('#' + containerId);
			
        //container.empty().html('<img src="${loadingImageLink}" />');
       
        $.ajax({
            url: url,
			data: {
				'render_mode': 'dynamic',
				'vis': 'person_pub_count',
				'vis_mode': 'short',
				'container': containerId
			},
            dataType: 'html',
            success:function(data){
                container.html(data);
            }
        });		
	}
};

$(document).ready(function() {
    visualization.render(visualizationUrl);
});
