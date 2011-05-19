/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var ScinodePolygon = Polygon.extend({
	init: function(options) {
		options.polygon = createGoogleCirclePolygon(options);
		this._super(options);
	},
	setValue: function(value) {
		this.options.value = value;
	},
	getValue: function() {
		return this.options.value;
	},
	setSize: function(size) {
		this.polygon.setRadius(size);
		this.setZIndex(-size);
	}
});

function createScinodeMarker(map, label, value, radius, color, latlng) {
	var circleOptions = {
		label: label,
		value: value,
		strokeColor: color,
		strokeOpacity: 1.0,
		strokeWeight: 1.0,
		fillColor: color,
		fillOpacity: 0.25,
		map: map,
		center: latlng,
		zIndex: -radius,
		radius: radius // min: 10000, max: 2500000
	};
	
	return new ScinodePolygon(circleOptions);
}

