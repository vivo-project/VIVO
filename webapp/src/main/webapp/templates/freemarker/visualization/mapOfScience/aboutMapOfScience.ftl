<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#assign aboutImagesRoot = '${urls.images}/visualization/mapofscience/about/'>

<h2>${i18n().about_map_of_science_heading}</h2>
<h3>${i18n().reference_basemap_heading}</h3>
<p>${i18n().reference_basemap_description}</p>

<h3>${i18n().data_overlay_heading}</h3>
<p>${i18n().data_overlay_description}</p>

<img src="${aboutImagesRoot}/scimap_discipline.jpg" width="450" height="327" />
<img src="${aboutImagesRoot}/scimap_subdiscipline.jpg" width="450" height="327" />

<h3>${i18n().expertise_profile_comparision_map_heading}</h3>
<p>${i18n().expertise_profile_comparision_map_description}</p>

<img src="${aboutImagesRoot}/scimap_comparison.jpg" width="803" height="781" style=
"margin-left: 50px;"/>

<h3>${i18n().interactivity_heading}</h3>
<p>${i18n().interactivity_description}</p>

<h3>${i18n().links_heading}</h3>
<p>
    ${i18n().links_description_the_first_part}
    <a href="https://doi.org/10.1371/journal.pone.0039464" target="_blank">https://doi.org/10.1371/journal.pone.0039464</a>.
    ${i18n().links_description_the_introduction_part}
    <a href="http://scimaps.org" target="_blank">http://scimaps.org</a> ${i18n().and}
    <a href="http://mapofscience.com" target="_blank">http://mapofscience.com</a>.
</p>
