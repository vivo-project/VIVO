/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var processInternalClassDataGetterContent = {
	dataGetterClass:null,
	//can use this if expect to initialize from elsewhere
	initProcessor:function(dataGetterClassInput) {
		this.dataGetterClass = dataGetterClassInput;
	},
	//Do we need a separate content type for each of the others?
	processPageContentSection:function(pageContentSection) {
		//get class group
		var classGroup = pageContentSection.find("select[name='selectClassGroup']").val();
		//Get classes selected
		var classesSelected = [];
		pageContentSection.find("input[name='classInClassGroup']:checked").each(function(){
			//Need to make sure that the class is also saved as a URI
			classesSelected.push($(this).val());
		});
		//If internal class selected, include here
		var isInternal="false";
		//if this checkbox is checked, then isInternal should be true
		pageContentSection.find("input[name='display-internalClass']:checked").each(function() {
			isInternal="true";
		});
		//JSON Object to be returned
		var returnObject = { classGroup: classGroup,
				classesSelectedInClassGroup:classesSelected, 
				isInternal:isInternal,
				dataGetterClass:this.dataGetterClass};
		return returnObject;
	},
	//For an existing set of content where form is already set, fill in the values 
	populatePageContentSection:function(existingContentObject, pageContentSection) {
		//var classGroupValue = existingContentObject["classGroup"];
		//var classesSelected = existingContentObject["classesSelectedInClassGroup"];
		var isInternal = existingContentObject["isInternal"];
		//Select the class group, display classes in class group, and select classes that are included
		processIndividualsForClassesDataGetterContent.populatePageContentSection(existingContentObject, pageContentSection);
		/*
		//Set class group
		pageContentSection.find("select[name='selectClassGroup']").val(classGroupValue);
		//Set classes selected within class group
		//TODO: Add magic for "all" if all classes selected
		var numberSelected = classesSelected.length;
		var i;
		for(i = 0; i < numberSelected; i++) {
			var classSelected = classesSelected[i];
			pageContentSection.find("input[name='classInClassGroup'][value='" + classSelected + "']").attr("checked", "checked");
		}*/
		//Also internal class needs to be selected
		if(isInternal == "true") {
			pageContentSection.find("input[name='display-internalClass']").attr("checked", "checked");
		} 
		//Since this is populating content from the template, no need to "uncheck" anything
		var results = pageContentSection.results;
		if(results != null && results.classGroupName != null) {
	    	var displayInternalMessage = pageContentSection.find('label[for="display-internalClass"] em');
	    	displayInternalMessage.filter(":first").html(results.classGroupName);
		}
		
	},
	//For the label of the content section for editing, need to add additional value
	retrieveAdditionalLabelText:function(existingContentObject) {
		return processClassGroupDataGetterContent.retrieveAdditionalLabelText(existingContentObject);

	},
	//Validation on form submit: Check to see that class group has been selected 
	validateFormSubmission: function(pageContentSection) {
		return processClassGroupDataGetterContent.validateFormSubmission(pageContentSection);
	}
		
}