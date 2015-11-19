/* $This file is distributed under the terms of the license in /doc/license.txt$ */
$.extend(this, i18nStringsCoauthorship);

var collaboratorTableMetadata = {
	tableID: "coauthorships_table",
	tableContainer: "coauth_table_container",
	tableCaption: i18nStringsCoauthorship.coAuthorsString + " ",
	tableColumnTitle1: i18nStringsCoauthorship.authorString,
	tableColumnTitle2: i18nStringsCoauthorship.publicationsWith + " <br />",
	tableCSVFileLink: egoCoAuthorsListDataFileURL,
	jsonNumberWorksProperty: "number_of_authored_works" 
};

var visType = "coauthorship"; 
var visKeyForFlash = "CoAuthor";

function renderStatsOnNodeClicked(json){
	
	var obj = jQuery.parseJSON(json);
	
	var works = "";
	var persons = "";
	var relation = "";
	var earliest_work = "";
	var latest_work = "";
	var number_of_works = "";
	
	works = i18nStringsCoauthorship.publicationsString;
	persons = i18nStringsCoauthorship.coauthorsString;
	relation = "coauthorship"
	earliest_work = obj.earliest_publication;
	latest_work = obj.latest_publication;
	number_of_works = obj.number_of_authored_works;

	$("#dataPanel").attr("style","visibility:visible");
	$("#works").empty().append(number_of_works);

	/*
	 * Here obj.url points to the uri of that individual
	 */
	if(obj.url){
		
		if (obj.url === unEncodedEgoURI) {
			
			$("#authorName").addClass('author_name').removeClass('neutral_author_name');
			$('#num_works > .author_stats_text').text(works);
			$('#num_authors > .author_stats_text').text(persons);
			
			var vivoProfileURL = $("a#profileUrl").detach();
			
			$("#profile-links").empty().append(vivoProfileURL);
			
		} else {

			$("#authorName").addClass('neutral_author_name').removeClass('author_name');
			$('#num_works > .author_stats_text').text('Joint ' + works);
			$('#num_authors > .author_stats_text').text('Joint ' + persons);

			if ($("#coAuthorshipVisUrl").length > 0) {
				
				$("#coAuthorshipVisUrl").attr("href", getWellFormedURLs(obj.url, relation));
				
			} else {
				
				$("#profile-links")
				.append(" | ")
				.append('<a href="' + getWellFormedURLs(obj.url, relation) 
							+ '" id="coAuthorshipVisUrl">Co-author network</a>');	
			} 
		}
		
		$("#profileUrl").attr("href", getWellFormedURLs(obj.url, "profile"));
		
		processProfileInformation("authorName", 
				"profileMoniker",
				"profileImage",
				jQuery.parseJSON(getWellFormedURLs(obj.url, "profile_info")),
				true,
				true);
		
		

	} else {
		$("#profileUrl").attr("href","#");
		$("#coAuthorshipVisUrl").attr("href","#");
	}

	$("#coAuthors").empty().append(obj.noOfCorelations);	
	
	$("#firstPublication").empty().append(earliest_work);
	(earliest_work)?$("#fPub").attr("style","visibility:visible"):$("#fPub").attr("style","visibility:hidden");
	$("#lastPublication").empty().append(latest_work);
	(latest_work)?$("#lPub").attr("style","visibility:visible"):$("#lPub").attr("style","visibility:hidden");

}