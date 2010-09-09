/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/* Javascript for sparkline visualization on person profile page */

var visualization = {
	render: function(visualizationData) {
		var container = $('#' + visualizationData.container);
        //$(container).empty().html('<img src="${loadingImageLink}" />');
       
        $.ajax({
            url: visualizationData.url,
            dataType: "html",
            success:function(data){
                $(container).html(data);
            }
        });		
	}
};

$(document).ready(function() {
    visualization.render(visualizationData);
});
