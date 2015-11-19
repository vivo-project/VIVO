<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Default individual search view -->

<#import "lib-vivo-properties.ftl" as p>

<a href="${individual.profileUrl}" title="${i18n().individual_name}">${individual.name}</a>

<@p.displayTitle individual />

<p class="snippet">${individual.snippet}</p>