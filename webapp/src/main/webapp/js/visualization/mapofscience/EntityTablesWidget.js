/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var EntityTablesWidget = Class.extend({
	init: function(sciMapWidget) {
		this.sciMapWidget = sciMapWidget;
		this.keyToDataTable = {};
		this.container = $('<div>');
		$("#subEntityTableArea").append(this.container);
		$("#subEntityTableArea").show();
	},
	initView: function(key) {
		this.container.show();
	},
	cleanView: function(key) {
		this.container.hide();
	},
	loadJsonData: function(data) {
	},
	loadEntity: function(data, color) {
		var key = data.label;
		var dataTable = this.getDataTable(key);
		if (dataTable == null) {
			dataTable = this._createDataTable(key, data, color);
		}
		dataTable.initView();
	},
	unloadEntity: function(key) {
		this._removeDataTable(key);
	},
	getDataTable: function(key) {
		return this.keyToDataTable[key];
	},
	_removeDataTable: function(key) {
		var dataTable = this.getDataTable(key);
		if (dataTable) {
			dataTable.cleanView();
			delete this.keyToDataTable[key];
		}
	},
	_createDataTable: function(key, data, color) {
		dataTable = new SimpleDataTableWidget({
			sciMapWidget: this.sciMapWidget, 
			container: this.container
			});
		data.color = color;
		dataTable.loadJsonData(data);
		this.keyToDataTable[key] = dataTable;
		return dataTable;
	}
});