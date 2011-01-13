/* $This file is distributed under the terms of the license in /doc/license.txt$ */

// NC: I've discussed this with Chintan and my hope is to have the temporal graph vis
// load the top level org by default (when the uri parameter is not included), therefore
// rendering this entire file unnecessary and obsolete.
var visualizationLink = {
    // Initial page setup
    onLoad: function() {
        this.mergeFromTemplate();
        this.initObjects();
    },
    
    // Add variables from menupage template
    mergeFromTemplate: function() {
        $.extend(this, menupageData);
    },
    
    // Create references to frequently used elements for convenience
    initObjects: function() {
        this.visLink = $('.visualization-menupage-link');
    },
    
    // Temporarily hide the link while we determine the href value via AJAX
    hideLink: function() {
        this.visLink.addClass('hidden');
    },
    
    // Determine the URL parameters for temporal graph of top level org
    getURL: function() {
        $.ajax({
            url: this.baseUrl + "/visualizationAjax",
            data: ({vis: "utilities", vis_mode: "HIGHEST_LEVEL_ORGANIZATION"}),
            dataType: "text",
            success:function(data){
                if (data != null && data != "") {
                        visualizationLink.visLink.attr("href", data);
                        visualizationLink.visLink.removeClass('hidden');
                    }
            }
        });
    }
};

$(document).ready(function() {
    visualizationLink.onLoad();
    visualizationLink.hideLink();
    visualizationLink.getURL();
});