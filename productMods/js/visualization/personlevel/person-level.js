/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$.extend(this, i18nStringsPersonLvl);

function getWellFormedURLs(given_uri, type) {
	
	if (!given_uri || given_uri == "") {
		return;
	}

	// general best practice is to put javascript code inside document.ready
	// but in this case when i do that the function does not get called
	// properly.
	// so removing it for now.

	// $(document).ready(function() {

	var finalURL;

	if (type == "coauthorship") {

		finalURL = $.ajax({
			url: contextPath + "/visualizationAjax",
			data: ({vis: "utilities", vis_mode: "COAUTHORSHIP_URL", uri: given_uri}),
			dataType: "text",
			async: false,
			success:function(data){
		}
		}).responseText;

		return finalURL;

	} else 	if (type == "coinvestigation") {

		finalURL = $.ajax({
			url: contextPath + "/visualizationAjax",
			data: ({vis: "utilities", vis_mode: "COPI_URL", uri: given_uri}),
			dataType: "text",
			async: false,
			success:function(data){
		}
		}).responseText;

		return finalURL;

	} else if (type == "profile") {

		finalURL = $.ajax({
			url: contextPath + "/visualizationAjax",
			data: ({vis: "utilities", vis_mode: "PROFILE_URL", uri: given_uri}),
			dataType: "text",
			async: false,
			success:function(data){
		}
		}).responseText;

		return finalURL;

	} else if (type == "image") {

		finalURL = $.ajax({
			url: contextPath + "/visualizationAjax",
			data: ({vis: "utilities", vis_mode: "IMAGE_URL", uri: given_uri}),
			dataType: "text",
			async: false,
			success:function(data){
		}
		}).responseText;

		return finalURL;

	} else if (type == "profile_info") {

		var profileInfoJSON = $.ajax({
			url: contextPath + "/visualizationAjax",
			data: ({vis: "utilities", vis_mode: "PROFILE_INFO", uri: given_uri}),
			dataType: "json",
			async: false,
			success:function(data){
		}
		}).responseText;

		return profileInfoJSON;
	}
}

function setProfileImage(imageContainerID, mainImageURL) {
	
	if (imageContainerID == "") {
		return;
	}
	
	if (!mainImageURL || mainImageURL == "") {
		$("#" + imageContainerID).empty();
		return;
	}
	
	var rawPath = getWellFormedURLs(mainImageURL, "image");

	var imageLink =  contextPath + rawPath;

	var imageContainer = $("#" + imageContainerID);
	imageContainer.image(imageLink, 
			function(){
		imageContainer.empty().append(this); 
	},
	function(){
		// For performing any action on failure to
		// find the image.
		imageContainer.empty();
	}
	);
	
}

function setProfileMoniker(monikerContainerID, moniker, doEllipsis) {

	if (monikerContainerID == "") {
		return;
	}
	
	if (!moniker) {
		$("#" + monikerContainerID).empty();
		return;
	}

	var finalDisplayMoniker;
	
	if (moniker.length > 40 && doEllipsis) {
		
		finalDisplayMoniker = moniker.substr(0,40) + "...";
		
	} else {
		
		finalDisplayMoniker = moniker;
		
	}
	
	$("#" + monikerContainerID).empty().text(finalDisplayMoniker);
	$("#" + monikerContainerID).attr('title', moniker);

}

function setProfileName(nameContainerID, name, doNameEllipsis) {
	
	if (nameContainerID == "") {
		return;
	}
	
	if (!name) {
		$("#" + nameContainerID).empty();
		return;
	}

	var finalDisplayName;
	
	if (name.length > 40 && doNameEllipsis) {
		
		finalDisplayName = name.substr(0,40) + "...";
		
	} else {
		
		finalDisplayName = name;
		
	}
	
	$("#" + nameContainerID).empty().text(finalDisplayName);
	$("#" + nameContainerID).attr('title', name);

}

function processProfileInformation(nameContainerID,
		monikerContainerID,
		imageContainerID,
		profileInfoJSON,
		doMonikerEllipsis,
		doNameEllipsis) {

	var name, mainImageURL, moniker;
	
	if (jQuery.isEmptyObject(profileInfoJSON)) {
		return;
	}

	$.each(profileInfoJSON, function(key, set){

		if (key.search(/mainImage/i) > -1) {

			mainImageURL = set[0];

		} else if (key.search(/title/i) > -1) {

			moniker = set[0];

		} else if (key.search(/label/i) > -1) {

			name = set[0];

		}

	});

	setProfileName(nameContainerID, name, doNameEllipsis);
	setProfileMoniker(monikerContainerID, moniker, doMonikerEllipsis);
	setProfileImage(imageContainerID, mainImageURL);

}

function visLoaded(nodes){
	
	var jsonedNodes = jQuery.parseJSON(nodes);
	
	$(document).ready(function() { 
		 createTable(collaboratorTableMetadata.tableID, collaboratorTableMetadata.tableContainer, jsonedNodes);
	});

}

function createTable(tableID, tableContainer, tableData) {
	
	var number_of_works = "";
	
	var table = $('<table>');
	table.attr('id', tableID);
	
	table.append($('<caption>').html(collaboratorTableMetadata.tableCaption 
										+ "<a href=\"" + collaboratorTableMetadata.tableCSVFileLink 
										+ "\">(.CSV " + i18nStringsPersonLvl.fileCapitalized + ")</a>"));  

	var header = $('<thead>');
	
	var row = $('<tr>'); 

	row.append($('<th>').html(collaboratorTableMetadata.tableColumnTitle1)); 
	row.append($('<th>').html(collaboratorTableMetadata.tableColumnTitle2 + "" + $('#ego_label').text()));  

	header.append(row);
	
	table.append(header);

	$.each(tableData, function(i, item){ 
		
		/*
		 * Make sure that we dont append a row that belong to the ego in the "co-investigator/author" table.
		 * */
		if (item.url !== unEncodedEgoURI) {
			
			number_of_works = item[collaboratorTableMetadata.jsonNumberWorksProperty];
			
			var row = $('<tr>'); 
	
			row.append($('<td>').html(item.label));
			row.append($('<td>').html(number_of_works));
	
			table.append(row);
		}

	});	

	table.prependTo('#' + tableContainer);
	$('#' + tableContainer + " #loadingData").remove();
	
}

/*
 * Inside both of these functions, '&' are replaced with '%26' because we are externally
 * passing two parameters to the flash code using flashvars (see renderCoAuthorshipVisualization())
 * and they are delimited using '&' too.
 */
function getEncodedURLFor(visType){
	var queryString = "uri="+ egoURI + "&vis=" + visType;
	return location.protocol + "//" + location.host + contextPath + visualizationDataRoot + '?' + queryString.replace(/&/g, '%26');
}

function renderCollaborationshipVisualization() {

//	console.log('visualization is ' + visualization + ' and encodedURL is '+ encodedURL);
	// Version check for the Flash Player that has the ability to start Player
	// Product Install (6.0r65)
	var hasProductInstall = DetectFlashVer(6, 0, 65);

	// Version check based upon the values defined in globals
	var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);

	if ( hasProductInstall && !hasRequestedVersion ) {
		// DO NOT MODIFY THE FOLLOWING FOUR LINES
		// Location visited after installation is complete if installation is
		// required
		var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
		var MMredirectURL = window.location;
		document.title = document.title.slice(0, 47) + " - Flash Player Installation";
		var MMdoctitle = document.title;

		AC_FL_RunContent(
				"src", "playerProductInstall",
				"FlashVars", "MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&MMdoctitle='+MMdoctitle+"",
				"width", "800",
				"height", "840",
				"align", "middle",
				"id", "EgoCentric",
				"quality", "high",
				"bgcolor", "#ffffff",
				"name", "EgoCentric",
				"allowScriptAccess","sameDomain",
				"type", "application/x-shockwave-flash",
				"pluginspage", "http://www.adobe.com/go/getflashplayer"
		);
	} else if (hasRequestedVersion) {
		// if we've detected an acceptable version
		// embed the Flash Content SWF when all tests are passed
		//coAuthorUrl=/vivo1/visualization?vis=coauthorship%26render_mode=data%26uri=http%3A%2F%2Fvivo.iu.edu%2Findividual%2FBrnerKaty&labelField=label&coPIUrl=/vivo1/visualization?vis=coprincipalinvestigator%26render_mode=data%26uri=http%3A%2F%2Fvivo.iu.edu%2Findividual%2FBrnerKaty&labelField=label
		AC_FL_RunContent(
				"src", swfLink,
//				"flashVars", 'coAuthorUrl='+ encodeURL(egoCoAuthorshipDataFeederURL) + '&coPIUrl=' + encodeURL(egoCoPIDataFeederURL) ,			
//				"flashVars", 'coAuthorUrl='+ getEncodedCoAuthorURL() + '&coPIUrl=' + getEncodedCoPIURL() ,
//				"flashVars", 'graphmlUrl=' + getEncodedCoAuthorURL() + '&labelField=label&visType=CoAuthor',
				"flashVars", 'graphmlUrl=' + getEncodedURLFor(visType) + '&labelField=label&visType='+visKeyForFlash,
				"width", "600",
				"height", "850",
				"align", "top",
				"id", "EgoCentric",
				"quality", "high",
				"bgcolor", "#ffffff",
				"name", "EgoCentric",
				"allowScriptAccess","sameDomain",
				"type", "application/x-shockwave-flash",
				"pluginspage", "http://www.adobe.com/go/getflashplayer"
		);
	} else {  // flash is too old or we can't detect the plugin
		var alternateContent = '<br /><h3 style="color: red;">'
			+ i18nStringsPersonLvl.contentRequiresFlash + ' '
			+ '<a href=http://www.adobe.com/go/getflash/>' + i18nStringsPersonLvl.getFlashString + '</a></h3>';
		document.write(alternateContent);  // insert non-flash content
		
	}

}