<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#educationalTraining -->

<#import "lib-sequence.ftl" as s>

<#assign degree>
    <#if statement.degree??>
        <@s.join [ statement.degreeAbbr!statement.degreeName, statement.majorField! ], " in " />
    </#if>
</#assign>

<#assign org><a href="${url(statement.org)}">${statement.orgName}</a></#assign>

<@s.join [ degree, org, statement.deptOrSchool!, statement.info!, statement.datetime! ] />
