/* $This file is distributed under the terms of the license in /doc/license.txt$ */

//Hard coded color constants
var	TURQUOISE = "#8DD3C7";
var	DARK_TURQUOISE = "#009999";
var	LIGHT_YELLOW = "#FFFFB3";
var	LIGHT_VIOLET = "#BEBADA";
var	RED = "#CC0000";
var	LIGHT_RED = "#FB8072";
var	DARK_RED = "#520000";
var	SKY_BLUE = "#80B1D3";
var	DARK_BLUE = "#80B1D3";
var	LIGHT_BLUE = "#3399FF";
var	ORANGE = "#FDB462";
var DARK_ORANGE = "#FF9900";
var	LIGHT_GREEN = "#B3DE69";
var	DARK_GREEN = "#006600";
var	VIBRANT_GREEN = "#99CC00";
var	LIGHT_PINK = "#FCCDE5";
var	LIGHT_GREY = "#D9D9D9";
var	PURPLE = "#BC80BD";
var	DARK_PURPLE = "#6600CC";
var	PINK_PURPLE = "#CC00CC";

var colorConstantQueue = [ DARK_BLUE, DARK_TURQUOISE, 
                           LIGHT_BLUE, DARK_GREEN,
                           VIBRANT_GREEN, DARK_PURPLE, 
                           PINK_PURPLE, DARK_ORANGE,
                           DARK_RED, RED ];

var freeColors = colorConstantQueue.slice();

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
var tableDiv;
var entityLevel;

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
		},
		yaxis : {
			tickSize : 1,
			tickDecimals : 0,
			min : 0
		},
		grid : {
			borderColor : "#D9D9D9"
		}
};

FlotOptions.colors = colorConstantQueue;