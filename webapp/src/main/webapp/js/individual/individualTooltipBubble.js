/* $This file is distributed under the terms of the license in LICENSE$ */

$(document).ready(function(){
    // This function creates and styles the bootstrap-popper tooltip that displays the bubble text when the user hovers
    // over the research area "group" icon.

    $.extend(this, i18nStrings);

    let tooltips = [
        {
            querySelector: "#researchAreaIcon",
            data: {
                title: "<div>" + i18nStrings.researchAreaTooltipOne + "</div><div>" + i18nStrings.researchAreaTooltipTwo + "</div>",
                html: true,
                fallbackPlacements: ['bottom', 'left', 'top', 'right'],
				customClass: "vivoTooltip"
            }
        },
        {
            querySelector: "#fullViewIcon",
            data: {
                content: i18nStrings.quickviewTooltip,
                html: true,
                fallbackPlacements: ['bottom', 'left', 'top', 'right'],
				customClass: "vivoTooltip"
            }
        },
        {
            querySelector: "#quickViewIcon",
            data: {
                content: "<div>" + i18nStrings.standardviewTooltipOne + '</div><div>' + i18nStrings.standardviewTooltipTwo + "</div>",
                html: true,
                fallbackPlacements: ['bottom', 'left', 'top', 'right'],
				customClass: "vivoTooltip"
            }
        },
    ]

    tooltips.forEach(tooltip => {
        setTooltip(tooltip.querySelector, tooltip.data)
    })

});
