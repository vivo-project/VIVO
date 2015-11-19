/* $This file is distributed under the terms of the license in /doc/license.txt$ */
/**
 * The scaler is used for scaling based on the predefined minimum value and
 * maximum value. You also can control the returned maximum scale and minimum 
 * scale.
 */
var Scaler = Class.extend({ 
	init: function(options) {
		this.options = $.extend({}, this.options, options);
	},
	options: {
		scaleFunc: ReallySimpleAreaScale,
		minValue: 0.0,
		maxValue: 0.0,
		minScale: 0.0,
		maxScale: 1.0
	},
	getScale: function(value) {
		var o = this.options;
		var scale = o.scaleFunc(value, o.minValue, o.maxValue, o.minScale, o.maxScale);
		if (scale > o.maxScale) {
			scale = maxScale;
		} else if (scale < o.minScale) {
			scale = minScale;
		}
		return scale;
	}
});

/* Scaling that ignore minScale and maxScale */
function ReallySimpleAreaScale(value, minValue, maxValue, minScale, maxScale) {
	return maxScale * Math.sqrt(value / maxValue);
}

/* Scaling that cares about minScale and maxScale */
function SimpleAreaScale(value, minValue, maxValue, minScale, maxScale) {
	if (maxValue != minValue) {
		var scale = minScale; 
		if (value > minValue) {
			var valueDiff = maxValue - minValue;
			var areaScale = value / valueDiff;
			scale = Math.sqrt(areaScale);
		}
		return scale;
	} else {
		return maxScale;
	}
}

/**
 * SizeCoder use scaler to scale its interested size based on the given
 * scaler strategy.
 */
var CircleSizeCoder = Class.extend({
	init: function(options) {
		this.options = $.extend({}, this.options, options);
	},
	options: {
		minRadius: 0,
		maxRadius: 25.0,
		scaler: new Scaler({})
	},
	getSize: function(value) {
		var o = this.options;
		var radius = o.scaler.getScale(value) * o.maxRadius;
		
		if (radius < o.minRadius) {
			radius = o.minRadius;
		} else if (radius > o.maxRadius) {
			radius = o.maxRadius;
		}
		
		return radius;
	},
	getMaxValue: function() {
		return this.options.scaler.options.maxValue;
	}
});

