/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var ScimapWidget = Class.extend({
	init: function(map, sliderControl) {
		var me = this;
		me.activeManager = null;
		me.isUnloaded = true;
		me.map = map;
		
		me.sliderControl = sliderControl;
		me.labelsMarkerManager = new DisciplineLabelsMarkerManager(map);
		me.disciplineLabelsControl = new CheckBoxPanel({ 
			map: map,
			checked: true,
			text: "Show discipline labels",
			click: function() {
				if($(this).attr('checked')) {
					me.labelsMarkerManager.showMarkers();
				} else {
					me.labelsMarkerManager.hideMarkers();
				}
			}
		});
		me.initView();
	},
	initView: function(){
		var me = this;
		/* Display labels if checked */
		if (me.disciplineLabelsControl.isChecked()) {
			me.labelsMarkerManager.showMarkers();
		}
		me.initMarkerManagers();
		me.sliderControl.setChangeEventHandler(function(event, ui) {
			me.updateDisplayedMarkers(ui.value);
		});
		me.show(SCIMAP_TYPE.SUBDISCIPLINE);
	},
	initMarkerManagers: function() {
		if (this.keyToMarkerManagers == null) {
			var managers = {};
			
			// Create discipline Marker Manager
			managers[SCIMAP_TYPE.DISCIPLINE] = new DisciplineMarkerManager(
				this.map, 
				new DisciplineColorStrategy(), 
				null
			);
			
			// Create subdiscipline Marker Manager
			managers[SCIMAP_TYPE.SUBDISCIPLINE] = new SubdisciplineMarkerManager(
				this.map,
				new SubdisciplineColorStrategy(), 
				null
			);
			this.keyToMarkerManagers = managers;
		}
	},
	needLoaded: function(){
		return this.isUnloaded;
	},
	loadJsonData: function(data) {
		var me = this;
		me.uri = data.uri;
		me.label = data.label;
		me.pubsWithNoJournals = data.pubsWithNoJournals;
		me.pubsWithInvalidJournals = data.pubsWithInvalidJournals;
		me.pubsMapped = data.pubsMapped;
		
		var scienceActivities = {};
		scienceActivities[SCIMAP_TYPE.DISCIPLINE] = me._collateDisciplineActivity(data.subdisciplineActivity);
		scienceActivities[SCIMAP_TYPE.SUBDISCIPLINE] = data.subdisciplineActivity;
		
		this.isUnloaded = false;
		
		$.each(this.keyToMarkerManagers, function(key, manager) {
			
			// Need to create the AreaSizeCoding function
			manager.setSizeCoder(new CircleSizeCoder({ 
				scaler: new Scaler({ maxValue: me.pubsMapped }) 
			}));
			
			$.each(scienceActivities[key], function(science, density) {
		
				// Create marker and add it to manager
				var marker = manager.createMarker(science, density);
				
			}); // end each scienceActivity
			
			manager.sort();
		}); // end each markerManagers
		me.updateMap();
	},
	
	_collateDisciplineActivity: function(subdiscipline) {
		
		var disciplineToActivity = {};
		
		$.each(DISCIPLINES, function(id, discipline) {
			disciplineToActivity[id] = 0.0;
		});
		
		$.each(subdiscipline, function(key, activity) {
			var currentSubdisciplinesDiscipline = SUBDISCIPLINES[key].discipline;
			disciplineToActivity[currentSubdisciplinesDiscipline] += activity; 
		});
		
		return disciplineToActivity;
	},
	
	mouseIn: function(key, childKey) {
		var manager = this.getMarkerManager(key);
		// Focus if only it is an active manager
		if (manager == this.activeManager) {
			// Focus all if no childKey is given
			if (childKey) {
				manager.mouseIn(childKey);
			} else {
				manager.mouseInAll();
			}
		}
	},
	mouseOut: function(key, childKey) {
		var manager = this.getMarkerManager(key);
		// Focus if only it is an active manager
		if (manager == this.activeManager) {
			// Unfocus all if no childKey is given
			if (childKey) {
				manager.mouseOut(childKey);
			} else {
				manager.mouseOutAll();
			}
		}
	},
	getMarkerManager: function(key) {
		return this.keyToMarkerManagers[key];
	},
	hasKey: function(key) {
		return (this.keyToMarkerManagers.hasOwnProperty(key));
	},
	show: function(key) {
		var manager = this.getMarkerManager(key);
		if (manager) {
			this._switchActiveManager(manager);
		}
	},
	hide: function(key) {
		var manager = this.getMarkerManager(key);
		if (this.activeManager == manager) {
			this.cleanup();
		}
	},
	_switchActiveManager: function(manager) {
		if (this.activeManager != manager) {
			this.cleanUp();
			manager.addMarkersToMap();
			this.activeManager = manager;
			this.updateMap();
		}
	},
	cleanUp: function() {
		if (this.activeManager) {
			this.activeManager.removeMarkersFromMap();
			INFO_WINDOW.close();
		}
	},
	updateDisplayedMarkers: function(numberOfMarkers) {
		this.activeManager.display(numberOfMarkers);
	},
	updateMap: function() {
		var manager = this.activeManager;
		if (manager) {
			var length = manager.length();
			var slider = this.sliderControl;
			slider.setMin(Math.min(1, length));
			slider.setMax(length);
			slider.setValue(length);
		}
	},
	changeFilter: function(filterType) {
		this.show(filterType);
	}
});