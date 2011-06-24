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
              title: { 
                  text: 'URI',
              },
              text: '<br /><span id="uri-text">' + $("#uriIcon").attr("title") + '</span><br /><br />'
          },
          position: {
              corner: {
              target: 'leftBottom',
              tooltip: 'bottomLeft'
              }
          },
          show: {
             when: {event: 'click'}
          },
          hide: {
             fixed: true // Make it fixed so it can be hovered over
          },
          style: {
              title: {'backgroundColor': '#2485AE', 'color': 'white', 'fontFamily': '"Lucida Sans Unicode","Lucida Grande", Geneva, helvetica, sans-serif'},
             padding: '6px 6px', // Give it some extra padding
             width: 380,
             textAlign: 'center',
             backgroundColor: '#f1f2ee'
         }
       });
    });
    
    // Reveal vCard QR code when QR icon is clicked
    $('#qrIcon, .qrCloseLink').click(function() {
        $('#qrCodeImage').toggleClass('hide');
        // event.preventDefault();
        return false;
    });
});