/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    
    // ensures proper layout when an organization has its webpage link displayed as a thumnail.
    // there's a timing issue, so we can't check the length here, so check the role just to see
    // if $('ul.webpages-withThumnails') exists
    if ( $('ul.webpages-withThumbnails').children('li').length > 0 ) {
        $('div.individual-overview').css("float","left");
        $('div#activeGrantsLink').css("margin-top","30px");
        $('section#individual-info').children('h2#overview').css("clear","both");
    }
    $.extend(this, i18nStrings);

    // "more"/"less" HTML truncator for showing more or less content in data property core:overview
    $('.overview-value').truncate({max_length: 500});
    
    $.fn.exists = function () {
        return this.length !== 0;
    }
    
    $.fn.moreLess = function () {
        $(this).each
    }
    
    var togglePropDisplay = {
        showMore: function($toggleLink, $itemContainer) {
            $toggleLink.click(function() {
                $itemContainer.show();
                $(this).attr('href', '#show less content');
                $(this).text(i18nStrings.displayLess);
                togglePropDisplay.showLess($toggleLink, $itemContainer);
                return false;
            });
        },
        
        showLess: function($toggleLink, $itemContainer) {
            $toggleLink.click(function() {
                $itemContainer.hide();
                $(this).attr('href', '#show more content');
                $(this).text(i18nStrings.displayMoreEllipsis);
                togglePropDisplay.showMore($toggleLink, $itemContainer);
                return false;
            });
        }
    };
    
    // var $propList = $('.property-list').not('>li>ul');
    var $propList = $('.property-list:not(:has(>li>ul))');
    $propList.each(function() {
        var $additionalItems = $(this).find('li:gt(4)');
        if ( $additionalItems.exists() ) {
            // create container for additional elements
            var $itemContainer = $('<div class="additionalItems" />').appendTo(this);
            
            // create toggle link
            var $toggleLink = $('<a class="more-less" href="#show more content" title="' + i18nStrings.showMoreContent + '">' + i18nStrings.displayMoreEllipsis + '</a>').appendTo(this);
            
            $additionalItems.appendTo($itemContainer);
            
            $itemContainer.hide();
            
            togglePropDisplay.showMore($toggleLink, $itemContainer);
        }
    });
    
    var $subPropList = $('.subclass-property-list');
    $subPropList.each(function() {
        var $additionalItems = $(this).find('li:gt(4)');
        if ( $additionalItems.exists() ) {
            // create container for additional elements
            var $itemContainer = $('<div class="additionalItems" />').appendTo(this);
            
            // create toggle link
            var $toggleLink = $('<a class="more-less" href="#show more content" title="' + i18nStrings.showMoreContent + '">' + i18nStrings.displayMoreEllipsis + '</a>').appendTo(this);
            
            $additionalItems.appendTo($itemContainer);
            
            $itemContainer.hide();
            
            togglePropDisplay.showMore($toggleLink, $itemContainer);
        }
    });
    
    var $subPropSibs = $subPropList.closest('li').last().nextAll();
    var $subPropParent = $subPropList.closest('li').last().parent();
    var $additionalItems = $subPropSibs.slice(3);
    if ( $additionalItems.length > 0 ) {
        // create container for additional elements
        var $itemContainer = $('<div class="additionalItems" />').appendTo($subPropParent);
        
        // create toggle link
        var $toggleLink = $('<a class="more-less" href="#show more content" title="' + i18nStrings.showMoreContent + '">' + i18nStrings.displayMoreEllipsis + '</a>').appendTo($subPropParent);
        
        $additionalItems.appendTo($itemContainer);
        
        $itemContainer.hide();
        
        togglePropDisplay.showMore($toggleLink, $itemContainer);
    }
    
    // Change background color button when verbose mode is off
    $('a#verbosePropertySwitch:contains("' + i18nStrings.verboseTurnOff + '")').addClass('verbose-off');
    
    // Reveal vCard QR code when QR icon is clicked
    $('#qrIcon, .qrCloseLink').click(function() {
        $('#qrCodeImage').toggleClass('hidden');
        return false;
    });

    // For pubs and grants on the foaf:person profile, and affiliated people
    // on the foaf:organization profile -- if a pub/grant/person has been hidden 
    // via the "manage" link, we need to ensure that the subclass heading gets removed
    // if there are no items to display for that subclass.
    $.each($('h3'), function() {
        if ( $(this).next().attr('class') == "subclass-property-list hideThis" ) {
            if ( $(this).next().children().length == 0 ) {       
                    $(this).closest('li').remove();
            }
        }
    });
        
    // if there are no selected pubs, hide the manage link; same for grants
    // and affiliated people on the org profile page
    if ( $('ul#relatedBy-Authorship-List').children('li').length < 1 && $('h3#relatedBy-Authorship').attr('class') != "hiddenPubs" ) {
        $('a#managePubLink').hide();
    }

    if ( $('ul#RO_0000053-ResearcherRole-List').children('li').length < 1 &&
            $('ul#RO_0000053-PrincipalInvestigatorRole-List').children('li').length < 1 &&
            $('ul#RO_0000053-CoPrincipalInvestigatorRole-List').children('li').length < 1 &&
            $('ul#RO_0000053-InvestigatorRole-List').children('li').length < 1 &&
            $('h3#RO_0000053-ResearcherRole').attr('class') != "hiddenGrants" ) {
                    $('a#manageGrantLink').hide();
    }

    if ( $('ul#relatedBy-Position-List').children('li').length < 1 && $('h3#relatedBy-Position').attr('class') != "hiddenPeople" ) {
        $('a#managePeopleLink').hide();
    }
   
    // if there are webpages but no contacts (email/phone), extend
    // the webpage border the full width. Used with "2 column" profile view.
    if ( $('h2#contactHeading').length < 1 ) {
        if ( $('div#webpagesContainer').length ) {
             $('div#webpagesContainer').css('width', '100%').css('clear','both');
        }
    }    
});
