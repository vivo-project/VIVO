/* $This file is distributed under the terms of the license in /doc/license.txt$ */
/**
 * The MarkerManager is more like a composite class of Marker. It manages 
 * markers by grouping the markers by keys. 
 */
var MarkerManager = Class.extend({
	init: function(colorStrategy, sizeCoder) {
		this.keyToMarker = {};
		this.colorStrategy = colorStrategy;
		this.sizeCoder = sizeCoder;
	},
	addMarker: function(key, marker) {
		this.keyToMarker[key] = marker;
	},
	length: function() {
		var size = 0;
		for (var key in this.keyToMarker) {
			if (this.keyToMarker.hasOwnProperty(key)) size++;
		}
		return size;
	},
	getMarker: function(key) {
		return this.keyToMarker[key];
	},
	hasKey: function(key) {
		return (this.keyToMarker.hasOwnProperty(key));
	},
	showMarkers: function() {
		$.each(this.keyToMarker, function(i, marker) {
			marker.show();
		});
	},
	hideMarkers: function() {
		$.each(this.keyToMarker, function(i, marker) {
			marker.hide();
		});
	},
	addMarkersToMap: function() {
//		console.log(this.keyToMarker);
		$.each(this.keyToMarker, function(i, marker) {
			marker.addToMap();
		});
	},
	removeMarkersFromMap: function() {
		$.each(this.keyToMarker, function(i, marker) {
			marker.removeFromMap();
		});
	}
});

/**
 * Customized MarkerManager for Science map purpose. It is be an abstract class 
 */
ScimapMarkerManager = MarkerManager.extend({
	init: function(map, colorStrategy, sizeCoder) {
		this._super();
		this.colorStrategy = colorStrategy;
		this.sizeCoder = sizeCoder;
		this.map = map;
		this.layer = {};
	},
	setSizeCoder: function(sizeCoder) {
		this.sizeCoder = sizeCoder;
	},
	createMarker: function(key, density) {
		var me = this;
		var marker;
		if (!me.hasKey(key)) {
			var size = me.sizeCoder.getSize(density);
			var color = me.colorStrategy.getColor(key);
			var layer = me.layer;
			var label = layer[key].label;
			var latlng = createNoWrapLatLng(layer[key].latitude, layer[key].longitude);
			marker = createScinodeMarker(me.map, label, density, size, color, latlng);
			me.addMarker(key, marker);
		} else {
			marker = me.keyToMarker[key];
			marker.setValue(marker.getValue() + density);
			marker.setSize(me.sizeCoder.getSize(marker.getValue()));
		}
		return marker;
	},
	updateMarkerViews: function() {
		var me = this;
		for (var key in me.keyToMarker) {
			var marker = me.keyToMarker[key];
			marker.setSize(me.sizeCodingFunc(marker.getValue()));
			marker.setColor(me.colorStrategy.getColor(key));
		}
	},
	display: function(numberOfMarkers) {
		$.each(this.keyToMarker, function(i, marker) {
			if (i <=  numberOfMarkers) {
				marker.show();
			} else {
				marker.hide();
			}
		});
	},
	mouseIn: function(key) {
		var marker = this.getMarker(key);
		if (marker) {
			marker.focus();
		}
	},
	mouseInAll: function() {
		$.each(this.keyToMarker, function(i, marker) {
			marker.focus();
		});
	},
	mouseOut: function(key) {
		var marker = this.getMarker(key);
		if (marker) {
			marker.unfocus();
		}
	},
	mouseOutAll: function() {
		$.each(this.keyToMarker, function(i, marker) {
			marker.unfocus();
		});
	}
});

var DisciplineMarkerManager = ScimapMarkerManager.extend({
	init: function(map, colorStrategy, sizeCoder) {
		this._super(map, colorStrategy, sizeCoder);
		this.layer = DISCIPLINES;
	},
	createMarker: function(subdisciplineKey, density) {
		var me = this;
		var key = SUBDISCIPLINES[subdisciplineKey].discipline;
		return this._super(key, density);
	}
});

var SubdisciplineMarkerManager = ScimapMarkerManager.extend({
	init: function(map, colorStrategy, sizeCoder) {
		this._super(map, colorStrategy, sizeCoder);
		this.layer = SUBDISCIPLINES;
	}
});
