/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var Polygon = Class.extend({
	init : function(options) {
		this.options = $.extend({}, this.options, options);
		if (options.polygon) {
			this.polygon = options.polygon;
		} else {
			this.polygon = createGoogleCirclePolygon(options);
		}
		this.hide();
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
		this.polygon.setMap(null);
		this.unregisterEvents();
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
	registerEvents : function() {
		var handlers = new Array();
		var polygon = this.polygon;
		handlers.push(addClickListener(polygon, function() {
			updateIFrame(this.url);
		}));
		this.handlers = handlers;
	},
	unregisterEvents : function() {
		removeListeners(this.handlers);
		this.handlers = null;
	}
});