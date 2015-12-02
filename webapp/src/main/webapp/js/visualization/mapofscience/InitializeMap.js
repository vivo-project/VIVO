/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$.extend(this, i18nStrings);

var map;
var downloader;
var currentVisMode;
var currentController;
var visModeControllers = {};
var responseContainerID = "map-of-science-response";
var ERROR_DISPLAY_WIDGET = '';

var loadingScreenTimeout;

/*
 * This method will setup the options for loading screen & then activate the 
 * loading screen.
 */
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
        			+ '" />&nbsp;' + i18nStrings.loadingDataFor + ' <i>' 
        			+ entityLabel
        			+ '</i></h3></div>'
    });
    
    clearTimeout(loadingScreenTimeout);
    
    loadingScreenTimeout = setTimeout(function() {
    	$("#loading-data-container")
    		.html('<h3><img id="refresh-page-icon" src="' 
    				+ refreshPageImageLink 
	    			+ '" />&nbsp;Data for <i>' + entityLabel
	    			+ '</i> ' + i18nStrings.mapBeingRefreshed + '</h3>')
	    	.css({'cursor': 'pointer'});
    }, 10 * 1000);
}

function initMap() {
	var gMap = google.maps;
	var centerLatLng = new google.maps.LatLng(55, -10);
	
	var mapOptions = {
		center: centerLatLng,
		zoom: 1,
		streetViewControl: false,
		mapTypeControlOptions: {
		  mapTypeIds: []
		}
	};
	
	var mapAreaId = $("#map_area");
	map = new gMap.Map(mapAreaId[0], mapOptions);
	
	var mapName = 'Scimap';
	createScimapType(map, mapName);
	map.setMapTypeId(mapName);
	
	downloader = new DownloadManager();
	
}

function initVisModeController() {
	var controller = getVisModeController(ENTITY_VIS_MODE);
	switchVisMode(controller.visMode);
	initVisModeTypeButton();
	initGlobalToolTips();
	currentController.loadData(scienceMapDataURL, false);
}

function helper() {
	/* override helper function to avoid reload script */
}

/* Using .load instead of .ready due to issue with IE and Google Maps API */
$(window).load(function() {
	
	ERROR_DISPLAY_WIDGET = new ErrorDisplayWidget({
		containerID: 'error-container'
	});
	
	setupLoadingScreen();
	initMap();
	initVisModeController();
});