
//Hard coded color constants
var	TURQUOISE = "#8DD3C7";
var	LIGHT_YELLOW = "#FFFFB3";
var	LIGHT_VIOLET = "#BEBADA";
var	LIGHT_RED = "#FB8072";
var	SKY_BLUE = "#80B1D3";
var	ORANGE = "#FDB462";
var	LIGHT_GREEN = "#B3DE69";
var	LIGHT_PINK = "#FCCDE5";
var	LIGHT_GREY = "#D9D9D9";
var	PURPLE = "#BC80BD";                     

var colorConstantQueue = 
	[
       TURQUOISE, LIGHT_YELLOW, LIGHT_VIOLET, LIGHT_RED, 
       SKY_BLUE, ORANGE, LIGHT_GREEN, LIGHT_PINK, LIGHT_GREY,
       PURPLE
    ];

var freeColors = [
                   TURQUOISE, LIGHT_YELLOW, LIGHT_VIOLET, LIGHT_RED, 
                   SKY_BLUE, ORANGE, LIGHT_GREEN, LIGHT_PINK, LIGHT_GREY,
                   PURPLE
                ];

var year = {
		min: 1998,
		max: 2018,
		globalMin: 1995,
		globalMax: 2025
};

var colors = {};
var prevColor = {};
var colorToAssign, colorToRemove;
var renderedObjects = [];
var labelToEntityRecord = {};
var setOfLabels = [];
var labelToCheckedEntities = {};
var stopWordsToCount = {};

var graphContainer;
var paginationDiv;
var tableDiv;

var lotsofSpaceCharacters = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\
		 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

//options for Flot
var FlotOptions = {
		legend : {
			show : false
		},
		lines : {
			show : true
		},
		points : {
			show : false
		},
		xaxis : {
			tickDecimals : 0,
			tickSize : 10
		},
		series : {
			lines : {
				lineWidth : 7
			}
		}
};

FlotOptions.colors = colorConstantQueue;

//options for pagination
var paginationOptions = {
		items_per_page : 2,
		num_display_entries : 3,
		num_edge_entries : 2,
		prev_text : "Prev",
		next_text : "Next"
};
