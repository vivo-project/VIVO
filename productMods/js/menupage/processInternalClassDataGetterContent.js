/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var processInternalClassDataGetterContent = {
	dataGetterClass:null,
	//can use this if expect to initialize from elsewhere
	initProcessor:function(dataGetterClassInput) {
		this.dataGetterClass = dataGetterClassInput;
	},
	//Do we need a separate content type for each of the others?
	processPageContentSection:function(pageContentSection) {
		//Get classes selected
		var classesSelected = [];
		pageContentSection.find("input[name='classInClassGroup']:checked").each(function(){
			//Need to make sure that the class is also saved as a URI
			classesSelected.push($(this).val());
		});
		//If internal class selected, include here
		var isInternal=false;
		//if this checkbox is checked, then isInternal should be true
		pageContentSection.find("input[name='display-internalClass']:checked").each(function() {
			isInternal=true;
		});
		//Not returning class group although could if need be.. 
		var returnObject = { classesSelectedInClassGroup:classesSelected, 
				isInternal:isInternal,
				dataGetterClass:this.dataGetterClass};
		return returnObject;
	}	
		
}