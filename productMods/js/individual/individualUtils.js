/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    
    // "more"/"less" HTML truncator for showing more or less content in data property core:overview
    $('.overview-value').truncate({max_length: 500});
    
    // Change background color button when verbose mode is off
    $('a#verbosePropertySwitch:contains("Turn off")').addClass('verbose-off');

    // This function creates and styles the "qTip" tooltip that displays the resource uri when the user clicks the uri icon.
    $('#uriIcon').each(function()
    {
        $(this).qtip(
        {
            content: {
                prerender: true, // We need this for the .click() event listener on 'a.close'
                text: '<h5>share the URI for this profile</h5> <input id="uriLink" type="text" value="' + $('#uriIcon').attr('title') + '" /> <a class="close" href="#">close</a>'
            },
            position: {
                corner: {
                    target: 'bottomLeft',
                    tooltip: 'topLeft'
                }
            },
            show: {
                when: {event: 'click'}
            },
            hide: {
                fixed: true, // Make it fixed so it can be hovered over and interacted with
                when: {
                    target: $('a.close'),
                    event: 'click'
                }
            },
            style: {
                padding: '6px',
                width: 380,
                backgroundColor: '#f1f2ee'
            }
        });
    });
    
    // Prevent close link for URI qTip from requesting bogus '#' href
    $('a.close').click(function() {
        $('#uriIcon').qtip("hide");
        return false;
    });
    
    // Reveal vCard QR code when QR icon is clicked
    $('#qrIcon, .qrCloseLink').click(function() {
        $('#qrCodeImage').toggleClass('hidden');
        return false;
    });
});