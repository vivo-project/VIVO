/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var ENTITY_VIS_MODE = "ENTITY";

var EntityVisModeController = Class.extend({
	init: function(map, sliderControl) {
		this.visMode = ENTITY_VIS_MODE;
		this.isUnloaded = true;
		this.initWidgets(map, sliderControl);
	},
	initFilter: function() {
		
		var dom = {
			disciplineFilterID: "discipline-filter",
			subdisciplinesFilterID: "subdisciplines-filter",
			filterOptionClass: "filter-option",
			activeFilterClass: "active-filter"
		},
		
		$("." + dom.filterOptionClass).live('click', function() {
			if (!$(this).hasClass(dom.activeFilterClass)) {
				if ($(this).attr('id') === dom.subdisciplinesFilterID) {
					$("#" + dom.disciplineFilterID).removeClass(dom.activeFilterClass);
					$.each(this.widgets, function(i, widget) {
						widget.changeFilter(SCIMAP_TYPE.SUBDISCIPLINE);
					});
					
				} else if ($(this).attr('id') === dom.disciplineFilterID) {
					$("#" + dom.subdisciplinesFilterID).removeClass(dom.activeFilterClass);
					$.each(this.widgets, function(i, widget) {
						widget.changeFilter(SCIMAP_TYPE.DISCIPLINE);
					});
				}
					
				$(this).addClass('active-filter');	
			}
		});
	},
	initWidgets: function(map, sliderControl) {
		var widgets = {};
		widgets['scimap'] = new ScimapWidget(map, sliderControl);
		widgets['sci_area_table'] = new DataTableWidget();
		
		this.widgets = widgets;
	},
	needLoaded: function() {
		return this.isUnloaded;
	},
	initView: function() {
		$.each(this.widgets, function(i, widget) {
			widget.initView();
		});
	},
	loadData: function(url, sync) {
		
		// Download data from server and add to markerManager if not gotten already
		var me = this;
		if (me.isUnloaded) {
			if (sync) {
				downloader.downloadAndWait(url, function(data) {
						me.loadJsonData(me, data[0]);
				});
			} else {
				downloader.download(url, function(data) {
						me.loadJsonData(me, data[0]);
				});
			}
		} // end if
	},
	loadJsonData: function(me, data) {
		$.each(me.widgets, function(i, widget) {
			widget.loadJsonData(data);
		});
		me.isUnloaded = false;
		$("#" + responseContainerID).unblock();
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
	cleanUp: function() {
		$.each(this.widgets, function(i, widget) {
			widget.cleanUp(key);
		});
	}
});