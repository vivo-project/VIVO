<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Display of an individual in a list (/individuallist). -->

<a href="${individual.profileUrl}">${individual.name}</a>

<#if individual.preferredTitle?has_content>${individual.preferredTitle}</#if>

<#-- add display of web pages -->