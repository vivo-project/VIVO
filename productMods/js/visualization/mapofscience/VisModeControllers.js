/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var ENTITY_VIS_MODE = "ENTITY";

var EntityVisModeController = Class.extend({
	init: function(map) {
		this.keyToMarkerManagers = {};
		this.activeManager = null;
		this.visMode = ENTITY_VIS_MODE;
		this.isUnloaded = true;
		this.map = map;
		this.initMarkerManagers(map);
	},
	initMarkerManagers: function(map) {
		var managers = this.keyToMarkerManagers;
		
		// Create discipline Marker Manager
		managers['discipline'] = new DisciplineMarkerManager(
			map, 
			new DisciplineColorStrategy(), 
			null
		);
		
		// Create subdiscipline Marker Manager
		managers['subdiscipline'] = new SubdisciplineMarkerManager(
			map,
			new SubdisciplineColorStrategy(), 
			null
		);
	},
	initView: function(){
		this.show('subdiscipline');
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
		
		$.each(this.keyToMarkerManagers, function(key, manager) {
			// Need to create the AreaSizeCoding function
			manager.setSizeCoder(new CircleSizeCoder({ 
				scaler: new Scaler({ maxValue: me.pubsMapped }) 
			}));
			//markerManager.setSiseCodingFunction(new AreaSizeCoding(0, data.pubsMapped));
			$.each(data.subdisciplineActivity, function(subdiscipline, density) {
		
				// Create marker and add it to manager
				var marker = manager.createMarker(subdiscipline, density);
				
				if (isActiveVisMode(me.visMode) && manager == me.activeManager) {
					marker.show();
				}
			}); // end each subdisciplineActivity
		}); // end each markerManagers
		
		this.isUnloaded = false;
	},
	getMarkerManager: function(key) {
		return this.keyToMarkerManagers[key];
	},
	hasKey: function(key) {
		return (this.keyToMarkerManagers.hasOwnProperty(key));
	},
	show: function(key) {
		var manager = this.getMarkerManager(key);
		var activeManager = this.activeManager;
		if (activeManager != manager) {
			this.cleanUp();
			manager.addMarkersToMap();
		}
		this.activeManager = manager;
	},
	hide: function(key) {
		var manager = this.getMarkerManager(key);
		if (this.activeManager == manager) {
			this.cleanup();
		}
	},
	cleanUp: function() {
		if (this.activeManager) {
			this.activeManager.removeMarkersFromMap();
		}
	}
});