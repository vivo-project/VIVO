/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/**
 * For rendering images dynamically.
 * 
 */
$.fn.image = function(src, successFunc, failureFunc){
	return this.each(function(){ 
		var profileImage = new Image();
		profileImage.onerror = failureFunc;
		profileImage.onload = successFunc;
		profileImage.src = src;

		return profileImage;
	});
};


/**
 * Function by Google Charts API Team to do "extended encode" of data. 
*/
function extendedEncodeDataForChartURL(arrVals, maxVal) {

    var EXTENDED_MAP = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.';
    var EXTENDED_MAP_LENGTH = EXTENDED_MAP.length;
    var chartData = 'e:';

    for (i = 0, len = arrVals.length; i < len; i++) {
        // In case the array vals were translated to strings.
        var numericVal = new Number(arrVals[i]);
        // Scale the value to maxVal.
        var scaledVal = Math.floor(EXTENDED_MAP_LENGTH * EXTENDED_MAP_LENGTH * numericVal / maxVal);

        if (scaledVal > (EXTENDED_MAP_LENGTH * EXTENDED_MAP_LENGTH) - 1) {
            chartData += "..";
        } else if (scaledVal < 0) {
            chartData += '__';
        } else {
            // Calculate first and second digits and add them to the output.
            var quotient = Math.floor(scaledVal / EXTENDED_MAP_LENGTH);
            var remainder = scaledVal - EXTENDED_MAP_LENGTH * quotient;
            chartData += EXTENDED_MAP.charAt(quotient) + EXTENDED_MAP.charAt(remainder);
        }
    }

    return chartData;
}

/**
 * This will be used for getting images directly from the secure https://charts.googleapis.com
 * instead of http://charts.apis.google.com which currently throws security warnings.
 * 
 * see http://code.google.com/apis/chart/docs/chart_params.html FOR chart parameters
 * see http://code.google.com/apis/chart/docs/data_formats.html FOR how to encode data
 * 
 * sample constructed URL - https://chart.googleapis.com/chart?cht=ls&chs=148x58&chdlp=r&chco=3399CC&chd=e%3AW2ttpJbb..ttgAbbNtAA
 */
function constructVisualizationURLForSparkline(dataString, visualizationOptions) {

	/*
	 * Since we are directly going to use this URL in img tag, we are supposed to enocde "&"
	 * update: But since we are directly using it in an Image creating function we dont need to encode it.
	*/
    //var parameterDifferentiator = "&amp;";
    var parameterDifferentiator = "&";

    var rootGoogleChartAPI_URL = "https://chart.googleapis.com/chart?";

    /*
     * cht=ls indicates chart of type "line chart sparklines". 
     * see http://code.google.com/apis/chart/docs/gallery/chart_gall.html 
	*/
    var chartType = "cht=" + visualizationOptions.chartType;

    /*
     * It seems google reduces 2px from width & height before rendering the actual image.
     * We will do the same.
	*/
    var chartSize = "chs=" + (visualizationOptions.width - 2) + "x" + (visualizationOptions.height - 2);

    /*
     * It means that legend, if present, is to be displayed to the right of the chart,
     * legend entries in a vertical column.
	*/
    var chartLabelPosition = "chdlp=" + visualizationOptions.chartLabel;

    /*
     * Color of the sparkline.
	*/
    var chartColor = "chco=" + visualizationOptions.color;

    return rootGoogleChartAPI_URL + chartType + parameterDifferentiator 
    			+ chartSize + parameterDifferentiator 
    			+ chartLabelPosition + parameterDifferentiator 
    			+ chartColor + parameterDifferentiator 
    			+ "chd=" + dataString
}

/*
 * In IE trim() is not supported.
 * */
if (typeof String.prototype.trim !== 'function') {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, '');
	}
}

function toTitleCase(str) {
	return str.replace(/\w\S*/g, function(txt) {
		return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
	});
}