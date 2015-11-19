/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$.extend(this, i18nStrings);

var SimpleDataTableWidget = Class.extend({
	
	dom: {
		paginationContainerClass : "subpaginatedtabs"
	},
	init: function(options) {
		var me = this;
		me.options = options;
		me.sciMapWidget = options.sciMapWidget;
		me.currentSelectedFilter = COMPARISON_TYPE.SUBDISCIPLINE;
		me.widget = '';
		me.tableDiv = $('<div />');
		me.addToContainer();
	},
	loadJsonData: function(data) {
		
		var me = this;
		me.uri = data.uri;
		me.label = data.label;
		me.pubsWithNoJournals = data.pubsWithNoJournals;
		me.pubsWithInvalidJournals = data.pubsWithInvalidJournals;
		me.pubsMapped = data.pubsMapped;
		me.type = data.type;
		me.color = data.color;
		me.subdisciplineActivity = data.subdisciplineActivity;
		me.setupView();
	},
	addToContainer: function() {
		this.options.container.append(this.tableDiv);
	},
	removeFromContainer: function() {
		this.tableDiv.remove();
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
		this.removeFromContainer();
	},
	initView: function() {
		this.addToContainer(this.tableDiv);
		this.show();
	},
	setupView: function() {
		
		var me = this;
		
		me.tableDiv.addClass("subEntityTable");
		
		/* Create filter */
		entityVivoProfileURLPrefix
		var organizationHeader = $('<div><a class="suborganization-title" href="' + 
				entityVivoProfileURLPrefix + me.uri +'">' + 
				truncateText(me.label, 23) + '</a><a href="' + entityMapOfScienceURLPrefix + 
				me.uri + '"><img class="drillDownIcon" src="' + 
				drillDownIconUrl + '" alt="' + i18nStrings.drillDownString + '" title="' + i18nStrings.drillDownString + '" /></a></div>');
		me.tableDiv.append(organizationHeader);
		
		/* Create table */
		var table = $('<table>');
		table.attr('id', 'entityDatatable');
		table.addClass('entity-datatable-table');
		
		/* Create table header */
		var thead = $('<thead>');
		var tr = $('<tr>');
		
		/*var levelOfScienceAreaTH = $('<th>');
		levelOfScienceAreaTH.html('Level of Science Area');*/
		
		var scienceAreasTH = $('<th>');
		scienceAreasTH.attr("id", "entity-science-areas-th");
		scienceAreasTH.html(i18nStrings.subdisciplinesString);
		
		var activityCountTH = $('<th width="53">');
		activityCountTH.html('# ' + i18nStrings.numberOfPubs);

		//tr.append(levelOfScienceAreaTH);
		tr.append(scienceAreasTH);
		tr.append(activityCountTH);
		
		thead.append(tr);
		table.append(thead);
		
		/* Create tbody and contents */
		var tbody = $('<tbody>');
		
		var rowsToInsert = [];
		var i = 0;
		
		$.each(me.subdisciplineActivity, function(index, density) {
			rowsToInsert[i++] = '<tr id="' + index + '">';
			rowsToInsert[i++] = '<td style="color:' + me.color + ';">' + truncateText(SUBDISCIPLINES[index].label, 20) + '</td>';
			rowsToInsert[i++] = '<td style="color:' + me.color + ';">' + density.toFixed(1) + '</td></tr>';
		});
		
		tbody.append(rowsToInsert.join(''));
		table.append(tbody);
		me.tableDiv.append(table);
		
		/* Register events */
		table.children("tbody").children("tr").mouseenter(function() {
			me.sciMapWidget.mouseInNode(me.type, me.label, $(this).attr("id"));
		});
		
		table.children("tbody").children("tr").mouseleave(function() {
			me.sciMapWidget.mouseOutNode(me.type, me.label, $(this).attr("id"));
		});
		
		/* Init DataTable object */
		me.widget = table.dataTable({
		    "sDom": '<"' + me.dom.paginationContainerClass + '"p><"table-separator"><"datatablewrapper"t>',
		    "aaSorting": [
		        [1, "desc"], [0,'asc']
		    ],
		    "asStripClasses": [],
		    "aoColumns": [{ "bSearchable": false },
		                  { "bSearchable": false }], 
		    "iDisplayLength": 10,
		    "bInfo": true,
		    "bFilter": false,
		    "oLanguage": {
				"sInfo": "_START_ - _END_ of _TOTAL_",
				"sInfoEmpty": i18nStrings.noMatchingScienceAreas,
				"sInfoFiltered": ""
			},
		    "sPaginationType": "gmail_style",
		    "fnDrawCallback": function () {
		    }
		});
		
		/* Create csv download button */
		var csvButton = '<hr class="subtle-hr" /><div id="main-science-areas-table-footer" style="background-color:' + me.color + ';"><a href="' +
						entityMapOfScienceSubDisciplineCSVURLPrefix + me.uri +
						'" class="map-of-science-links">' + i18nStrings.saveAllAsCSV + '</a></div>';
		me.tableDiv.append(csvButton);
		
		/* Create mapping statistic result */
		var totalPublications = me.pubsWithNoJournals + me.pubsWithInvalidJournals + me.pubsMapped;
		var mappedText = '<a class="mapped-result" href="' + entityUnmappedJournalsCSVURLPrefix + me.uri + '">' + 
						(100 * me.pubsMapped / totalPublications).toFixed(2) + '% mapped</a>';
		me.tableDiv.append($(mappedText));
		me.widget.fnDraw();
	}
});

function truncateText(text, len) {

	var trunc = text;
	
	if (text.length > len) {
		trunc = text.substring(0, len);
		trunc = trunc.replace(/\w+$/, '') + '<font title="' + text + '">...</font>'
	}

	return trunc;
}