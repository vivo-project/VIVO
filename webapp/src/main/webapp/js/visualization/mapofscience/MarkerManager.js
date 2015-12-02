/* $This file is distributed under the terms of the license in /doc/license.txt$ */
/**
 * The MarkerManager is more like a composite class of Marker. It manages 
 * markers by grouping the markers by keys. 
 */

$.extend(this, i18nStrings);

var MarkerManager = Class.extend({
	init: function() {
		this.keyToMarker = {};
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
	getMarkerArray: function() {
		var array = [];
		$.each(this.keyToMarker, function(i, e){ array.push(e); });
		return array;
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
		$.each(this.keyToMarker, function(i, marker) {
			marker.addToMap();
		});
	},
	removeMarkersFromMap: function() {
		$.each(this.keyToMarker, function(i, marker) {
			marker.removeFromMap();
		});
	},
	removeAll: function() {
		this.removeMarkersFromMap();
		this.keyToMarker = {};
	}
});

/**
 * Customized Discipline labels MarkerManager for Science map purpose. It is an abstract class 
 */
var DisciplineLabelsMarkerManager = MarkerManager.extend({
	init: function(map, getLabelURL) {
		this._super();
		this.map = map;
		this.getLabelURL = getDisciplineLabelImageURL;
		if (getLabelURL != null) {
			this.getLabelURL = getLabelURL;
		}
		this.initMarkers(map);
	},
	initMarkers: function(map) {
		me = this;
		$.each(DISCIPLINES, function(id, discipline) {
			var opts = {
					map: map,
					position: createNoWrapLatLng(discipline.labelLatitude, discipline.labelLongitude),
					icon: me.getLabelURL(id),
					clickable: false
				};
			me.addMarker(id, new Marker(opts));
		});
	},
	showMarkers: function() {
		this._super();
	}
});

/**
 * Customized MarkerManager for Science map purpose. It is an abstract class 
 */
var ScimapMarkerManager = MarkerManager.extend({
	init: function(map, colorStrategy, sizeCoder) {
		this._super();
		this.colorStrategy = colorStrategy;
		this.sizeCoder = sizeCoder;
		this.map = map;
		this.maxValue = 1;
		this.layer = {};
	},
	setSizeCoder: function(sizeCoder) {
		this.sizeCoder = sizeCoder;
		this.maxValue = sizeCoder.getMaxValue();
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
		var markerArray = this.sortedMarkers;
		if (!markerArray || !markerArray.length) {
			markerArray = this.getMarkerArray();
		}
		
		$.each(markerArray, function() {
			if (numberOfMarkers >  0) {
				this.show();
				numberOfMarkers--;
			} else {
				this.hide();
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
	},
	sort: function() {
		this.sortedMarkers = this.getMarkerArray();
		this.sortedMarkers.sort(function(a, b) {
			return b.getValue() -a.getValue();
		});
	}
});

var DisciplineMarkerManager = ScimapMarkerManager.extend({
	init: function(map, colorStrategy, sizeCoder) {
		this._super(map, colorStrategy, sizeCoder);
		this.layer = DISCIPLINES;
	},
	createMarker: function(key, density) {
		var me = this;
		var marker = this._super(key, density);
		var poly = marker.polygon;
		
		marker.setContent( 
				'<div style="font-size: 80%; padding: 5px; text-align: left;"><b>'
				+ poly.label +'</b><br />'
				+ addCommasToNumber(poly.value.toFixed(2)) + ' ' + i18nStrings.publicationsPubs + '<br />'
				+ (poly.value * 100 / this.maxValue).toFixed(2) + i18nStrings.percentActivity
		);
		return marker;
	}
});

var SubdisciplineMarkerManager = ScimapMarkerManager.extend({
	init: function(map, colorStrategy, sizeCoder) {
		this._super(map, colorStrategy, sizeCoder);
		this.layer = SUBDISCIPLINES;
	},
	createMarker: function(subdisciplineKey, density) {
		var marker = this._super(subdisciplineKey, density);
		var disciplineId = SUBDISCIPLINES[subdisciplineKey].discipline;
		var disciplineLabel = DISCIPLINES[disciplineId].label;
		var poly = marker.polygon;
		/* Override the getContent for Subdiscipline */
		marker.setContent(
			'<div style="font-size: 80%; padding: 5px; text-align: left;"><b>'
			+ poly.label + '</b> in ' + disciplineLabel +'<br />' 
			+ addCommasToNumber(poly.value.toFixed(2)) + ' ' + i18nStrings.publicationsPubs + '<br />'
			+ (poly.value * 100 / this.maxValue).toFixed(2) + i18nStrings.percentActivity
		);
		
		return marker;
	}
});

var CompositeMarkerManager = Class.extend({
	init: function() {
		this.keyToManager = {};
	},
	addManager: function(key, manager) {
		this.keyToManager[key] = manager;
	},
	length: function() {
		var size = 0;

		$.each(this.keyToManager, function(i, manager) {
			msize = manager.length();
			if (size < msize) {
				size = msize;
			}
		});
		return size;
	},
	getManager: function(key) {
		return this.keyToManager[key];
	},
	getManagerArray: function() {
		var array = [];
		$.each(this.keyToManager, function(i, e){ 
			array.push(e); 
		});
		return array;
	},
	hasKey: function(key) {
		return (this.keyToManager.hasOwnProperty(key));
	},
	showManager: function() {
		$.each(this.keyToManager, function(i, manager) {
			manager.showMarkers();
		});
	},
	hideManager: function() {
		$.each(this.keyToManager, function(i, manager) {
			manager.hideMarkers();
		});
	},
	addManagersToMap: function() {
		$.each(this.keyToManager, function(i, manager) {
			manager.addMarkersToMap();
		});
	},
	removeManagersFromMap: function() {
		$.each(this.keyToManager, function(i, manager) {
			manager.removeMarkersFromMap();
		});
	},
	removeManager: function(key) {
		if (this.hasKey(key)) {
			this.getManager(key).removeAll();
			delete this.keyToManager[key];
		}
	},
	removeAll: function() {
		$.each(this.keyToManager, function(i, manager) {
			manager.removeAll();
		});
		this.keyToManager = {};
	},
	mouseIn: function(key) {
		var manager = this.getManager(key);
		if (manager) {
			manager.mouseInAll();
		}
	},
	mouseInAll: function() {
		$.each(this.keyToManager, function(i, manager) {
			manager.mouseInAll();
		});
	},
	mouseOut: function(key) {
		var manager = this.getManager(key);
		if (manager) {
			manager.mouseOutAll();
		}
	},
	mouseOutAll: function() {
		$.each(this.keyToManager, function(i, manager) {
			manager.mouseOutAll();
		});
	},
	display: function(numberOfMarkers) {
		$.each(this.keyToManager, function(i, manager) {
			manager.display(numberOfMarkers);
		});
	}
});
