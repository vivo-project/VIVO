/* $This file is distributed under the terms of the license in /doc/license.txt$ */

// This file extends and proxies the default behavior defined in vitro/webapp/web/js/menupage/browseByVClass.js

// Saving the original getIndividuals function from browseByVClass
var getPersonIndividuals = browseByVClass.getIndividuals;

// Assigning the proxy function
browseByVClass.getIndividuals = function(vclassUri, alpha, page, scroll) {
    var url = this.dataServiceUrl + encodeURIComponent(vclassUri);
    if ( alpha && alpha != "all") {
        url = url + '&alpha=' + alpha;
    }
    if ( page ) {
        url += '&page=' + page;
    } else {
        page = 1;
    }
    if ( typeof scroll === "undefined" ) {
        scroll = true;
    }
    
    // Scroll to #menupage-intro page unless told otherwise
    if ( scroll != false ) {
        // only scroll back up if we're past the top of the #browse-by section
        var scrollPosition = browseByVClass.getPageScroll();
        var browseByOffset = $('#browse-by').offset();
        if ( scrollPosition[1] > browseByOffset.top) {
            $.scrollTo('#menupage-intro', 500);
        }
    }
    
    $.getJSON(url, function(results) {
        var individualList = "";
        
        // Catch exceptions when empty individuals result set is returned
        // This is very likely to happen now since we don't have individual counts for each letter and always allow the result set to be filtered by any letter
        if ( results.individuals.length == 0 ) {
            browseByVClass.emptyResultSet(results.vclass, alpha)
        } else {
            var vclassName = results.vclass.name;
            $.each(results.individuals, function(i, item) {
                var individual,
                    label, 
                    firstName,
                    lastName, 
                    fullName,
                    mostSpecificTypes, 
                    preferredTitle,
                    moreInfo,
                    uri, 
                    profileUrl,
                    image, 
                    listItem;
                    
                individual = results.individuals[i];
                label = individual.label;
                firstName = individual.firstName;
                lastName = individual.lastName;
                if ( firstName && lastName ) {
                    fullName = firstName + ' ' + lastName;
                } else {
                    fullName = label;
                }
                mostSpecificTypes = individual.mostSpecificTypes;
                if ( individual.preferredTitle ) {
                    preferredTitle = individual.preferredTitle;
                    moreInfo = browseByVClass.getMoreInfo(mostSpecificTypes, vclassName, preferredTitle);
                } else {
                    moreInfo = browseByVClass.getMoreInfo(mostSpecificTypes, vclassName);
                }
                uri = individual.URI;
                profileUrl = individual.profileUrl;
                if ( !individual.thumbUrl ) {
                    image = browseByVClass.baseUrl + '/images/placeholders/person.thumbnail.jpg';
                } else {
                    image = browseByVClass.baseUrl + individual.thumbUrl;
                }
                // Build the content of each list item, piecing together each component
                listItem = '<li class="vcard individual foaf-person" role="listitem" role="navigation">';
                listItem += '<img src="'+ image +'" width="90" alt="'+ fullName +'" />';
                listItem += '<h1 class="fn thumb"><a href="'+ profileUrl +'" title="View the profile page for '+ fullName +'">'+ fullName +'</a></h1>';
                if ( moreInfo != '' ) {
                    listItem += '<span class="title">'+ moreInfo +'</span>';
                }
                listItem += '</li>';
                individualList += listItem;
            })
            
            // Remove existing content
            browseByVClass.wipeSlate();
            
            // And then add the new content
            browseByVClass.individualsInVClass.append(individualList);
            
            // Check to see if we're dealing with pagination
            if ( results.pages.length ) {
                var pages = results.pages;
                browseByVClass.pagination(pages, page);
            }
            
        }
        
        // Set selected class, alpha and page
        // Do this whether or not there are any results
        $('h3.selected-class').text(results.vclass.name);
        browseByVClass.selectedVClass(results.vclass.URI);
        browseByVClass.selectedAlpha(alpha);
    });
};