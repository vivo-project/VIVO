/* $This file is distributed under the terms of the license in /doc/license.txt$ */
var ColorStrategy = Class.extend ({
	getColor: function(key) {
	}
});

var SingleColorStrategy = ColorStrategy.extend ({
	init: function(defaultColor) {
		this.defaultColor = '#000000';
		if (defaultColor) {
			this.defaultColor = defaultColor;
		}
	},
	getColor: function(key) {
		return this.defaultColor;
	}
});

var DisciplineColorStrategy = SingleColorStrategy.extend ({
	init: function(defaultColor) {
		this._super(defaultColor);
	},
	getColor: function(key) {
		var color = DISCIPLINES[key].color;
		if (color) {
			return color;
		} else {
			return this._super(key);
		}
	}
});

var SubdisciplineColorStrategy = ColorStrategy.extend ({
	init: function(defaultColor) {
		this.colorStrategy = new DisciplineColorStrategy(defaultColor);
	},
	getColor: function(key) {
		var mapKey = SUBDISCIPLINES[key].discipline;
		return this.colorStrategy.getColor(mapKey);
	}
});

// Todo: Stop coding until comparison view, might need to use Temporal Line Graph solution
var AutoAssignColorStrategy = SingleColorStrategy.extend ({
	init: function(defaultColor, ColorList) {
		this._super(defaultColor);
	},
	getColor: function(key) {
	}
});

