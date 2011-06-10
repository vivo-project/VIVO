/* $This file is distributed under the terms of the license in /doc/license.txt$ */
/**
 * This is the Google MAP API utilities file to make sure all calls to 
 * Google API is correct. Please add all common utilities at here.
 */
var GMAPS = google.maps;
var GEVENT = google.maps.event;
var DEFAULT_POINT = new google.maps.Point(0, 0);

function createMarkerImage(url, width, height) {
	return new GMAPS.MarkerImage(
			url,
		    new GMAPS.Size(width, height), /* set the image viewable window size */
		    DEFAULT_POINT, /* Use this to cut the image */
		    // TODO Fix icons so their center at the right spot. Very low priority.
		    new google.maps.Point(width/2, height/2), /* use this to shift the marker location in pixels. Default */
			new GMAPS.Size(width, height)); /* set the desired image size */
}

function createNoWrapLatLng(lat, lng) {
	return new GMAPS.LatLng(lat, lng, true);
}

function createGoogleCirclePolygon(options) {
	return new GMAPS.Circle(options);
}

function createGooglePolygon(options) {
	return new GMAPS.Polygon(options);
}

function createGoogleMarker(options) {
	return new GMAPS.Marker(options);
}

function createInfoWindow(content, maxWidth) {
	return new GMAPS.InfoWindow({ 
		content: content,
		maxWidth: maxWidth
		});
}

function addMapProjectionChangedListener(map, actionFunction) {
	return GEVENT.addListener(map, 'projection_changed', actionFunction);
}

function addMouseOverListener(marker, actionFunction) {
	return GEVENT.addListener(marker, 'mouseover', actionFunction);
}

function addMouseOutListener(marker, actionFunction) {
	return GEVENT.addListener(marker, 'mouseout', actionFunction);
}

function addClickListener(marker, actionFunction) {
	return GEVENT.addListener(marker, 'click', actionFunction);
}

function removeListener(handler) {
	GEVENT.removeListener(handler);
}
