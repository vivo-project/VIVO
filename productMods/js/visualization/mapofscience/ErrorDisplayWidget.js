/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var ErrorDisplayWidget = Class.extend({
	
	container: '',
	body: '',
	bodyID: 'error-body',
	messagePlaceholderID: 'variable-error-text',
	
	init: function(opts) {
	
		this.container = $("#" + opts.containerID);
		this.body = this.container.find("#" + this.bodyID);
	}, 
	
	isErrorConditionTriggered: function(responseData) {
		
		if (responseData.error) {
			return true;
		}
		
		if (responseData[0].pubsMapped === 0) {
			return true;
		}
		
		return false;
	},
	
	show: function(errorForType, responseData) {
		
		var isZeroPublicationsCase = responseData.error ? true : false;
		var newErrorMessage = "";
		
		/*
		 * This means that the organization or person has zero publications. 
		 * */
		if (isZeroPublicationsCase) {
			
			newErrorMessage += "No publications in the system have been attributed to this " + errorForType.toLowerCase() + ".";
			
		} else {
		/*
		 * This means that the organization or person has publications but none of them are mapped.
		 * Change the default text.
		 * */
			newErrorMessage += this._getUnScienceLocatedErrorMessage(errorForType, responseData[0]);
		} 

		/*
		 * Now replace the error message with the newly created one.
		 * */
		this.body.find("#" + this.messagePlaceholderID).html(newErrorMessage);	
		
		this.container.show();
	},
	
	_getUnScienceLocatedErrorMessage: function(errorForType, responseData) {
		
		var totalPublications = responseData.pubsWithNoJournals + responseData.pubsWithInvalidJournals;
		var newErrorMessage = "";

		if (totalPublications > 1) {
			newErrorMessage = "None of the " + totalPublications + " publications attributed to this " 
			+ errorForType.toLowerCase() + " have been 'science-located'.";	
			
		} else {
			newErrorMessage = "The publication attributed to this " 
				+ errorForType.toLowerCase() + " has not been 'science-located'.";
		}
		
		
		newErrorMessage += "<ul class='error-list'>";
		
		if (responseData.pubsWithNoJournals && responseData.pubsWithNoJournals > 0) {
			
			var publicationsText = (responseData.pubsWithNoJournals > 1) ? "publications" : "publication";
			
			newErrorMessage += "<li>" + responseData.pubsWithNoJournals + " " + publicationsText + " have no journal" 
				+ " information.</li>"
				
		}
		
		if (responseData.pubsWithInvalidJournals && responseData.pubsWithInvalidJournals > 0) {
			
			var publicationsText = (responseData.pubsWithInvalidJournals > 1) ? "publications" : "publication";
			
			newErrorMessage += "<li>" + responseData.pubsWithInvalidJournals + " " + publicationsText + " " 
			+ " could not be matched with a map location using their journal information.</li>" 				
		}
		
		newErrorMessage += "</ul>";
		
		return newErrorMessage;
	},
	
	hide: function() {
		this.container.hide();
	}
	
});