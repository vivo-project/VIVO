/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var manageWebpages = {

    /* *** Initial page setup *** */
   
    onLoad: function() {
    
        this.mixIn();               
        this.initPage();       
    },

    mixIn: function() {
        
        // Get the custom form data from the page
        $.extend(this, customFormData);
        $.extend(this, i18nStrings);
    },
    
    // Initial page setup. Called only at page load.
    initPage: function() {

        this.initWebpageData();
       
        this.bindEventListeners();
               
        this.initDragAndDrop();
        
        if ($('.webpage').length) {  // make sure we have at least one webpage
            // Reorder web pages on page load so that previously unranked items get a rank. Otherwise,
            // when we add a new web page, it will get put ahead of any previously unranked web pages, instead
            // of at the end of the list. (It is also helpful to normalize the data before we get started.)            
            this.reorder();
        }     
    },

    // On page load, associate data with each list item. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // items.
    initWebpageData: function() {
        $('.webpage').each(function(index) {
            $(this).data(webpageData[index]);    
            
            // RY We might still need position to put back an element after reordering
            // failure. Rank might already have been reset? Check.
            $(this).data('position', index+1);      
        });
    },
    
    bindEventListeners: function() {

        $('.remove').click(function() {
            manageWebpages.removeWebpage(this);
            return false;
        });
               
    },
    
    /* *** Ajax initializations *** */

    /* Drag-and-drop */
    initDragAndDrop: function() {
        
        var webpages = $('#dragDropList');
        
        // No DD if < 2 items
        if (webpages.children('li') < 2) {
            return;
        }
        
        $('.webpageName').each(function() {
            $(this).attr('title', manageWebpages.dragDropToReorderWebpages);
        });
        
        webpages.sortable({
            cursor: 'move',
            update: function(event, ui) {
                manageWebpages.reorder(event, ui);
            }
        });     
    },
    
    // Reorder webpages. Called on page load and after drag-and-drop and remove.
    // Event and ui parameters are defined only in the case of drag-and-drop.
    reorder: function(event, ui) {
        var webpages = $('li.webpage').map(function(index, el) {
            return $(this).data('webpageUri');
        }).get();

        $.ajax({
            url: manageWebpages.reorderUrl,
            data: {
                predicate: manageWebpages.rankPredicate,
                individuals: webpages
            },
            
            traditional: true, // serialize the array of individuals for the server
            dataType: 'json',
            type: 'POST',
            success: function(data, status, request) {
                var pos;
                $('.webpage').each(function(index){
                    pos = index + 1;
                    // Set the new position for this element. The only function of this value 
                    // is so we can reset an element to its original position in case reordering fails.
                    manageWebpages.setPosition(this, pos);                
                }); 
            },
            error: function(request, status, error) {
                // ui is undefined on page load and after a webpage removal.
                if (ui) {
                    // Put the moved item back to its original position.
                    // Seems we need to do this by hand. Can't see any way to do it with jQuery UI. ??
                    var pos = manageWebpages.getPosition(ui.item),                       
                        nextpos = pos + 1, 
                        webpages = $('#dragDropList'), 
                        next = manageWebpages.findWebpage('position', nextpos);
                    
                    if (next.length) {
                        ui.item.insertBefore(next);
                    }
                    else {
                        ui.item.appendTo(webpages);
                    }
                    
                    alert(manageWebpages.webpageReorderingFailed);                                 
                }      
            }
        });           
    },
    
    getPosition: function(webpage) {
        return $(webpage).data('position');
    },
    
    setPosition: function(webpage, pos) {
        $(webpage).data('position', pos);
    },
    
    findWebpage: function(key, value) {
        var matchingWebpage = $(); // if we don't find one, return an empty jQuery set
        
        $('.webpage').each(function() {
            var webpage = $(this);
            if ( webpage.data(key) === value ) {
                matchingWebpage = webpage; 
                return false; // stop the loop
            }
        });
         
        return matchingWebpage;       
    },
                  
    removeWebpage: function(link) {
        // RY Upgrade this to a modal window
        var removeLast = false,
            message = manageWebpages.confirmWebpageDeletion;
            
        if (!confirm(message)) {
            return false;
        }

        $('a#returnToIndividual').hide();
        $('img#indicator').removeClass('hidden');
        $('a#showAddForm').addClass('disabledSubmit');
        $('a#showAddForm').attr('disabled', 'disabled');
        
        if ($(link)[0] === $('.remove:last')[0]) {
            removeLast = true;
        } 
        
        $.ajax({
            url: $(link).attr('href'),
            type: 'POST', 
            data: {
                deletion: $(link).parents('.webpage').data('webpageUri')
            },
            dataType: 'json',
            context: link, // context for callback
            complete: function(request, status) {
                var webpage;
            
                if (status === 'success') {
                    
                    webpage = $(this).parents('.webpage');
                    
                    webpage.fadeOut(400, function() {
                        var numWebpages;
                        
                        // Remove from the DOM                       
                        $(this).remove();

                        // Actions that depend on the webpage having been removed from the DOM:
                        numWebpages = $('.webpage').length; // retrieve the new length after removing webpage from the DOM
                        
                        // If removed item not last, reorder to remove any gaps
                        if (numWebpages > 0 && ! removeLast) {                            
                            manageWebpages.reorder();
                        }
                            
                        // If fewer than two webpages remaining, disable drag-drop
                        if (numWebpages < 2) {
                            manageWebpages.disableDD();
                        }                           
                        $('img#indicator').fadeOut(100, function() {
                            $(this).addClass('hidden');
                        });

                        $('a#returnToIndividual').fadeIn(100, function() {
                            $(this).show();
                        });
                        $('a#showAddForm').removeClass('disabledSubmit');
                        $('a#showAddForm').attr('disabled', '');
                    });

                } else {
                    alert(manageWebpages.errorRemovingWebpage);
                }
            }
        });        
    },
    
    // Disable DD and associated cues if only one item remains
    disableDD: function() {
        var webpages = $('#dragDropList');
        
        $('#dragDropList').sortable({ disable: true } )
                         /* Use class dd rather than jQuery UI's class ui-sortable, so that we can remove
                          * the class if there's fewer than one webpage. We don't want to remove the ui-sortable
                          * class, in case we want to re-enable DD without a page reload (e.g., if implementing
                          * adding a webpage via Ajax request). 
                          */
                         .removeClass('dd');
              
        $('.webpageName').removeAttr('title');
    }

};

$(document).ready(function() {   
    manageWebpages.onLoad();
}); 
