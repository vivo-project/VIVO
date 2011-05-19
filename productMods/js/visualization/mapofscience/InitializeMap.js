/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var map;
var markerManager;
var universityModeMarkerManagers;
var textControl;
var legendControl;
var downloader;
var currentVisMode;
var currentController;
var visModeControllers = {};

function loadMap() {
	var gMap = google.maps;
	var centerLatLng = new google.maps.LatLng(50, 0);
	
	var mapOptions = {
		center: centerLatLng,
		zoom: 1,
		mapTypeControlOptions: {
		  mapTypeIds: []
		}
	};
	
	var mapAreaId = $("#map_area");
	map = new gMap.Map(mapAreaId[0], mapOptions);
	
	var mapName = 'Scimap';
	createScimapType(map, mapName);
	map.setMapTypeId(mapName);
}

function initMapControls() {
	//textControl = new TextControl({map: map, divClass: 'time_div'});
	//textControl.setText("1991");
}

function initVisModeController() {
	var controller = new EntityVisModeController(map);
	visModeControllers[controller.visMode] = controller;
	switchVisMode(controller.visMode);
}

function initMarkers() {
	downloader = new DownloadManager();
	loadMarkers(ENTITY_VIS_MODE, "smallSampleData.json", false);
}

function initMap() {
	loadMap();
	initMapControls();
	initVisModeController();
	initMarkers();
}

function helper() {
	/* override helper function to avoid reload script */
}

/* Using .load instead of .ready due to issue with IE and Google Maps API */
$(window).load(function() {
	initMap();
});





