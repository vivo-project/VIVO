/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$.extend(this, i18nStrings);

var ComparisonDataTableWidget = Class.extend({
	
	dom: {
		firstFilterLabel: i18nStrings.organizationsString,
		secondFilterLabel: i18nStrings.peopleString,
		
		searchBarParentContainerClass : "comparisonSearchbar",
		paginationContainerClass : "paginatedtabs",
		containerID: "main-science-areas-table-container",
		footerID: "main-science-areas-table-footer",
		firstFilterID: "comparison-first-filter",
		secondFilterID: "comparison-second-filter",
		filterOptionClass: "comparison-filter-option",
		activeFilterClass: "comparison-active-filter",
		filterInfoIconClass: "comparisonFilterInfoIcon"
	},
	init: function(sciMapWidget, entityTablesWidget) {
		var me = this;
		me.sciMapWidget = sciMapWidget;
		me.entityTablesWidget = entityTablesWidget;
		me.widgetType = "COMPARISON_SCIENCE_AREAS";
		me.currentSelectedFilter = COMPARISON_TYPE.ORGANIZATION;
		me.widget = '';
		me.tableDiv = $('<div />');
		$("#" + me.dom.containerID).append(this.tableDiv);
	},
	loadJsonData: function(data) {
		
		var me = this;
		me.uri = data.uri;
		me.label = data.label;
		me.pubsWithNoJournals = data.pubsWithNoJournals;
		me.pubsWithInvalidJournals = data.pubsWithInvalidJournals;
		me.pubsMapped = data.pubsMapped;
		me.type = data.type;
		me.subEntities = data.subEntities;
		
		me.setupView();
	},
	hasKey: function(key) {
		return (this.keyToMarkerManagers.hasOwnProperty(key));
	},
	show: function(key) {
		this.tableDiv.show();
	},
	hide: function(key) {
		this.tableDiv.hide();
	},
	cleanView: function() {
		this.hide();
	},
	initView: function() {
		this.show();
		this.changeFilter(this.currentSelectedFilter);
	},
	setupView: function() {
		
		var me = this;
		
		/* Create filter */
		var dom = me.dom;
		var filter = $('<div class="science-areas-filter">' +
	    	'<span id="' + dom.firstFilterID + '" class="' + dom.filterOptionClass + ' ' + dom.activeFilterClass + '">' + dom.firstFilterLabel + '</span>'+
	    	/* This is temporary removed due to the person's publications mapping rate is too low to be displayed.
		    	' | ' +
		    	'<span id="' + dom.secondFilterID + '" class="' + dom.filterOptionClass + '">' + dom.secondFilterLabel + '</span>' +
	    	*/
	    	'<img class="' + dom.filterInfoIconClass + '" id="comparisonImageIconTwo" src="'+ infoIconUrl +'" alt="' + i18nStrings.infoIconString + '" title="" /></div>');
		me.tableDiv.append(filter);
		createToolTip($("#comparisonImageIconTwo"), $("#comparisonToolTipTwo").html(), "topLeft");
		initFilter(dom);
		
		/* Create table */
		var table = $('<table>');
		table.attr('id', 'comparisonDatatable');
		table.addClass('datatable-table');
		
		var thead = $('<thead>');
		var tr = $('<tr>');
		
		var levelOfScienceAreaTH = $('<th>');
		levelOfScienceAreaTH.html(i18nStrings.entityTypeString);
		
		var checkBoxTH = $('<th>');
		checkBoxTH.html('');
		
		var scienceAreasTH = $('<th>');
		scienceAreasTH.attr("id", "comparison-science-areas-th");
		if (this.currentSelectedFilter === COMPARISON_TYPE.ORGANIZATION ) {
			scienceAreasTH.html(i18nStrings.organizationString);
		} else {
			scienceAreasTH.html(i18nStrings.personString);
		}
		
		var activityCountTH = $('<th width="53">');
		activityCountTH.html('# ' + i18nStrings.numberOfPubs);
		activityCountTH.attr("id", "activity-count-column");

		tr.append(levelOfScienceAreaTH);
		tr.append(checkBoxTH);
		tr.append(scienceAreasTH);
		tr.append(activityCountTH);
		
		thead.append(tr);
		
		table.append(thead);
		
		var tbody = $('<tbody id="comparisonTbody">');
		
		var rowsToInsert = [];
		var i = 0;
		
		$.each(me.subEntities, function(index, item) {
			rowsToInsert[i++] = '<tr id="' + index + '" style="color: grey;"><td>' + item.type + '</td>';
			rowsToInsert[i++] = '<td><input class="chk" type="checkbox" value="' + index + '"/></td>';
			rowsToInsert[i++] = '<td>' + item.label + '</td>';
			rowsToInsert[i++] = '<td>' + item.pubs + '</td></tr>';
		});
		
		tbody.append(rowsToInsert.join(''));

		table.append(tbody);
		me.tableDiv.append(table);
		
		table.children("tbody").children("tr").mouseenter(function() {
			
			var item = me.subEntities[$(this).attr("id")];
			me.sciMapWidget.mouseIn(item.type, item.label);
		});
		
		table.children("tbody").children("tr").mouseleave(function() {

			var item = me.subEntities[$(this).attr("id")];
			me.sciMapWidget.mouseOut(item.type, item.label);
		});
		
		$('.chk').click(function() {
			var element = $(this);
			var index = element.attr("value");
			var item = me.subEntities[index];
			var color = "grey";
			if (element.attr('checked')) {
				if ($("input:checkbox[class=chk]:checked").length > 3) {
					element.attr('checked', false);
					alert(i18nStrings.maxNbrForComp);
				} else {
					me.loadEntity(item.uri, index);
				}
			} else {
				me.unloadEntity(item.type, item.label, index);
			}
		});
		
		/*
		 * GMAIL_STYLE_PAGINATION_CONTAINER_CLASS, ACTIVE_DISCIPLINE_SUBDISCIPLINE_FILTER has to be declared 
		 * for the filter & pagination to work properly.
		 * */
		GMAIL_STYLE_PAGINATION_CONTAINER_CLASS = me.dom.paginationContainerClass;
		ACTIVE_DISCIPLINE_SUBDISCIPLINE_FILTER = me.currentSelectedFilter;
		
		if($.inArray(disciplineOrSubdisciplineDataTableFilter, $.fn.dataTableExt.afnFiltering) < 0) {
			$.fn.dataTableExt.afnFiltering.push(disciplineOrSubdisciplineDataTableFilter);
		}
		
		me.widget = table.dataTable({
		    "sDom": '<"' + me.dom.searchBarParentContainerClass 
		    			+ '"f><"filterInfo"i><"' 
		    			+ me.dom.paginationContainerClass + '"p><"table-separator"><"datatablewrapper"t>',
		    "aaSorting": [
		        [3, "desc"], [2,'asc']
		    ],
		    "asStripClasses": [],
		    "aoColumns": [{ "bVisible": false, "bSearchable": false },
		                  null,
		                  null,
		                  null], 
		    "iDisplayLength": 10,
		    "bInfo": true,
		    "oLanguage": {
				"sInfo": "_START_ - _END_ of _TOTAL_",
				"sInfoEmpty": i18nStrings.noMatchingScienceAreas,
				"sInfoFiltered": ""
			},
		    "sPaginationType": "gmail_style",
		    "fnDrawCallback": function () {
		    	
		        /* We check whether max number of allowed comparisions (currently 10) is reached
		         * here as well becasue the only function that is guaranteed to be called during 
		         * page navigation is this. No need to bind it to the nav-buttons becuase 1. It is over-ridden
		         * by built-in navigation events & this is much cleaner.
		         * */
//		        checkIfColorLimitIsReached();
		    }
		});
		
		/* Create search box */
		var searchInputBox = $("." + me.dom.searchBarParentContainerClass).find("input[type=text]");
		searchInputBox.css("width", "140px");
		searchInputBox.after("<span id='comparison-reset-search' title='" + i18nStrings.clearSearchQuery + "'>X</span>" 
								+ "<img class='comparisonFilterInfoIcon' id='comparisonSearchInfoIcon' src='" + infoIconUrl + "' alt='" + i18nStrings.infoIconString + "' title='' />");
		$("#comparison-reset-search").live('click', function() {
			me.widget.fnFilter("");
		});
		createToolTip($("#comparisonSearchInfoIcon"), $("#comparisonSearchInfoTooltipText").html(), "topLeft");
		
		/* Create csv download button */
		var csvButton = '<hr class="subtle-hr"/><div id="main-science-areas-table-footer"><a href="' +
						comparisonScienceMapCsvDataUrlPrefix + me.uri +
						'" class="map-of-science-links">' + i18nStrings.saveAllAsCSV + '</a></div>';
		me.tableDiv.append(csvButton);
	},
	changeFilter: function(filterType) {
		var me = this;
		if (filterType === COMPARISON_TYPE.ORGANIZATION) {
			
			$("#comparison-science-areas-th").html(i18nStrings.organizationString);
			me.currentSelectedFilter = COMPARISON_TYPE.ORGANIZATION;
		} else {
			
			$("#comparison-science-areas-th").html(i18nStrings.personString);
			me.currentSelectedFilter = COMPARISON_TYPE.PERSON;
			
		}
		
		ACTIVE_DISCIPLINE_SUBDISCIPLINE_FILTER = me.currentSelectedFilter;

		if (me.widget) {
			me.widget.fnDraw();
			// load one item if no item is selected. Need to be improved
			if ($("input:checkbox[class=chk]:checked").length == 0) {
				$("input:checkbox[class=chk]").each(function(){
					var item = me.subEntities[$(this).attr("value")];
					if (item.type == me.currentSelectedFilter) {
						// click event didn't work at this point???
						$(this).click();
						me.loadEntity(item.uri, $(this).attr("value"));
						return false;
					}
				});
			}
		}
	},
	loadEntity: function(uri, index) {
		
		// Download data from server and add to markerManager if not gotten already
		var me = this;
		var url = scienceMapDataPrefix + uri;
		downloader.downloadAndWait(url, function(data) {
			me.sciMapWidget.loadEntity(data[0]);
			
			// This is ugly, need fix!!!
			var color = me.sciMapWidget.getColor(me.currentSelectedFilter, me.subEntities[index].label);
			$("#comparisonTbody > tr[id=" + index + "]").css("color", color);
			me.entityTablesWidget.loadEntity(data[0], color);
		});
	},
	unloadEntity: function(key, childKey, index) {
		this.sciMapWidget.unloadEntity(key, childKey);
		this.entityTablesWidget.unloadEntity(childKey);
		
		// This is ugly, need fix!!!
		$("#comparisonTbody > tr[id=" + index + "]").css("color", "grey");
	}
});