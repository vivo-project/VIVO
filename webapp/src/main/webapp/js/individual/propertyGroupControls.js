/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
        
    $.extend(this, individualLocalName);
    adjustFontSize();
    padSectionBottoms();
    checkLocationHash();
    
    // ensures that shorter property group sections don't cause the page to "jump around"
    // when the tabs are clicked
    function padSectionBottoms() {
        $.each($('section.property-group'), function() {
            var sectionHeight = $(this).height();
            if ( sectionHeight < 1000 ) {
                    $(this).css('margin-bottom', 1000-sectionHeight + "px");
            }
        });
    }

    // controls the property group tabs
    $.each($('li.clickable'), function() {
        var groupName = $(this).attr("groupName");
        var $propertyGroupLi = $(this);
        
        $(this).click(function() {
            
            if ( $propertyGroupLi.attr("class") == "nonSelectedGroupTab clickable" ) {
                $.each($('li.selectedGroupTab'), function() {
                    $(this).removeClass("selectedGroupTab clickable");
                    $(this).addClass("nonSelectedGroupTab clickable");
                });
                $propertyGroupLi.removeClass("nonSelectedGroupTab clickable");
                $propertyGroupLi.addClass("selectedGroupTab clickable");
            }
            if ( $propertyGroupLi.attr("groupname") == "viewAll" ) {
                processViewAllTab();
            }
            else {
                padSectionBottoms();
                var $visibleSection = $('section.property-group:visible');
                $visibleSection.hide();
                $('h2[pgroup=tabs]').addClass("hidden");
                $('nav#scroller').addClass("hidden"); 
                $('section#' + groupName).show();
            }
            manageLocalStorage();
            return false;
        });
    });
    
    function processViewAllTab() {
        $.each($('section.property-group'), function() {
            $(this).css("margin-bottom", "1px");
            $(this).children('h2').css("margin-top", "-15px").css("border-bottom","1px solid #DFEBE5").css("padding","12px 25px 10px 20px");
            $(this).show(); 
            $('h2[pgroup=tabs]').removeClass("hidden");
            $('nav#scroller').removeClass("hidden"); 
        });        
    }
   
    // If users click a marker on the home page map, they are taken to the profile
    // page of the corresponding country. The url contains the word "Research" in
    // the location hash. Use this to select the Research tab, which displays the
    // researchers who have this countru as a geographic focus.
    function checkLocationHash() {
        if ( location.hash ) {
            // remove the trailing white space
            location.hash = location.hash.replace(/\s+/g, '');
            if ( location.hash.indexOf("map") >= 0 ) {  
                // get the name of the group that contains the geographicFocusOf property.
                // if it doesn't exist, don't do anything.
                if ( $('h3#geographicFocusOf').length ) {
                    var tabName = $('h3#geographicFocusOf').parent('article').parent('div').attr("id");
                    tabName = tabName.replace("Group","");
                    tabNameCapped = tabName.charAt(0).toUpperCase() + tabName.slice(1);
                    // if the name of the first tab section = tabName we don't have to do anything;
                    // otherwise, select the correct tab and deselect the first one
                    var $firstTab = $('li.clickable').first();
                    if ( $firstTab.text() != tabNameCapped ) {
                        // select the correct tab
                        $('li[groupName="' + tabName + '"]').removeClass("nonSelectedGroupTab clickable");
                        $('li[groupName="' + tabName + '"]').addClass("selectedGroupTab clickable");
                        // deselect the first tab
                        $firstTab.removeClass("selectedGroupTab clickable");
                        $firstTab.addClass("nonSelectedGroupTab clickable");
                        $('section.property-group:visible').hide();
                        // show the selected tab section
                        $('section#' + tabName).show();                
                    }
                }
                // if there is a more link, "click" more to show all the researchers
                // we need the timeout delay so that the more link can get rendered                
                setTimeout(geoFocusExpand,250);
            }
            else {
                retrieveLocalStorage();
            }            
        }
        else {
            retrieveLocalStorage();
        }
    }

    function geoFocusExpand() {
        // if the ontology is set to collate by subclass, $list.length will be > 0 
        // this ensures both possibilities are covered
        var $list = $('ul#geographicFocusOfList').find('ul');
        if ( $list.length > 0 )
        {
            var $more = $list.find('a.more-less');
            $more.click();
        }
        else {
            var $more = $('ul#geographicFocusOfList').find('a.more-less');
            $more.click();
        }
    }
    
    //  Next two functions --  keep track of which property group tab was selected,
    //  so if we return from a custom form or a related individual, even via the back button,
    //  the same property group will be selected as before.
    function manageLocalStorage() {
        var localName = this.individualLocalName;
        // is this individual already stored? If not, how many have been stored?
        // If the answer is 3, remove the first one in before adding the new one
        var current = amplify.store(localName);
        var profiles = amplify.store("profiles");
        if ( current == undefined ) {
            if ( profiles == undefined ) {
                var lnArray = [];
                lnArray.push(localName);
                amplify.store("profiles", lnArray);
            }
            else if ( profiles != undefined && profiles.length >= 3 ) {
                firstItem = profiles[0];
                amplify.store(firstItem, null);
                profiles.splice(0,1);
                profiles.push(localName);
                amplify.store("profiles", profiles)
            }
            else if ( profiles != undefined && profiles.length < 3 ) {
                profiles.push(localName);
                amplify.store("profiles", profiles)
            }
        }
        var selectedTab = [];
        selectedTab.push($('li.selectedGroupTab').attr('groupName'));
        amplify.store(localName, selectedTab);
        var checkLength = amplify.store(localName);
        if ( checkLength.length == 0 ) {
            amplify.store(localName, null);
        }
    }

    function retrieveLocalStorage() {
        
        var localName = this.individualLocalName;
        var selectedTab = amplify.store(individualLocalName);
        
        if ( selectedTab != undefined ) {
            var groupName = selectedTab[0];
            
            // unlikely, but it's possible a tab that was previously selected and stored won't be 
            // displayed because the object properties would have been deleted (in non-edit mode). 
            // So ensure that the tab in local storage has been rendered on the page.     
            if ( $("ul.propertyTabsList li[groupName='" + groupName + "']").length ) { 
                // if the selected tab is the default first one, don't do anything
                if ( $('li.clickable').first().attr("groupName") != groupName ) {   
                    // deselect the default first tab
                    var $firstTab = $('li.clickable').first();
                    $firstTab.removeClass("selectedGroupTab clickable");
                    $firstTab.addClass("nonSelectedGroupTab clickable");
                    // select the stored tab
                    $("li[groupName='" + groupName + "']").removeClass("nonSelectedGroupTab clickable");
                    $("li[groupName='" + groupName + "']").addClass("selectedGroupTab clickable");
                    // hide the first tab section
                    $('section.property-group:visible').hide();

                    if ( groupName == "viewAll" ) {
                        processViewAllTab();
                    }

                    // show the selected tab section
                    $('section#' + groupName).show();
                }
            }
        }
    }
    // if there are so many tabs that they wrap to a second line, adjust the font size to 
    //prevent wrapping
    function adjustFontSize() {
        var width = 0;
        $('ul.propertyTabsList li').each(function() {
            width += $(this).outerWidth();
        });
        if ( width < 922 ) {
            var diff = 926-width;
            $('ul.propertyTabsList li:last-child').css('width', diff + 'px');
        }
        else {
            var diff = width-926;
            if ( diff < 26 ) {
                $('ul.propertyTabsList li').css('font-size', "0.96em");
            }
            else if ( diff > 26 && diff < 50 ) {
                $('ul.propertyTabsList li').css('font-size', "0.92em");
            }
            else if ( diff > 50 && diff < 80 ) {
                $('ul.propertyTabsList li').css('font-size', "0.9em");
            }
            else if ( diff > 80 && diff < 130 ) {
                $('ul.propertyTabsList li').css('font-size', "0.84em");
            }
            else if ( diff > 130 && diff < 175 ) {
                $('ul.propertyTabsList li').css('font-size', "0.8em");
            }
            else if ( diff > 175 && diff < 260 ) {
                $('ul.propertyTabsList li').css('font-size', "0.73em");
            }
            else {
                $('ul.propertyTabsList li').css('font-size', "0.7em");
            }

            // get the new width
            var newWidth = 0
            $('ul.propertyTabsList li').each(function() {
                newWidth += $(this).outerWidth();
            });
            var newDiff = 926-newWidth;
            $('ul.propertyTabsList li:last-child').css('width', newDiff + 'px');
        }
    }
});


