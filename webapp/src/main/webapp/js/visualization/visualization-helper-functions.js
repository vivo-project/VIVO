/* $This file is distributed under the terms of the license in LICENSE$ */

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
