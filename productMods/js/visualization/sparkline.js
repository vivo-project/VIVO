/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/* Javascript for sparkline visualization on person profile page */

var visualization = {
	renderCoAuthor: function(url) {
	
		var containerIdCoAuthor = 'vis_container_coauthor',
		containerCoAuthor = $('#' + containerIdCoAuthor);
			
        $.ajax({
            url: url,
			data: {
				'render_mode': 'dynamic',
				'vis': 'person_pub_count',
				'vis_mode': 'short',
				'container': containerIdCoAuthor
			},
            dataType: 'html',
            success:function(data){
            	if ($.trim(data) != "") {
            		containerCoAuthor.html(data);
    				containerCoAuthor.children("#pub_count_short_sparkline_vis");
            	} 
				
            }
        });		
	}
};

$(document).ready(function() {
    visualization.renderCoAuthor(visualizationUrl);
});