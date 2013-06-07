/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$.extend(this, i18nStrings);

var ScimapWidget = Class.extend({
	init: function(map) {
		var me = this;
		me.activeManager = null;
		me.isUnloaded = true;
		me.map = map;
		
		me.initView();
	},
	initView: function(){
		var me = this;
		me.initControlPanels();
		me.initMarkerManagers();
		me.activeManager.addMarkersToMap();
		me.updateDisplayedMarkers();
	},
	cleanView: function() {
		var me = this;
		me._cleanUpMarkers();
		me.sliderControl.removeFromMap();
		me.disciplineLabelsControl.removeFromMap();
		me.labelsMarkerManager.removeMarkersFromMap();
	},
	initControlPanels: function() {
		var me = this;
		
		/* Create slider control panel */
		if (me.sliderControl == null) {
			me.sliderControl = new SliderControlPanel({ 
				map:me.map, 
				controlPositions: google.maps.ControlPosition.RIGHT_BOTTOM
			});
		}
		
		/* Register event */
		me.sliderControl.addToMap();
		me.sliderControl.setChangeEventHandler(function(event, ui) {
			if (me.keyToMarkerManagers) {
				me.updateDisplayedMarkers();
			}
		});
		
		/* create */
		if (me.disciplineLabelsControl == null) {
			me.labelsMarkerManager = new DisciplineLabelsMarkerManager(map);
			me.disciplineLabelsControl = new CheckBoxPanel({ 
				map: map,
				checked: true,
				text: i18nStrings.showDisciplineLabels,
				click: function() {
					if($(this).attr('checked')) {
						me.labelsMarkerManager.showMarkers();
					} else {
						me.labelsMarkerManager.hideMarkers();
					}
				}
			});
		}
		
		/* Display labels if checked */
		me.disciplineLabelsControl.addToMap();
		me.labelsMarkerManager.addMarkersToMap();
		if (me.disciplineLabelsControl.isChecked()) {
			me.labelsMarkerManager.showMarkers();
		}
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
			this.show(SCIMAP_TYPE.SUBDISCIPLINE);
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
			this._cleanupMarkers();
		}
	},
	_switchActiveManager: function(manager) {
		if (this.activeManager != manager) {
			this._cleanUpMarkers();
			manager.addMarkersToMap();
			this.activeManager = manager;
			this.updateMap();
		}
	},
	_cleanUpMarkers: function() {
		if (this.activeManager) {
			this.activeManager.removeMarkersFromMap();
			INFO_WINDOW.close();
		}
	},
	updateDisplayedMarkers: function() {
		this.activeManager.display(this.sliderControl.getValue());
	},
	updateMap: function() {
		var manager = this.activeManager;
		if (manager) {
			var length = manager.length();
			var slider = this.sliderControl;
			if (this.getMarkerManager(SCIMAP_TYPE.DISCIPLINE) == manager) {
				slider.setTypeString(i18nStrings.disciplinesLower);
			} else {
				slider.setTypeString(i18nStrings.subdisciplinesLower);
			}
			slider.setMin(Math.min(1, length));
			slider.setMax(length);
			slider.setValue(length);
		}
	},
	changeFilter: function(filterType) {
		this.show(filterType);
	}
});