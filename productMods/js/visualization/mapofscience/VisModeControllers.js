/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var ENTITY_VIS_MODE = "ENTITY";

var EntityVisModeController = Class.extend({
	init: function(map, sliderControl) {
		this.visMode = ENTITY_VIS_MODE;
		this.isUnloaded = true;
		this.initWidgets(map, sliderControl);
		this.initFilter();
	},
	initFilter: function() {
		var widgets = this.widgets;
		var dom = {
			disciplineFilterID: "discipline-filter",
			subdisciplinesFilterID: "subdisciplines-filter",
			filterOptionClass: "filter-option",
			activeFilterClass: "active-filter"
		};
		
		$("." + dom.filterOptionClass).live('click', function() {
			if (!$(this).hasClass(dom.activeFilterClass)) {
				if ($(this).attr('id') === dom.subdisciplinesFilterID) {
					$("#" + dom.disciplineFilterID).removeClass(dom.activeFilterClass);
					$.each(widgets, function(i, widget) {
						widget.changeFilter(SCIMAP_TYPE.SUBDISCIPLINE);
					});
					
				} else if ($(this).attr('id') === dom.disciplineFilterID) {
					$("#" + dom.subdisciplinesFilterID).removeClass(dom.activeFilterClass);
					$.each(widgets, function(i, widget) {
						widget.changeFilter(SCIMAP_TYPE.DISCIPLINE);
					});
				}
					
				$(this).addClass('active-filter');	
			}
		});
		
		/* Init default filter */
		$("#" + dom.subdisciplinesFilterID).trigger('click');
	},
	initWidgets: function(map, sliderControl) {
		var widgets = {};
		widgets['scimap'] = new ScimapWidget(map, sliderControl);
		widgets['sci_area_table'] = new DataTableWidget(widgets['scimap']);
		
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
						me.loadJsonData(me, data);
				});
			} else {
				downloader.download(url, function(data) {
						me.loadJsonData(me, data);
				});
			}
		} // end if
	},
	loadJsonData: function(me, data) {
		
		$("#" + responseContainerID).unblock();
		
		if (data.error) {
			$("#map-of-science-response").hide();
			$("#error-container").show();
			return;
		} 
		
		data = data[0];
		
		$.each(me.widgets, function(i, widget) {
			widget.loadJsonData(data);
		});
		me.isUnloaded = false;
		me.initToolTipInfo();
		
	},
	initToolTipInfo: function() {

		$('.filterInfoIcon').each(function () {
		    
			var me = $(this);
			
			var tipText;
		    var tipLocation = "topLeft";
		    
		    if (me.attr('id') == 'imageIconOne') {
		        tipText = $('#toolTipOne').html();
		    } else if (me.attr('id') == 'imageIconTwo') {
		        tipText = $('#toolTipTwo').html();
		    } else if (me.attr('id') == 'searchInfoIcon') {
		        tipText = $('#searchInfoTooltipText').html();
		    } else {
		        tipText = $('#toolTipThree').html();
		        tipLocation = "topRight";
		    }
		    
		    me.qtip({
		        content: {
		            text: tipText
		        },
		        position: {
		            corner: {
		                target: 'center',
		                tooltip: tipLocation
		            }
		        },
		        show: {
		            when: {
		                event: 'mouseover'
		            }
		        },
		        hide: {
		            fixed: true // Make it fixed so it can be hovered over
		        },
		        style: {
		            padding: '6px 6px',
		            // Give it some extra padding
		            width: 500,
		            textAlign: 'left',
		            backgroundColor: '#ffffc0',
		            fontSize: '.7em',
		            padding: '6px 10px 6px 10px',
		            lineHeight: '14px'
		        }
		    });
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
	cleanUp: function() {
		$.each(this.widgets, function(i, widget) {
			widget.cleanUp(key);
		});
	}
});