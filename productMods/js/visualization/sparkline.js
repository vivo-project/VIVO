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
    				
    				/* Since there are publications there are chances that there will be co-authors as well, so show the 
    				 * co-author network icon.*/
    				$("#coauthorship_link_container").show();
            	} 
				
            }
        });		
	}
};

$(document).ready(function() {
    visualization.renderCoAuthor(visualizationUrl);
});