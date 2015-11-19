/* $This file is distributed under the terms of the license in /doc/license.txt$ */
/**
 * Marker Object for that hold external information - data. Please refer to the
 * Google.map.MakerOptions for options' details
 */

var Marker = Class.extend({
	init : function(options) {
		this.options = $.extend({}, this.options, options);
		this.marker = createGoogleMarker(this.options);
		this.hide();
		this.registerEvents();
	},
	options : {
		value : 0,
		map : null,
		icon : null,
		position : null,
		content : null
	},
	addToMap : function() {
		this.marker.setMap(this.options.map);
		this.registerEvents();
	},
	removeFromMap : function() {
		this.marker.setMap(null);
		this.unregisterEvents();
	},
	show : function() {
		this.marker.setVisible(true);
	},
	hide : function() {
		this.marker.setVisible(false);
	},
	setIcon : function(icon) {
		this.marker.setIcon(icon);
	},
	setZIndex: function(zIndex){
		this.marker.setZIndex(zIndex);
	},
	setTitle : function(title) {
		this.marker.title = title;
	},
	registerEvents : function() {
		var handlers = new Array();
		var marker = this.marker;
		handlers.push(addClickListener(marker, function() {
			updateIFrame(this.url);
		}));
		this.handlers = handlers;
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
