/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var browseByVClass = {
    // Initial page setup
    onLoad: function() {
        this.mergeFromTemplate();
        this.initObjects();
        this.bindEventListeners();
    },
    
    // Add variables from menupage template
    mergeFromTemplate: function() {
        $.extend(this, menupageData);
    },
    
    // Create references to frequently used elements for convenience
    initObjects: function() {
        this.vgraphVClasses = $('#vgraph-classes');
        this.vgraphVClassLinks = $('#vgraph-classes li a');
        this.browseVClasses = $('#browse-classes');
        this.browseVClassLinks = $('#browse-classes li a');
        this.alphaIndex = $('#alpha-browse-individuals');
        this.alphaIndexLinks = $('#alpha-browse-individuals li a');
        this.individualsInVClass = $('#individuals-in-class ul');
        this.individualsContainer = $('#individuals-in-class');
    },
    
    // Event listeners. Called on page load
    bindEventListeners: function() {
        // Listeners for vClass switching
        this.vgraphVClassLinks.click(function() {
            uri = $(this).attr('data-uri');
            browseByVClass.getIndividuals(uri);
        });
        
        this.browseVClassLinks.click(function() {
            uri = $(this).attr('data-uri');
            browseByVClass.getIndividuals(uri);
            return false;
        });
        
        // Listener for alpha switching
        this.alphaIndexLinks.click(function() {
            uri = $('#browse-classes li a.selected').attr('data-uri');
            alpha = $(this).attr('data-alpha');
            browseByVClass.getIndividuals(uri, alpha);
            return false;
        });
        
        // Call the pagination listener
        this.paginationListener();
    },
    
    // Listener for page switching -- separate from the rest because it needs to be callable
    paginationListener: function() {
        $('.pagination li a').click(function() {
            uri = $('#browse-classes li a.selected').attr('data-uri');
            alpha = $('#alpha-browse-individuals li a.selected').attr('data-alpha');
            page = $(this).attr('data-page');
            browseByVClass.getIndividuals(uri, alpha, page);
            return false;
        });
    },
    
    // Load individuals for default class as specified by menupage template
    defaultVClass: function() {
        if ( this.defaultBrowseVClassURI != "false" ) {
            this.getIndividuals(this.defaultBrowseVClassUri, "all", 1, false);
        }
    },
    
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
    
    // Where all the magic happens -- gonna fetch me some individuals
    getIndividuals: function(vclassUri, alpha, page, scroll) {
        url = this.dataServiceUrl + encodeURIComponent(vclassUri);
        if ( alpha && alpha != "all") {
            url += '&alpha=' + alpha;
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
            scrollPosition = browseByVClass.getPageScroll();
            browseByOffset = $('#browse-by').offset();
            if ( scrollPosition[1] > browseByOffset.top) {
                $.scrollTo('#menupage-intro', 500);
            }
        }
        
        $.getJSON(url, function(results) {
            individualList = "";
            
            // Catch exceptions when empty individuals result set is returned
            // This is very likely to happen now since we don't have individual counts for each letter and always allow the result set to be filtered by any letter
            if ( results.individuals.length == 0 ) {
                browseByVClass.emptyResultSet(results.vclass, alpha)
            } else {
                $.each(results.individuals, function(i, item) {
                    label = browseByVClass.toTitleCase(results.individuals[i].label);
                    moniker = results.individuals[i].moniker;
                    vclassName = results.individuals[i].vclassName;
                    uri = results.individuals[i].URI;
                    profileUrl = results.individuals[i].profileUrl;
                    if ( results.individuals[i].thumbUrl ) {
                        image = browseByVClass.baseUrl + results.individuals[i].thumbUrl;
                    }
                    // Build the content of each list item, piecing together each component
                    listItem = '<li class="individual" role="listitem" role="navigation">';
                    if ( typeof results.individuals[i].thumbUrl !== "undefined" ) {
                        listItem += '<img src="'+ image +'" width="90" alt="'+ label +'" /><h1 class="thumb">';
                    } else {
                        listItem += '<h1>';
                    }
                    listItem += '<a href="'+ profileUrl +'" title="View the profile page for '+ label +'">'+ label +'</a></h1>';
                    // Include the moniker only if it's not empty and not equal to the VClass name
                    if ( moniker != vclassName && moniker != "" ) {
                        listItem += '<span class="title">'+ moniker +'</span>';
                    }
                    listItem += '</li>';
                    // browseByVClass.individualsInVClass.append(listItem);
                    individualList += listItem;
                })
                
                // Remove existing content
                browseByVClass.wipeSlate();
                
                // And then add the new content
                browseByVClass.individualsInVClass.append(individualList);
                
                // Check to see if we're dealing with pagination
                if ( results.pages.length ) {
                    pages = results.pages;
                    browseByVClass.pagination(pages, page);
                }
                
                selectedClassHeading = '<h3 class="selected-class">'+ results.vclass.name +'</h3>';
                browseByVClass.individualsContainer.prepend(selectedClassHeading);
                
                // Set selected class, alpha and page
                browseByVClass.selectedVClass(results.vclass.URI);
                browseByVClass.selectedAlpha(alpha);
            }
        });
    },
    
    // getPageScroll() by quirksmode.org
    getPageScroll: function() {
        var xScroll, yScroll;
        if (self.pageYOffset) {
          yScroll = self.pageYOffset;
          xScroll = self.pageXOffset;
        } else if (document.documentElement && document.documentElement.scrollTop) {
          yScroll = document.documentElement.scrollTop;
          xScroll = document.documentElement.scrollLeft;
        } else if (document.body) {// all other Explorers
          yScroll = document.body.scrollTop;
          xScroll = document.body.scrollLeft;
        }
        return new Array(xScroll,yScroll)
    },
    
    // Print out the pagination nav if called
    pagination: function(pages, page) {
        pagination = '<div class="pagination menupage">';
        pagination += '<h3>page</h3>';
        pagination += '<ul>';
        $.each(pages, function(i, item) {
            anchorOpen = '<a class="round" href="#" title="View page '+ pages[i].text +' of the results" data-page="'+ pages[i].index +'">';
            anchorClose = '</a>';
            
            pagination += '<li class="round';
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
        
        // Add the pagination above and below the list of individuals and call the listener
        browseByVClass.individualsContainer.prepend(pagination);
        browseByVClass.individualsContainer.append(pagination);
        browseByVClass.paginationListener();
    },
    
    // Toggle the active class so it's clear which is selected
    selectedVClass: function(vclassUri) {
        // Remove active class on all vClasses
        $('#browse-classes li a.selected').removeClass('selected');
        
        // Add active class for requested vClass
        $('#browse-classes li a[data-uri="'+ vclassUri +'"]').addClass('selected');
    },

    // Toggle the active letter so it's clear which is selected
    selectedAlpha: function(alpha) {
        // if no alpha argument sent, assume all
        if ( alpha == null ) {
            alpha = "all";
        }
        // Remove active class on all letters
        $('#alpha-browse-individuals li a.selected').removeClass('selected');
        
        // Add active class for requested alpha
        $('#alpha-browse-individuals li a[data-alpha="'+ alpha +'"]').addClass('selected');
    },
    
    // Wipe the currently displayed class heading, individuals, no-content message, and existing pagination
    wipeSlate: function() {
        $('h3.selected-class').remove();
        browseByVClass.individualsInVClass.empty();
        $('p.no-individuals').remove();
        $('.pagination').remove();
    },
    
    // When no individuals are returned for the AJAX request, print a reasonable message for the user
    emptyResultSet: function(vclass, alpha) {
        this.wipeSlate();
        this.selectedAlpha(alpha);
        
        if ( alpha != "all" ) {
            nothingToSeeHere = '<p class="no-individuals">There are no '+ vclass.name +' individuals whose name starts with <em>'+ alpha.toUpperCase() +'</em>.</p> <p class="no-individuals">Please try another letter or browse all.</p>';
        } else {
            nothingToSeeHere = '<p class="no-individuals">There are no '+ vclass.name +' individuals in the system.</p> <p class="no-individuals">Please select another class from the list.</p>';
        }
        browseByVClass.individualsContainer.prepend(nothingToSeeHere);
    }
};

$(document).ready(function() {
    browseByVClass.onLoad();
    browseByVClass.defaultVClass();
});