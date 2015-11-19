/* $This file is distributed under the terms of the license in /doc/license.txt$ */
$.extend(this, i18nStrings);

COMPARISON = {
	"one":{ "name": "one", "color": "#99CC00"},
	"two":{ "name": "two", "color": "#FF9900"},
	"three":{ "name": "three", "color": "#3399FF"}
}

COMPARISON_TYPE = {
	"ORGANIZATION": "ORGANIZATION",
	"PERSON": "PERSON"
}

var ManagerRegistry = Class.extend({
	init: function(itemArray) {
		this.occupied = {};
		this.availability = itemArray;
	},
	register: function(key) {
		var me = this;
		if (me.occupied[key] == null && me.isAvailable()) {
			me.occupied[key] = me.availability.pop();
		}
		
		return me.occupied[key];
	},
	unregister: function(key) {
		var me = this;
		var item = me.occupied[key];
		if (item) {
			me.availability.push(me.occupied[key]);
			delete me.occupied[key];
		}
		
		return item;
	},
	isAvailable: function() {
		return (this.availability.length != 0);
	}
});

var ComparisonScimapWidget = Class.extend({
	init: function(map) {
		var me = this;
		me.activeCompositeManager = null;
		me.map = map;
		me.initView();
	},
	initView: function(){
		var me = this;
		me.initControlPanels();
		me.initMarkerManagers();
		me.show(COMPARISON_TYPE.ORGANIZATION);
		this.activeCompositeManager.addManagersToMap();
		this.updateDisplayedMarkers();
	},
	cleanView: function() {
		var me = this;
		me._cleanUpManagers();
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
			if (me.keyToCompositeManager) {
				me.updateDisplayedMarkers();
			}
		});
		
		/* create */
		if (me.disciplineLabelsControl == null) {
			me.labelsMarkerManager = new DisciplineLabelsMarkerManager(map, getDisciplineBlackLabelImageURL);
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
		if (this.keyToCompositeManager == null) {
			
			// Create all the marker managers
			var me = this;
			me.keyToCompositeManager = {};
			$.each(COMPARISON_TYPE, function(type, value) {
				var managerArray = [];
				
				$.each(COMPARISON, function(key, attrs) {
					var manager = new SubdisciplineMarkerManager(
						me.map,
						new SingleColorStrategy(attrs.color), 
						null
					);
					manager.color = attrs.color;
					managerArray.push(manager);
				});
				me.keyToCompositeManager[type] = new CompositeMarkerManager();
				me.keyToCompositeManager[type].registry = new ManagerRegistry(managerArray);
			});
		}
	},
	loadJsonData: function(data) {
	},
	loadEntity: function(data) {
		var me = this;
		me.uri = data.uri;
		me.label = data.label;
		me.pubsWithNoJournals = data.pubsWithNoJournals;
		me.pubsWithInvalidJournals = data.pubsWithInvalidJournals;
		me.pubsMapped = data.pubsMapped;
		
		var scienceActivities = data.subdisciplineActivity;
		
		var childKey = data.label;
		var compositeManager = me.getCompositeManager(data.type);
		// Avoid reload if data was loaded
		if (compositeManager.hasKey(childKey)) {
			me.updateMap();
		} else {
			var manager = compositeManager.registry.register(childKey);
			if (manager) {
				// clean previous markers
				manager.removeAll();
				
				// Need to create the AreaSizeCoding function
				manager.setSizeCoder(new CircleSizeCoder({ 
					scaler: new Scaler({ maxValue: me.pubsMapped }) 
				}));
				
				$.each(scienceActivities, function(science, density) {
			
					// Create marker and add it to manager
					manager.createMarker(science, density);
					
				}); // end each scienceActivity
				manager.sort();
				
				compositeManager.addManager(childKey, manager);
				me.updateMap();
			}
		}
	},
	unloadEntity: function(key, childKey) {
		this.removeManager(key, childKey);
	},
	getCompositeManager: function(key) {
		return this.keyToCompositeManager[key];
	},
	mouseIn: function(key, childKey) {
		var compositeManager = this.getCompositeManager(key);
		
		// Focus if only it is a valid manager
		if (compositeManager == this.activeCompositeManager) {
			// Focus all
			compositeManager.mouseIn(childKey);
		}
	},
	mouseOut: function(key, childKey) {
		var compositeManager = this.getCompositeManager(key);
		
		// Unfocus if only it is a valid manager
		if (compositeManager == this.activeCompositeManager) {
			// Unfocus all
			compositeManager.mouseOut(childKey);
		}
	},
	mouseInNode: function(key, childKey, subdisciplineId) {
		var compositeManager = this.getCompositeManager(key);
		// Focus if only it is a valid manager
		if (compositeManager == this.activeCompositeManager) {
			// Focus all
			compositeManager.registry.register(childKey).mouseIn(subdisciplineId);
		}
	},
	mouseOutNode: function(key, childKey, subdisciplineId) {
		var compositeManager = this.getCompositeManager(key);
		
		// Unfocus if only it is a valid manager
		if (compositeManager == this.activeCompositeManager) {
			compositeManager.registry.register(childKey).mouseOut(subdisciplineId);
		}
	},
	removeManager: function(key, childKey) {
		var compositeManager = this.getCompositeManager(key);
		
		// Remove manager
		if (compositeManager == this.activeCompositeManager) {
			this.activeCompositeManager.removeManager(childKey);
			this.activeCompositeManager.registry.unregister(childKey);
		}
	},
	show: function(key) {
		var compositeManager = this.getCompositeManager(key);
		if (compositeManager) {
			this._switchActiveCompositeManager(compositeManager);
		}
	},
	hide: function(key) {
		var compositeManager = this.getCompositeManager(key);
		if (this.activeCompositeManager == compositeManager) {
			this._cleanupMarkers();
		}
	},
	_switchActiveCompositeManager: function(compositeManager) {
		if (this.activeCompositeManager != compositeManager) {
			this._cleanUpManagers();
			compositeManager.addManagersToMap();
			this.activeCompositeManager = compositeManager;
			this.updateMap();
		}
	},
	_cleanUpManagers: function() {
		if (this.activeCompositeManager) {
			this.activeCompositeManager.removeManagersFromMap();
			INFO_WINDOW.close();
		}
	},
	updateDisplayedMarkers: function() {
		this.activeCompositeManager.display(this.sliderControl.getValue());
	},
	updateMap: function() {
		var compositeManager = this.activeCompositeManager;
		if (compositeManager) {
			var length = compositeManager.length();
			var slider = this.sliderControl;
			slider.setTypeString(i18nStrings.subdisciplinesLower);
			slider.setMin(Math.min(1, length));
			slider.setMax(length);
			slider.setValue(length);
		}
	},
	changeFilter: function(filterType) {
		this.show(filterType);
	},
	getColor: function(key, childKey) {
		return this.getCompositeManager(key).registry.register(childKey).color;
	}
});
