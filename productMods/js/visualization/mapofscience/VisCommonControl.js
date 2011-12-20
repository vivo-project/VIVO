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

function createVisModeController(visMode) {
	if (visMode === ENTITY_VIS_MODE) {
		var controller = new EntityVisModeController(map);
		visModeControllers[controller.visMode] = controller;
	}
	
	if (visMode === COMPARISON_VIS_MODE) {
		var controller = new ComparisonVisModeController(map);
		visModeControllers[controller.visMode] = controller;
		controller.loadData(scienceMapDataURL, false);
	}
}

function isActiveVisMode(visMode) {
	return (currentVisMode === visMode);
}

function getVisModeController(visMode){
	if (visModeControllers[visMode] == null) {
		createVisModeController(visMode);
	}
	return visModeControllers[visMode];
}

function switchVisMode(visMode) {
	if (!isActiveVisMode(visMode)) {
		if (currentController) {
			currentController.cleanView();
		}
		currentController = getVisModeController(visMode);
		currentVisMode = visMode;
		currentController.initView();
	}
}

function initFilter(dom) {
	
	// Switch filter handling
	$("." + dom.filterOptionClass).live('click', function() { 
		var obj = $(this);
		if (!obj.hasClass(dom.activeFilterClass)) {
			var checked = obj.attr('id');
			if (checked === dom.secondFilterID) {
				$("#" + dom.firstFilterID).removeClass(dom.activeFilterClass);
				currentController.changeFilter(2);
				
			} else if (checked === dom.firstFilterID) {
				$("#" + dom.secondFilterID).removeClass(dom.activeFilterClass);
				currentController.changeFilter(1);
			}
				
			obj.addClass(dom.activeFilterClass);	
		}
	});
	
	$("#" + dom.firstFilterID).trigger('click');
}

function initVisModeTypeButton() {
	// Switch vis mode handling
	var viewTypeRadio = "input[name='view-type']";
	$(viewTypeRadio).change( function() {
		var visMode = $(viewTypeRadio+ ":checked").val();
		switchVisMode(visMode);
	});
	
	/* Init default filter */
	$(viewTypeRadio+ ":eq(0)").click();
}

function initGlobalToolTips() {

	createToolTip($("#imageIconOne"), $('#toolTipOne').html(), "topLeft");
	createToolTip($("#exploreInfoIcon"), $('#exploreTooltipText').html(), "topLeft");
	createToolTip($("#compareInfoIcon"), $('#compareTooltipText').html(), "topLeft");
	createToolTip($("#imageIconThree"), $('#toolTipThree').html(), "topRight");
}

function createToolTip(target, tipText, tipLocation) {
	target.qtip({
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
}