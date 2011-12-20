/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var INFO_WINDOW = createInfoWindow("", "300");

var Polygon = Class.extend({
	init : function(options) {
		this.options = $.extend({}, this.options, options);
		if (options.polygon) {
			this.polygon = options.polygon;
		} else {
			this.polygon = createGooglePolygon(options);
		}
		this.registerEvents();
	},
	options : {
		map : null,
		icon : null,
		position : null,
		content : null
	},
	addToMap : function() {
		this.polygon.setMap(this.options.map);
		this.registerEvents();
	},
	removeFromMap : function() {
		this.unregisterEvents();
		this.polygon.setMap(null);
	},
	show : function() {
		this.polygon.setMap(this.options.map);
	},
	hide : function() {
		this.polygon.setMap(null);
	},
	setIcon : function(icon) {
	},
	setZIndex: function(zIndex){
		this.polygon.zIndex = zIndex;
	},
	setTitle : function(title) {
		this.polygon.title = title;
	},
	setOptions: function(options) {
		this.polygon.setOptions(options);
	},
	registerEvent : function(handler) {
		var me = this;
		if (me.handlers == null) {
			me.handlers = new Array();
		}
		me.handlers.push(handler);
	},
	unregisterEvent : function(handler) {
		if (this.handlers[handler]) {
			removeListener(handler);
			delete(this.handlers[handler]);
		}
	},
	registerEvents : function() {
	},
	unregisterEvents : function() {
		if (this.handlers) {
			$.each(this.handlers, function(i, handler) {
				removeListener(handler);
			});
			this.handlers = null;
		}
	}
});

var RADIAN_PER_DEGREE = Math.PI / 180;

function degreeToRadians(degree) {
	return degree * RADIAN_PER_DEGREE;
}

var TOOLTIP = new Tooltip({ attachedToMouse: true });

var CirclePolygon = Polygon.extend({
	init : function(options) {
		this.options = $.extend({}, this.options, options);
		this._super(this.options);
	},
	options : {
		radius: 0.0,
		center: null
	},
	addToMap: function() {
		if (!this.isPointsCreated()) {
			this.initCirclePoints();
		}
		this._super();
	},
	show: function() {
		if (!this.isPointsCreated()) {
			this.initCirclePoints();
		}
		this._super();
	},
	isPointsCreated: function() {
		return (this.polygon.getPath().getLength() > 1);
	},
	initCirclePoints: function() {
		this.clearCirclePoints();
		this.createCirclePoints();
	},
	createCirclePoints: function() {
		var me = this;
		var map = me.options.map;
		var latLngArray = new google.maps.MVCArray(); // Circle's LatLngs
		if (map && map.getProjection()) {
			var projection = map.getProjection();
			var centerPoint = projection.fromLatLngToPoint(me.options.center);
			var radius = me.options.radius;
			
			var incrementDegreeBy = (radius > 2) ? 1 : 10;
			
			// Create polygon points (extra point to close polygon)
			for (var degree = 0; degree < 360; degree+=incrementDegreeBy) {
				var radian = degreeToRadians(degree);
				var x = centerPoint.x + (radius * Math.sin(radian));
				var y = centerPoint.y + (radius * Math.cos(radian));
				var point = new google.maps.Point(parseFloat(x), parseFloat(y));
				latLngArray.push(projection.fromPointToLatLng(point));
			}
		}
		me.polygon.setPath(latLngArray);
	},
	clearCirclePoints: function() {
		this.polygon.getPath().clear();
	},
	setRadius: function(radius) {
		this.polygon.radius = radius;
		this.options.radius = radius;
		this.initCirclePoints();
	},
	registerEvents: function() {
		var me = this;
		var polygon = me.polygon;
		this.registerEvent(addMapProjectionChangedListener(me.options.map, function() {
			me.initCirclePoints(); 
		}));
		
		this.registerEvent(addMouseOverListener(polygon, function() {
			TOOLTIP.setHtml("<b>" + this.label + "</b>");
			TOOLTIP.show();
		}));
		
		this.registerEvent(addMouseOutListener(polygon, function() {
			TOOLTIP.hide();
		}));
	}
});