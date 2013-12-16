/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    // This function creates and styles the "qTip" tooltip that displays the bubble text when the user hovers
    // over the research area "group" icon. 
    
    $.extend(this, i18nStrings);

    $('#researchAreaIcon').each(function()
    {   
        $(this).qtip(
        {
            content: {
                prerender: true,
                text: '<div style="padding-top:0.5em;margin-left:-14px;color:white">' + i18nStrings.researchAreaTooltipOne + '</div><div style="margin-left:-14px;color:white">' + i18nStrings.researchAreaTooltipTwo + '</div>'
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
                textAlign: 'center',
                fontSize: '0.7em',
                lineHeight: '15px',
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
                text: '<div style="padding-top:0.5em;color:white">' + i18nStrings.quickviewTooltip + '</div>'
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
                textAlign: 'center',
                fontSize: '0.7em',
                lineHeight: '15px',
                width: 140,
                border: 'none',
                background: 'url(' + imagesPath + '/individual/toolTipBubble.png)  no-repeat'
            }
        });
    });

    $('#quickViewIcon').each(function()
    {
        $(this).qtip(
        {
            content: {
                text: '<div style="padding-top:0.5em;color:white">' + i18nStrings.standardviewTooltipOne + '</div><div style="color:white">' + i18nStrings.standardviewTooltipTwo + '</div>'
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
                textAlign: 'center',
                fontSize: '0.7em',
                lineHeight: '15px',
                width: 144,
                border: 'none',
                background: 'url(' + imagesPath + '/individual/toolTipBubble.png)  no-repeat'
            }
        });
    });
});