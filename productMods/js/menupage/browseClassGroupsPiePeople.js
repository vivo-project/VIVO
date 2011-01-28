/* $This file is distributed under the terms of the license in /doc/license.txt$ */

// This file extends and proxies the default behavior defined in vitro/webapp/web/js/menupage/browseByClassGroupsPie.js

// Saving the original graphClasses function from browseClassGroups
var graphPersonClasses = graphClasses.piechart;

// Assigning the proxy function
graphClasses.piechart = function(values, labels, uris) {
    // Clear the existing pie chart
    $('#menupage-graph').empty();
    
    // Create the canvas
    var r = Raphael("menupage-graph", 300, 300);
    
    // Setup the colors for the slices
    // colors = ['#192933', '#26404E', '#294656', '#194c68', '#487A96', '#63A8CE', '#67AED6','#758A96', '#9DB9C9' ];
    colors = ['#143D52', '#1F5C7A', '#297AA3', '#3399CC', '#5CADD6', '#85C2E0', '#ADD6EB', '#ADCBDD', '#D6EBF5', '#E9F1F5' ];
    // Reverse colors to see how it looks with larger slices in lighter hues:
    // colors = colors.reverse();
    
    // Now draw the pie chart
    var pie = r.g.piechart(100, 80, 70, values, {legend: labels, legendmark: "square", legendpos: "south", colors: colors});
    pie.hover(function () {
        this.sector.stop();
        this.sector.scale(1.1, 1.1, this.cx, this.cy);
        if (this.label) {
            this.label[0].stop();
            this.label[0].scale(1.5);
            this.label[1].attr({"font-weight": 800});
        }
    }, function () {
        this.sector.animate({scale: [1, 1, this.cx, this.cy]}, 500, "bounce");
        if (this.label) {
            this.label[0].animate({scale: 1}, 500, "bounce");
            this.label[1].attr({"font-weight": 400});
        }
    });
};