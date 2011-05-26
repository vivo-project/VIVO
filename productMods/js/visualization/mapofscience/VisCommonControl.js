/* $This file is distributed under the terms of the license in /doc/license.txt$ */
function switchMarkerManager(id) {

	markerManager = getMarkerManager(id);
	if(isActiveMarkerManager(markerManager)) {
		markerManager.addAllToMap();
		
		if(activeMarkerManager) {
			activeMarkerManager.removeAllFromMap();
		}
		
		/* switch to target marker manager */
		activeMarkerManager = markerManager;
	}
}

function isActiveVisMode(visMode) {
	return (currentVisMode == visMode);
}

function getVisModeController(visMode){
	return visModeControllers[visMode];
}

function switchVisMode(visMode) {
	if (currentVisMode != visMode) {
		currentVisMode = visMode;
		if (currentController) {
			currentController.cleanView();
		}
		currentController = getVisModeController(visMode);
		currentController.initView();
	}
	return 
}
