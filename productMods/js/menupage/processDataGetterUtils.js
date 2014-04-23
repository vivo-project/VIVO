/* $This file is distributed under the terms of the license in /doc/license.txt$ */

//This class is responsible for the product-specific form processing/content selection that might be possible
//Overrides the usual behavior of selecting the specific JavaScript class needed to convert the form inputs
//into a JSON object for submission based on the page content type
//VIVO Specific version includes individuals for classes data getter in addition to the others
//The internal class specific processor class is in VIVO, while all other javascript files are in Vitro

var processDataGetterUtils = {
		dataGetterProcessorMap:{"browseClassGroup": processClassGroupDataGetterContent, 
								"sparqlQuery": processSparqlDataGetterContent, 
								"fixedHtml":processFixedHTMLDataGetterContent,
								"internalClass":processInternalClassDataGetterContent,
								"searchIndividuals":processSearchDataGetterContent},
	    selectDataGetterType:function(pageContentSection) {
			var contentType = pageContentSection.attr("contentType");
			//The form can provide "browse class group" as content type but need to check
			//whether this is in fact individuals for classes instead
			if(contentType == "browseClassGroup") {
				//Is ALL NOT selected and there are other classes, pick one
				//this SHOULD be an array
				var allClassesSelected = pageContentSection.find("input[name='allSelected']:checked");
				var isInternalSelected = pageContentSection.find("input[name='display-internalClass']:checked");
				//If all NOT selected then need to pick a different content type OR if internal class selected
				if( isInternalSelected.length > 0 || allClassesSelected.length == 0) {
					contentType = "internalClass";
				}
			} 
			
			return contentType;
	    },
	    isRelatedToBrowseClassGroup:function(contentType) {
	    	return (contentType == "browseClassGroup" || contentType == "internalClass");
	    },
	    getContentTypeForCloning:function(contentType) {
	    	if(contentType == "browseClassGroup" || contentType == "internalClass") {
	    		return "browseClassGroup";
	    	} 
	    	return contentType;
	    }
};