/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    // This function creates and styles the "qTip" tooltip that displays the bubble text when the user hovers
    // over the research area "group" icon.
    $('#researchAreaIcon').each(function()
    {   
        $(this).qtip(
        {
            content: {
                prerender: true,
                text: '&nbsp;'
            },
            position: {
                corner: {
                    target: 'topleft',
                    tooltip: 'bottomcenter'
                },
                adjust: {
                    x:22,
                    y:30
                }
            },
            show: {
                when: {
                    event: 'mouseenter'
                }
            },
            hide: {
                fixed: false, 
                when: {
                    event: 'mouseleave'
                }
            },
            style: {
                padding: '0em',
                height: 56,
                width: 180,
                border: 'none',
                background:'url(' + imagesPath + '/individual/researchAreaBubble.png)  no-repeat'
            }
        });
    });

    $('#fullViewIcon').each(function()
    {
        $(this).qtip(
        {
            content: {
                text: '&nbsp;'
            },
            position: {
                corner: {
                    target: 'topleft',
                    tooltip: 'bottomcenter'
                },
                adjust: {
                    x:27,
                    y:30
                }
            },
            show: {
                
                when: {
                    event: 'mouseenter'
                }
            },
            hide: {
                fixed: false, 
                when: {
                    event: 'mouseleave'
                }
            },
            style: {
                padding: '0em',
                height: 56,
                width: 140,
                border: 'none',
                background: 'url(' + imagesPath + '/individual/quickViewBubble.png)  no-repeat'
            }
        });
    });

    $('#quickViewIcon').each(function()
    {
        $(this).qtip(
        {
            content: {
                text: '&nbsp;'
            },
            position: {
                corner: {
                    target: 'topleft',
                    tooltip: 'bottomcenter'
                },
                adjust: {
                    x:28,
                    y:30
                }
            },
            show: {
                when: {event: 'mouseenter'}
            },
            hide: {
                fixed: false, 
                when: {
                    event: 'mouseleave'
                }
            },
            style: {
                padding: '0em',
                height: 56,
                width: 144,
                border: 'none',
                background: 'url(' + imagesPath + '/individual/fullViewBubble.png)  no-repeat'
            }
        });
    });
});