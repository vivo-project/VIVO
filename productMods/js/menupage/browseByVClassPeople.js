/* $This file is distributed under the terms of the license in /doc/license.txt$ */

// This file extends and proxies the default behavior defined in vitro/webapp/web/js/menupage/browseByVClass.js

// Saving the original getIndividuals function from browseByVClass
var getPersonIndividuals = browseByVClass.getIndividuals;

// Assigning the proxy function
browseByVClass.getIndividuals = function(vclassUri, alpha) {
    // alert("This is the mothership!");
    url = this.dataServiceUrl + encodeURIComponent(vclassUri);
    if ( alpha && alpha != "all") {
        url = url + '&alpha=' + alpha;
    }
    
    // First wipe currently displayed individuals
    this.individualsInVClass.empty();
    
    $.getJSON(url, function(results) {
        $.each(results.individuals, function(i, item) {
            label = results.individuals[i].label;
            firstName = results.individuals[i].firstName;
            lastName = results.individuals[i].lastName;
            if ( firstName && lastName ) {
                fullName = firstName + ' ' + lastName;
            } else {
                fullName = label;
            }
            moniker = results.individuals[i].moniker;
            vclassName = results.individuals[i].vclassName;
            if ( results.individuals[i].preferredTitle == "") {
               // Use the moniker only if it's not empty and not equal to the VClass name
               if ( moniker != vclassName && moniker != "" ) {
                   preferredTitle = moniker;
               } else {
                   preferredTitle = "";
               }
            } else {
               preferredTitle = results.individuals[i].preferredTitle;
            }
            // preferredTitle = results.individuals[i].preferredTitle;
            uri = results.individuals[i].URI;
            profileUrl = results.individuals[i].profileUrl;
            if ( !results.individuals[i].thumbUrl ) {
                image = browseByVClass.baseUrl + '/images/placeholders/person.thumbnail.jpg';
            } else {
                image = browseByVClass.baseUrl + results.individuals[i].thumbUrl;
            }
            // Build the content of each list item, piecing together each component
            listItem = '<li class="vcard individual-foaf-person" role="listitem" role="navigation">';
            listItem += '<img src="'+ image +'" width="90" height="90" alt="'+ fullName +'" />';
            listItem += '<h1 class="fn thumb"><a href="'+ profileUrl +'" title="View the profile page for '+ fullName +'">'+ fullName +'</a></h1>';
            // Include the calculated preferred title (see above) only if it's not empty
            if ( preferredTitle != "" ) {
                listItem += '<span class="title">'+ preferredTitle +'</span>';
            }
            listItem += '</li>';
            browseByVClass.individualsInVClass.append(listItem);
        })
        // set selected class and alpha
        browseByVClass.selectedVClass(results.vclass.URI);
        browseByVClass.selectedAlpha(alpha);
    });
};