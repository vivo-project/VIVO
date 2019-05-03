/* $This file is distributed under the terms of the license in LICENSE$ */

$(document).ready(function(){
    // This function creates and styles the "qTip" tooltip that displays the bubble text when the user hovers
    // over the research area "group" icon.

    $.extend(this, i18nStrings);

    $('head').append('<style id="researchAreaCSS">.qtip { font-size: 14px; max-width: none !important; } .researchAreaTip { background: url(' + imagesPath + '/individual/researchAreaBubble.png) no-repeat; } </style>');

    $('#researchAreaIcon').each(function()
    {
        $(this).qtip(
        {
            prerender: true,
            content: {
                text: '<div style="padding-top:0.5em;margin-left:-14px;color:white">' + i18nStrings.researchAreaTooltipOne + '</div><div style="margin-left:-14px;color:white">' + i18nStrings.researchAreaTooltipTwo + '</div>'
            },
            position: {
                my: 'top left',
                at: 'bottom center',
                adjust: {
                    x:22,
                    y:30
                }
            },
            style: {
                classes: 'researchAreaTip',
                height: 56,
                width: 180,
            }
        });
    });

    $('head').append('<style id="bubbleCSS">.qtip { font-size: 14px; max-width: none !important; } .bubbleTip { url(' + imagesPath + '/individual/toolTipBubble.png) no-repeat; } </style>');

    $('#fullViewIcon').each(function()
    {
        $(this).qtip(
        {
            content: {
                text: '<div style="padding-top:0.5em;color:white">' + i18nStrings.quickviewTooltip + '</div>'
            },
            position: {
                my: 'top left',
                at: 'bottom center',
                adjust: {
                    x:27,
                    y:30
                }
            },
            style: {
                classes: 'bubbleTip',
                height: 56,
                width: 140,

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
                my: 'top left',
                at: 'bottom center',
                adjust: {
                    x:28,
                    y:30
                }
            },
            style: {
                classes: 'bubbleTip',
                height: 56,
                width: 144,
            }
        });
    });
});
