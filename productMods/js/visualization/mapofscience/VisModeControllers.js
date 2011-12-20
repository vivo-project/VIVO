/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var ENTITY_VIS_MODE = "ENTITY";
var COMPARISON_VIS_MODE = "COMPARISON";

var dataMarket = {};

var VisModeController = Class.extend({
	init: function(map) {
		this.visMode = ENTITY_VIS_MODE;
		this.isUnloaded = true;
		this.initWidgets(map);
	},
	initWidgets: function(map) {
		this.widgets = {};
	},
	needLoaded: function() {
		return this.isUnloaded;
	},
	loadData: function(url, sync) {
		
		// Download data from server and add to markerManager if not gotten already
		var me = this;
		if (me.isUnloaded) {
			// Lazy loading
			if (!dataMarket[url]) {
				if (sync) {
					downloader.downloadAndWait(url, function(data) {
						dataMarket[url] = data;
						me.loadJsonData(me, data);
					});
				} else {
					downloader.download(url, function(data) {
						dataMarket[url] = data;
						me.loadJsonData(me, data);
					});
				}
			} else {
				me.loadJsonData(me, dataMarket[url]);
			}
		} // end if
	},
	loadJsonData: function(me, data) {
		
		$("#" + responseContainerID).unblock();
		
		if (ERROR_DISPLAY_WIDGET.isErrorConditionTriggered(data)) {
			$("#map-of-science-response").hide();
			ERROR_DISPLAY_WIDGET.show(ENTITY_TYPE, data);
			return;
		}
		
		data = data[0];
		
		$.each(me.widgets, function(i, widget) {
			widget.loadJsonData(data);
		});
		//me.initToolTipInfo();
		me.isUnloaded = false;
	},
	initView: function() {
		$.each(this.widgets, function(i, widget) {
			widget.initView();
		});
	},
	// key can be discippline or subdiscipline
	show: function(key) {
		$.each(this.widgets, function(i, widget) {
			widget.show(key);
		});
	},
	hide: function(key) {
		$.each(this.widgets, function(i, widget) {
			widget.hide(key);
		});
	},
	cleanView: function() {
		$.each(this.widgets, function(i, widget) {
			widget.cleanView();
		});
	},
	changeFilter: function(value) {
		var type = this.getFilterType(value);
		
		$.each(this.widgets, function(i, widget) {
			widget.changeFilter(type);
		});
	},
});

var EntityVisModeController = VisModeController.extend({
	init: function(map) {
		this._super(map);
		this.visMode = ENTITY_VIS_MODE;
	},
	getFilterType: function(value) {
		if (value === 1) {
			return SCIMAP_TYPE.SUBDISCIPLINE;
		}
		return SCIMAP_TYPE.DISCIPLINE;
	},
	initWidgets: function(map) {
		var widgets = {};
		widgets['scimap'] = new ScimapWidget(map);
		widgets['sci_area_table'] = new DataTableWidget(widgets['scimap']);
		
		this.widgets = widgets;
	}
});

var ComparisonVisModeController = VisModeController.extend({
	init: function(map) {
		this._super(map);
		this.visMode = COMPARISON_VIS_MODE;
	},
	getFilterType: function(value) {
		if (value === 1) {
			return COMPARISON_TYPE.ORGANIZATION;
		}
		return COMPARISON_TYPE.PERSON;
	},
	initWidgets: function(map) {
		var widgets = {};
		widgets['scimap'] = new ComparisonScimapWidget(map);
		widgets['entity_area_table'] = new EntityTablesWidget(widgets['scimap']);
		widgets['sci_area_table'] = new ComparisonDataTableWidget(widgets['scimap'], widgets['entity_area_table']);
		
		this.widgets = widgets;
	}
});