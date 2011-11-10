/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var ScinodePolygon = CirclePolygon.extend({
	init: function(options) {
		this._super(options);
		this.hide();
	},
	setValue: function(value) {
		this.polygon.value = value;
	},
	getValue: function() {
		return this.polygon.value;
	},
	setSize: function(size) {
		this.setRadius(size);
		this.setZIndex(-size);
	},
	focus: function() {
		this.setOptions({strokeWeight: 1.2, strokeColor: '#000'});
	},
	unfocus: function() {
		this.setOptions({strokeWeight: 1.0, strokeColor: '#808080'});
	},
	setContent: function(content) {
		this.polygon.content = content;
	},
	registerEvents : function() {
		var me = this;
		var polygon = me.polygon;
		me._super();
		
		me.registerEvent(addClickListener(polygon, function() {
			INFO_WINDOW.setPosition(this.center);
			var content = this.content;
			INFO_WINDOW.setContent(content);
			INFO_WINDOW.open(this.map);
		}));
		
		me.registerEvent(addMouseOverListener(polygon, function() {
			me.focus();
		}));
		
		me.registerEvent(addMouseOutListener(polygon, function() {
			me.unfocus();
		}));
	}
});

function createScinodeMarker(map, label, value, radius, color, latlng) {
	var circleOptions = {
		label: label,
		value: value,
		strokeColor: '#808080',
		strokeOpacity: 1.0,
		strokeWeight: 1.0,
		fillColor: color,
		fillOpacity: 0.9,
		map: map,
		center: latlng,
		zIndex: -radius,
		radius: radius // min: 10000, max: 2500000
	};
	
	return new ScinodePolygon(circleOptions);
}

