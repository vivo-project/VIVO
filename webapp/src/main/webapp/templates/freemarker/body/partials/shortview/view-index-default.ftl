<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Display of an individual in a list (on /individuallist and menu pages). -->

<#import "lib-vivo-properties.ftl" as p>

<a href="${individual.profileUrl}" title="${i18n().individual_name}">${individual.name}</a>

<@p.displayTitle individual />

<#-- add display of web pages? -->