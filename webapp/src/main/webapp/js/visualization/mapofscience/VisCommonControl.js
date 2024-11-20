/* $This file is distributed under the terms of the license in LICENSE$ */
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
	$( document ).on( 'click', "." + dom.filterOptionClass, function() {
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

	let tooltips = [
		{
			querySelector: "#imageIconOne",
			data: {
				title: "<div class='visCommonControllTooltip'>" + $('#toolTipOne').html() + "</div>",
				customClass: "vitroTooltip vitroTooltip-yellow",
				html: true,
				sanitize: false,
				fallbackPlacements: ['right', 'bottom', 'top', 'left']
			}
		},{
			querySelector: "#exploreInfoIcon",
			data: {
				title: "<div class='visCommonControllTooltip'>" + $('#exploreTooltipText').html() + "</div>",
				customClass: "vitroTooltip vitroTooltip-yellow",
				html: true,
				sanitize: false,
				fallbackPlacements: ['right', 'bottom', 'top', 'left']
			}
		},{
			querySelector: "#compareInfoIcon",
			data: {
				title: "<div class='visCommonControllTooltip'>" + $('#compareTooltipText').html() + "</div>",
				customClass: "vitroTooltip vitroTooltip-yellow",
				html: true,
				sanitize: false,
				fallbackPlacements: ['right', 'bottom', 'top', 'left']
			}
		},{
			querySelector: "#imageIconThree",
			data: {
				title: "<div class='visCommonControllTooltip'>" + $('#toolTipThree').html() + "</div>",
				customClass: "vitroTooltip vitroTooltip-yellow",
				html: true,
				sanitize: false,
				fallbackPlacements: ['left', 'bottom', 'top', 'right']
			}
		},
	]

	tooltips.forEach(tooltip => {
		setTooltip(tooltip.querySelector, tooltip.data)
	})
}
