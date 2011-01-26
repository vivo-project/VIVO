/* $This file is distributed under the terms of the license in /doc/license.txt$ */

// This file extends and proxies the default behavior defined in vitro/webapp/web/js/menupage/browseByVClass.js

// Saving the original getIndividuals function from browseByVClass
var getPersonIndividuals = browseByVClass.getIndividuals;

// Assigning the proxy function
browseByVClass.getIndividuals = function(vclassUri, alpha, page) {
    url = this.dataServiceUrl + encodeURIComponent(vclassUri);
    if ( alpha && alpha != "all") {
        url = url + '&alpha=' + alpha;
    }
    if ( page ) {
        url += '&page=' + page;
    } else {
        page = 1;
    }
    
    // First wipe currently displayed individuals and existing pagination
    this.individualsInVClass.empty();
    $('nav.pagination').remove();
    
    $.getJSON(url, function(results) {
        // Check to see if we're dealing with pagination
        if ( results.pages.length ) {
            pages = results.pages;
            
            pagination = '<nav class="pagination menupage">';
            pagination += '<h3>page</h3>';
            pagination += '<ul>';
            $.each(pages, function(i, item) {
                anchorOpen = '<a class="page'+ pages[i].text +' round" href="#" title="View page '+ pages[i].text +' of the results">';
                anchorClose = '</a>';
                
                pagination += '<li class="page'+ pages[i].text;
                pagination += ' round';
                // Test for active page
                if ( pages[i].text == page) {
                    pagination += ' selected';
                    anchorOpen = "";
                    anchorClose = "";
                }
                pagination += '" role="listitem">';
                pagination += anchorOpen;
                pagination += pages[i].text;
                pagination += anchorClose;
                pagination += '</li>';
            })
            pagination += '</ul>';
            // browseByVClass.paginationNav.remove();
            
            // Add the pagination above the list of individuals and call the listener
            browseByVClass.individualsContainer.prepend(pagination);
            browseByVClass.paginationListener();
            
        }
        
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
            listItem = '<li class="vcard individual foaf-person" role="listitem" role="navigation">';
            listItem += '<img src="'+ image +'" width="90" height="90" alt="'+ fullName +'" />';
            listItem += '<h1 class="fn thumb"><a href="'+ profileUrl +'" title="View the profile page for '+ fullName +'">'+ fullName +'</a></h1>';
            // Include the calculated preferred title (see above) only if it's not empty
            if ( preferredTitle != "" ) {
                listItem += '<span class="title">'+ preferredTitle +'</span>';
            }
            listItem += '</li>';
            browseByVClass.individualsInVClass.append(listItem);
        })
        
        // Add the pagination below the list as well
        if ( results.pages.length ) {
            browseByVClass.individualsContainer.append(pagination);
        }
        
        // set selected class, alpha and page
        browseByVClass.selectedVClass(results.vclass.URI);
        browseByVClass.selectedAlpha(alpha);
    });
};