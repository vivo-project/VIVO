/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    
    // This function creates and styles the "qTip" tooltip that displays when the user hovers over the info icons.
    $('.filterInfoIcon').each(function()
    {
       var tipText ;
       var tipLocation = "topLeft";
       if ( $(this).attr('id') == 'imageIconOne' ) {
           tipText = $('#toolTipOne').html();
        }
        else if ( $(this).attr('id') == 'imageIconTwo' ) {
            tipText = $('#toolTipTwo').html();
        }
        else {
            tipText = $('#toolTipThree').html();
            tipLocation = "topRight"
        };
       $(this).qtip(
       {
          content: {
              text: tipText 
          },
          position: {
              corner: {
              target: 'center',
              tooltip: tipLocation
              }
          },
          show: {
             when: {event: 'mouseover'}
          },
          hide: {
             fixed: true // Make it fixed so it can be hovered over
          },
          style: {
             padding: '6px 6px', // Give it some extra padding
             width: 500,
             textAlign: 'left',
             backgroundColor: '#ffffc0',
             fontSize: '.7em',
             padding: '6px 10px 6px 10px',
             lineHeight: '14px' 
         }
       });
    });

});