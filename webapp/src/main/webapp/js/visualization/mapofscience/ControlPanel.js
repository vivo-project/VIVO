/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var ControlPanel = Class.extend({
	init: function(options) {
		this.options = $.extend({}, this.options, options);
		this.initDiv();
	},
	options: {
		divClass: null, // use this to set absolute position and style
		controlPositions: google.maps.ControlPosition.RIGHT_TOP, // Refer to API for alternative value
		map: null, // map to be add to
		jQueryDiv: null // add custom div
	},
	initDiv: function() {
		var opt = this.options;
		if (opt.jQueryDiv == null) {
			opt.jQueryDiv = $(document.createElement('div'));
		}
		
		if (opt.divClass) {
			opt.jQueryDiv.addClass(opt.divClass);
		}
		this.div = opt.jQueryDiv[0];
	},
	getDiv: function() {
		/* Allow to edit everything start from div level by returning div jquery object */
		return this.options.jQueryDiv;
	},
	hide: function() {
		var div = this.div;
		if (div) {
			div.style.display = "none";
		}
	},
	show: function() {
		var div = this.div;
		if (div) {
			div.style.display = "block";
		}
	},
	addToMap: function() {
		var opt = this.options;
		opt.map.controls[opt.controlPositions].push(this.div);
	},
	removeFromMap: function() {
		var opt = this.options;
		opt.map.controls[opt.controlPositions].pop();
	}
});

var SliderControlPanel = ControlPanel.extend({
	init: function(options) {
		this._super(options);
		this.initSlider();
	},
	initSlider: function() {
		var me = this;
		me.typeString = "";
		var label = $("<div />").width(150).css("font-size", "75%").css("text-align", "center").text("");
		var slider = $("<div />").width(150).css("font-size","60%");
		slider.slider({
			slide:  function(event, ui) {
		    	me._setLabel(ui.value);
		    }
		});
		
		this.sliderDiv = slider;
		this.labelDiv = label;
		
		var div = me.getDiv();
		div.css("margin-right", "10px");
		div.append(label);
		div.append(slider);
	},
	getValue: function () {
		return this.sliderDiv.slider( "option", "value" );
	},
	setMin: function(min) {
		this.sliderDiv.slider({
			min: min
		});
	},
	setMax: function(max) {
		this.sliderDiv.slider({
			max: max
		});
	},
	setValue: function(value) {
		this.sliderDiv.slider({
			value: value
		});
		this._setLabel(value);
	},
	setTypeString: function(typeString) {
		this.typeString = typeString;
	},
	_setLabel: function(value) {
		var labelText = "Top " + value + " " + this.typeString + " shown";
        this.labelDiv.text(labelText);
	},
	setChangeEventHandler: function(handlerFunc) {
		this.sliderDiv.slider({ 
			change: handlerFunc
		});
	}
});

/**
 * options
 * map - Container map to be added
 * click - Handler function for click event
 * text - Description of the check Box
 */
var CheckBoxPanel = ControlPanel.extend({
	init: function(options) {
		this._super(options);
		this.initCheckBox();
	},
	initCheckBox: function() {
		var me = this;
		var description
		var text = me.options.text;
		var checkBox = $('<input type="checkbox"><span style="font-size: 75%; vertical-align: text-top">'
				+ text
				+ '</span></input>'
			);
		me.checkBox = checkBox;
		
		var div = me.getDiv();
		div.css("margin-right", "10px");
		div.append(checkBox);
		
		me.checkBox.attr('checked', me.options.checked);
		/* Init contents if it is given */
		var click = me.options.click;
		if (click) {
			me.click(click);
		}
	},
	click: function(handlerFunc) {
		this.checkBox.click(handlerFunc);
	},
	isChecked: function() {
		return this.checkBox.attr('checked');
	}
});

/**
 * Copyright
 */
var CopyrightPanel = ControlPanel.extend({
	init: function(options) {
		this._super(options);
		this.initCopyRight();
		this.addToMap();
	},
	initCopyRight: function() {
		var me = this;
		var html = me.options.html;
		var copyright = $("<div />").css("padding", "0px 5px 2px 5px").css("font-size", "75%").css("text-align", "center");
		if (html) {
			copyright.html(html);
		}
		me.copyright = copyright;
		
		me.getDiv().append(copyright);
	}
});
