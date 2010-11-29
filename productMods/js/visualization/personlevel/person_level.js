/* $This file is distributed under the terms of the license in /doc/license.txt$ */

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
			url: contextPath + "/visualization",
			data: ({vis: "utilities", vis_mode: "PERSON_LEVEL_URL", uri: given_uri}),
			dataType: "text",
			async: false,
			success:function(data){
		}
		}).responseText;

		return finalURL;


	} else if (type == "profile") {

		finalURL = $.ajax({
			url: contextPath + "/visualization",
			data: ({vis: "utilities", vis_mode: "PROFILE_URL", uri: given_uri}),
			dataType: "text",
			async: false,
			success:function(data){
		}
		}).responseText;

		return finalURL;

	} else if (type == "image") {

		finalURL = $.ajax({
			url: contextPath + "/visualization",
			data: ({vis: "utilities", vis_mode: "IMAGE_URL", uri: given_uri}),
			dataType: "text",
			async: false,
			success:function(data){
		}
		}).responseText;

		return finalURL;

	} else if (type == "profile_info") {

		var profileInfoJSON = $.ajax({
			url: contextPath + "/visualization",
			data: ({vis: "utilities", vis_mode: "PROFILE_INFO", uri: given_uri}),
			dataType: "json",
			async: false,
			success:function(data){
		}
		}).responseText;

		return profileInfoJSON;

	}

	// });
}

$.fn.image = function(src, successFunc, failureFunc){
	return this.each(function(){ 
		var profileImage = new Image();
		profileImage.onerror = failureFunc;
		profileImage.onload = successFunc;
		profileImage.src = src;

		return profileImage;
	});
};

function setProfileImage(imageContainerID, mainImageURL, contextPath) {
	
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
	
	if (moniker.length > 30 && doEllipsis) {
		
		finalDisplayMoniker = moniker.substr(0,30) + "...";
		
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
	
	if (name.length > 30 && doNameEllipsis) {
		
		finalDisplayName = name.substr(0,30) + "...";
		
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

	var name, mainImageURL, imageContextPath, moniker;
	
	if (jQuery.isEmptyObject(profileInfoJSON)) {
		return;
	}

	$.each(profileInfoJSON, function(key, set){

		if (key.search(/mainImage/i) > -1) {

			mainImageURL = set[0];

		} else if (key.search(/imageContextPath/i) > -1) {

			imageContextPath = set[0];

		} else if (key.search(/moniker/i) > -1) {

			moniker = set[0];

		} else if (key.search(/label/i) > -1) {

			name = set[0];

		}

	});

	setProfileName(nameContainerID, name, doNameEllipsis);
	setProfileMoniker(monikerContainerID, moniker, doMonikerEllipsis);
	setProfileImage(imageContainerID, mainImageURL, imageContextPath);

}

function visLoaded(nodes){

	var jsonedNodes = jQuery.parseJSON(nodes);

	$(document).ready(function() { 
		 createTable("coauthorships_table", "coauth_table_container", jsonedNodes.slice(1));
	});

}

function createTable(tableID, tableContainer, tableData) {
	
	var table = $('<table>');
	table.attr('id', tableID);
	
	table.append($('<caption>').html("Co-authors <a href=\"" + egoCoAuthorsListDataFileURL + "\">(.CSV File)</a>"));  
	
	var header = $('<thead>');
	
	var row = $('<tr>'); 

	var authorTH = $('<th>');
	authorTH.html("Author");
	row.append(authorTH);
	
	row.append($('<th>').html("Publications with <br />" + $('#ego_label').text()));  

	header.append(row);
	
	table.append(header);

	$.each(tableData, function(i, item){ 

		var row = $('<tr>'); 

		row.append($('<td>').html(item.label));
		row.append($('<td>').html(item.number_of_authored_works));

		table.append(row);

	});	

	table.prependTo('#' + tableContainer);
	$('#' + tableContainer + " #loadingData").remove();
}

//renderStatsOnNodeClicked, CoRelations, noOfCoRelations
//function nodeClickedJS(json){
function renderStatsOnNodeClicked(json){
	var obj = jQuery.parseJSON(json);

	$("#dataPanel").attr("style","visibility:visible");
	$("#works").empty().append(obj.number_of_authored_works);

	/*
	 * Here obj.url points to the uri of that individual
	 */
	if(obj.url){
		
		if (obj.url == egoURI) {
			
			$("#authorName").addClass('author_name').removeClass('neutral_author_name');
			$('#num_works > .author_stats_text').text('Publication(s)');
			$('#num_authors > .author_stats_text').text('Co-author(s)');
			
		} else {

			$("#authorName").addClass('neutral_author_name').removeClass('author_name');
			$('#num_works > .author_stats_text').text('Joint Publication(s)');
			$('#num_authors > .author_stats_text').text('Joint Co-author(s)');
			
		}
		
		$("#profileUrl").attr("href", getWellFormedURLs(obj.url, "profile"));
		$("#coAuthorshipVisUrl").attr("href", getWellFormedURLs(obj.url, "coauthorship"));
		processProfileInformation("authorName", 
				"profileMoniker",
				"profileImage",
				jQuery.parseJSON(getWellFormedURLs(obj.url, "profile_info")),
				true,
				true);
		
		

	} else{
		$("#profileUrl").attr("href","#");
		$("#coAuthorshipVisUrl").attr("href","#");
	}

	$("#coAuthors").empty().append(obj.num_coauthors);	
	
	$("#firstPublication").empty().append(obj.earliest_publication);
	(obj.earliest_publication)?$("#fPub").attr("style","visibility:visible"):$("#fPub").attr("style","visibility:hidden");
	$("#lastPublication").empty().append(obj.latest_publication);
	(obj.latest_publication)?$("#lPub").attr("style","visibility:visible"):$("#lPub").attr("style","visibility:hidden");

	// obj.url:the url parameter for node

}

/*
 * Inside both of these functions, '&' are replaced with '%26' because we are externally
 * passing two parameters to the flash code using flashvars (see renderCoAuthorshipVisualization())
 * and they are delimited using '&' too.
 */

function getEncodedCoAuthorURL(){
	
	var queryString = "uri="+ egoURI + "&vis=coauthorship&render_mode=data";
//	console.log('domainParam is '+ domainParam);
//	console.log('CoAuthorURL is ' + domainParam + '?' + queryString.replace(/&/g, '%26'));
	return domainParam + '?' + queryString.replace(/&/g, '%26');	
}

function getEncodedCoPIURL(){
	
	var queryString = "uri="+ egoURI+ "&vis=coprincipalinvestigator&render_mode=data";
//	console.log('CoPIURL is ' + domainParam + '?' + queryString.replace(/&/g, '%26') );	
	return domainParam + '?' + queryString.replace(/&/g, '%26');
}

function renderCoAuthorshipVisualization() {

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
				"flashVars", 'graphmlUrl=' + getEncodedCoAuthorURL() + '&labelField=label&visType=CoAuthor',
				"width", "800",
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
			+ 'This content requires the Adobe Flash Player. '
			+ '<a href=http://www.adobe.com/go/getflash/>Get Flash</a></h3>';
		document.write(alternateContent);  // insert non-flash content
		
	}

}