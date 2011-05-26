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
var dataTableWidgets = {};
var responseContainerID = "map-of-science-response";
var loadingScreenTimeout;

/*
 * This method will setup the options for loading screen & then activate the 
 * loading screen.
 * */
function setupLoadingScreen() {
	
    $.blockUI.defaults.overlayCSS = { 
            backgroundColor: '#fff', 
            opacity: 1.0
        };
        
    $.blockUI.defaults.css.width = '500px';
    $.blockUI.defaults.css.height = '100px';
    $.blockUI.defaults.css.border = '0px';
    
    $("#" + responseContainerID).block({
        message: '<div id="loading-data-container"><h3><img id="data-loading-icon" src="' + loadingImageLink 
        			+ '" />&nbsp;Loading data for <i>' 
        			+ entityLabel
        			+ '</i></h3></div>'
    });
    
    clearTimeout(loadingScreenTimeout);
    
    loadingScreenTimeout = setTimeout(function() {
    	$("#loading-data-container")
    		.html('<h3><img id="refresh-page-icon" src="' 
    				+ refreshPageImageLink 
	    			+ '" />&nbsp;Data for <i>' + entityLabel
	    			+ '</i> is now being refreshed. The visualization will load as soon as we are done computing, ' 
	    			+ 'or you can come back in a few minutes.</h3>')
	    	.css({'cursor': 'pointer'});
    	
    }, 10 * 1000);
}

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

function initDataTableWidget() {
	var widget = new DataTableWidget();
	dataTableWidgets[widget.widgetType] = widget;
}

function initMarkers() {
	downloader = new DownloadManager();
	loadMarkers(ENTITY_VIS_MODE, scienceMapDataURL, false);
}

function initMap() {
	setupLoadingScreen();
	loadMap();
	initMapControls();
	initVisModeController();
	initDataTableWidget();
	initMarkers();
}

function helper() {
	/* override helper function to avoid reload script */
}

/* Using .load instead of .ready due to issue with IE and Google Maps API */
$(window).load(function() {
	initMap();
});