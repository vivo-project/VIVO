/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var DataTableWidget = Class.extend({
	
	widgetType: "MAIN_SCIENCE_AREAS",
	
	currentSelectedFilter: "DISCIPLINE",
	
	dom: {
		searchBarParentContainerClass : "searchbar",
		paginationContainerClass : "paginatedtabs",
		containerID: "main-science-areas-table-container",
		footerID: "main-science-areas-table-footer",
		disciplineFilterID: "discipline-filter",
		subdisciplinesFilterID: "subdisciplines-filter",
		filterOptionClass: "filter-option",
		activeFilterClass: "active-filter"
	},
	
	widget: '',
	
	init: function(opts) {
		this.opts = opts;
		
		this.subdisciplineInfo = {};
		this.disciplineInfo = {};
		
		var me = this;
		
		$.each(DISCIPLINES, function(index, item) {
			var emptyScienceAreaElement = {
				publicationCount: 0,
				label: item.label		
			};
			me.disciplineInfo[index] = emptyScienceAreaElement;
		});
		
		$.each(SUBDISCIPLINES, function(index, item) {
			var emptyScienceAreaElement = {
				publicationCount: 0,
				label: item.label		
			};
			me.subdisciplineInfo[index] = emptyScienceAreaElement;
		});
		
	},
	loadJsonData: function(data) {
		
		var me = this;
		me.uri = data.uri;
		me.label = data.label;
		me.pubsWithNoJournals = data.pubsWithNoJournals;
		me.pubsWithInvalidJournals = data.pubsWithInvalidJournals;
		me.pubsMapped = data.pubsMapped;
		me.type = data.type;
		
		$.each(data.subdisciplineActivity, function(subdiscipline, density) {
			
			me.subdisciplineInfo[subdiscipline].publicationCount = density;
			
			var currentSubdisciplinesDiscipline = SUBDISCIPLINES[subdiscipline].discipline;
			
			if (me.disciplineInfo[currentSubdisciplinesDiscipline]) {
				
				me.disciplineInfo[currentSubdisciplinesDiscipline].publicationCount = 
					me.disciplineInfo[currentSubdisciplinesDiscipline].publicationCount + density; 
				
			} 
			
		}); 
	},
	hasKey: function(key) {
		return (this.keyToMarkerManagers.hasOwnProperty(key));
	},
	show: function(key) {
	},
	hide: function(key) {
	},
	cleanUp: function() {
	},
	
	initView: function() {
		
		var me = this;
		
		var table = $('<table>');
		table.attr('id', 'datatable');
		table.addClass('datatable-table');
		
		var thead = $('<thead>');
		var tr = $('<tr>');
		
		var levelOfScienceAreaTH = $('<th>');
		levelOfScienceAreaTH.html('Level of Science Area');
		
		var scienceAreasTH = $('<th>');
		scienceAreasTH.attr("id", "science-areas-th");
		if (this.currentSelectedFilter === 'SUBDISCIPLINE' ) {
			scienceAreasTH.html('Sub-Disciplines');
		} else {
			scienceAreasTH.html('Disciplines');
		}
		
		var activityCountTH = $('<th>');
		activityCountTH.html('# of pubs.');
		activityCountTH.attr("id", "activity-count-column");

		var percentageActivityTH = $('<th>');
		percentageActivityTH.html('% activity');

		tr.append(levelOfScienceAreaTH);
		tr.append(scienceAreasTH);
		tr.append(activityCountTH);
		tr.append(percentageActivityTH);
		
		thead.append(tr);
		
		table.append(thead);
		
		var tbody = $('<tbody>');
		
		var rowsToInsert = [];
		var i = 0;
		
		$.each(me.disciplineInfo, function(index, item) {
			rowsToInsert[i++]  = '<tr><td>DISCIPLINE</td>';
		    rowsToInsert[i++]  = '<td>' + item.label + '</td>';
		    rowsToInsert[i++]  = '<td>' + item.publicationCount.toFixed(1) + '</td>';
		    rowsToInsert[i++]  = '<td>' + (100 * (item.publicationCount / me.pubsMapped)).toFixed(1) + '</td></tr>';
		});
		
		
		$.each(me.subdisciplineInfo, function(index, item) {
			rowsToInsert[i++]  = '<tr><td>SUBDISCIPLINE</td>';
		    rowsToInsert[i++]  = '<td>' + item.label + '</td>';
		    rowsToInsert[i++]  = '<td>' + item.publicationCount.toFixed(1) + '</td>';
		    rowsToInsert[i++]  = '<td>' + (100 * (item.publicationCount / me.pubsMapped)).toFixed(1) + '</td></tr>';
		});
		
		tbody.append(rowsToInsert.join(''));

		table.append(tbody);
		$("#" + me.dom.containerID).append(table);
		
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
		        [2, "desc"], [1,'asc']
		    ],
		    "asStripClasses": [],
		    "aoColumns": [{ "bVisible": false, "bSearchable": false },
		                  null,
		                  null,
		                  null], 
		    "iDisplayLength": 13,
		    "bInfo": true,
		    "oLanguage": {
				"sInfo": "_START_ - _END_ of _TOTAL_",
				"sInfoEmpty": "No matching science areas found",
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
		
		
		var searchInputBox = $("." + me.dom.searchBarParentContainerClass).find("input[type=text]");
		
		searchInputBox.after("<span id='reset-search' title='Clear search query'>X</span>");
		
		$("#reset-search").live('click', function() {
			me.widget.fnFilter("");
		});
		
		$("." + me.dom.filterOptionClass).live('click', function() {
			
			if (!$(this).hasClass(me.dom.activeFilterClass)) {
				
				if ($(this).attr('id') === me.dom.subdisciplinesFilterID) {
					
					$("#" + me.dom.disciplineFilterID).removeClass(me.dom.activeFilterClass);
					$("#science-areas-th").html("Sub-Disciplines");
					
					me.widget.fnSettings()._iDisplayLength = 10;
					me.currentSelectedFilter = "SUBDISCIPLINE";
					
					$("a#csv").attr("href", entityMapOfScienceSubDisciplineCSVURL);
					
				} else if ($(this).attr('id') === me.dom.disciplineFilterID) {
					
					$("#" + me.dom.subdisciplinesFilterID).removeClass(me.dom.activeFilterClass);
					$("#science-areas-th").html("Disciplines");
					
					me.currentSelectedFilter = "DISCIPLINE";
					me.widget.fnSettings()._iDisplayLength = 13;
					
					$("a#csv").attr("href", entityMapOfScienceDisciplineCSVURL);
				} 
				
				$(this).addClass('active-filter');
				
				ACTIVE_DISCIPLINE_SUBDISCIPLINE_FILTER = me.currentSelectedFilter;
				me.widget.fnDraw();
			}
		});
		
	}
});