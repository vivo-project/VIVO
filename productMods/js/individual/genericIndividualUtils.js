/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    
	var fixCasesForContent = {
		
		toTitleCase: function(text) {
		
	    return text.replace(/\w\S*/g, function(matchedSubstring, position, completeString){
	        
	        if ((completeString.charAt(position-1) === "&") 
	                || matchedSubstring.charAt(1).match(/\W/)) {
	            return matchedSubstring;
	        } else {
	            return matchedSubstring.charAt(0).toUpperCase() + matchedSubstring.substr(1).toLowerCase();                             
	        }                                                               
	        });
		},	
			
		fixCaseFor: function(selector) {
			
			$.each(selector, function(index, value) {
				
				$(value).text(fixCasesForContent.toTitleCase($(value).text()));
				
			});
			
		},
		
		fixCases: function() {
			fixCasesForContent.fixCaseFor($("ul#individual-personInPosition > li > a:first-child"));
			fixCasesForContent.fixCaseFor($("ul.property-list > li > a:first-child"));
			fixCasesForContent.fixCaseFor($("ul.subclass-property-list > li > a:first-child"));
		}
	}
	
	fixCasesForContent.fixCases();
    
});