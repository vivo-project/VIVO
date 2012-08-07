/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    
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
                $(this).text('less');
                togglePropDisplay.showLess($toggleLink, $itemContainer);
                return false;
            });
        },
        
        showLess: function($toggleLink, $itemContainer) {
            $toggleLink.click(function() {
                $itemContainer.hide();
                $(this).attr('href', '#show more content');
                $(this).text('more...');
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
            var $toggleLink = $('<a class="more-less" href="#show more content">more...</a>').appendTo(this);
            
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
            var $toggleLink = $('<a class="more-less" href="#show more content">more...</a>').appendTo(this);
            
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
        var $toggleLink = $('<a class="more-less" href="#show more content">more...</a>').appendTo($subPropParent);
        
        $additionalItems.appendTo($itemContainer);
        
        $itemContainer.hide();
        
        togglePropDisplay.showMore($toggleLink, $itemContainer);
    }
    
    // Change background color button when verbose mode is off
    $('a#verbosePropertySwitch:contains("Turn off")').addClass('verbose-off');
    
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
    if ( $('ul#authorInAuthorshipList').children('li').length < 1 && $('h3#authorInAuthorship').attr('class') != "hiddenPubs" ) {
        $('a#managePubLink').hide();
    }

    if ( $('ul#hasResearcherRoleList').children('li').length < 1 &&
            $('ul#hasPrincipalInvestigatorRoleList').children('li').length < 1 &&
            $('ul#hasCo-PrincipalInvestigatorRoleList').children('li').length < 1 &&
            $('ul#hasInvestigatorRoleList').children('li').length < 1 &&
            $('h3#hasResearcherRole').attr('class') != "hiddenGrants" ) {
                    $('a#manageGrantLink').hide();
    }

    if ( $('ul#organizationForPositionList').children('li').length < 1 && $('h3#organizationForPosition').attr('class') != "hiddenPeople" ) {
        $('a#managePeopleLink').hide();
    }

});
