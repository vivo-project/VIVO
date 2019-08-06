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
	createToolTip($("#imageIconOne"), $('#toolTipOne').html(), "top left");
	createToolTip($("#exploreInfoIcon"), $('#exploreTooltipText').html(), "top left");
	createToolTip($("#compareInfoIcon"), $('#compareTooltipText').html(), "top left");
	createToolTip($("#imageIconThree"), $('#toolTipThree').html(), "top right");
}

var visCommonToolTipInit = true;
function createToolTip(target, tipText, tipLocation) {
	if (visCommonToolTipInit) {
		$('head').append('<style id="visCommonToolTipCSS">.qtip { font-size: .7em; max-width: none !important; } .visCommonToolTip {'
			+ ' background-color: #ffffc0;'
			+ ' textAlign: left;'
			+ ' padding: 6px 10px 6px 10px;'
			+ ' lineHeight: 14px;'
			+ '} </style>');

		visCommonToolTipInit = false;
	}

	target.qtip({
        content: {
            text: tipText
        },
		position: {
			my: tipLocation,
			at: 'center'
		},
        style: {
			classes: 'visCommonToolTip',
            width: 500,
        }
    });
}
