/* $This file is distributed under the terms of the license in LICENSE$ */

$(document).ready(function(){
    $.extend(this, i18nStrings);

    let tooltips = [
        {
            querySelector: "#researchAreaIcon",
            data: {
                title: "<div>" + i18nStrings.researchAreaTooltipOne + "</div><div>" + i18nStrings.researchAreaTooltipTwo + "</div>",
                placements: ['top', 'right', 'bottom', 'left'],
				customClass: "vitroTooltip"
            }
        },
        {
            querySelector: "#fullViewIcon",
            data: {
                title: i18nStrings.quickviewTooltip,
                placements: ['top', 'right', 'bottom', 'left'],
				customClass: "vitroTooltip"
            }
        },
        {
            querySelector: "#quickViewIcon",
            data: {
                title: "<div>" + i18nStrings.standardviewTooltipOne + '</div><div>' + i18nStrings.standardviewTooltipTwo + "</div>",
                placements: ['top', 'right', 'bottom', 'left'],
				customClass: "vitroTooltip"
            }
        },
    ]

    tooltips.forEach(tooltip => {
        setTooltip(tooltip.querySelector, tooltip.data)
    })

});
