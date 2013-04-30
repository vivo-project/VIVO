<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Macros used to build the statistical information on the home page -->

<#-- Get the classgroups so they can be used to qualify searches -->
<#macro allClassGroupNames classGroups>
    <#list classGroups as group>
        <#-- Only display populated class groups -->
        <#if (group.individualCount > 0)>
            <li role="listitem"><a href="" title="${group.uri}">${group.displayName?capitalize}</a></li>
        </#if>
    </#list>
</#macro>

<#-- We need the faculty count in order to randomly select 4 faculty using a solr query -->
<#macro facultyMemberCount classGroups>
    <#assign foundClassGroup = false />
    <#list classGroups as group>
        <#if (group.individualCount > 0) && group.displayName == "people" >
            <#list group.classes as class>
                <#if (class.name == "Faculty Member") >
                    <#assign foundClassGroup = true />
                    <#if (class.individualCount > 0) >
                        <script>var facultyMemberCount = ${class.individualCount?string?replace(",","")};</script>
                    <#else>
                        <script>var facultyMemberCount = 0;</script>
                    </#if>
                </#if>
            </#list>
        </#if>
     </#list>
     <#if !foundClassGroup>
        <script>var facultyMemberCount = 0;</script>
    </#if>
</#macro>

<#-- builds the "stats" section of the home page, i.e., class group counts -->
<#macro allClassGroups classGroups>
    <#-- Loop through classGroups first so we can account for situations when all class groups are empty -->
    <#assign selected = 'class="selected" ' />
    <#assign classGroupList>
        <#list classGroups as group>
            <#-- Only display populated class groups -->
            <#if (group.individualCount > 0)>
                <#-- Catch the first populated class group. Will be used later as the default selected class group -->
                <#if !firstPopulatedClassGroup??>
                    <#assign firstPopulatedClassGroup = group />
                </#if>
                <#-- Determine the active (selected) group -->
                <#assign activeGroup = "" />
                <#if !classGroup??>
                    <#if group_index == 0>
                        <#assign activeGroup = selected />
                    </#if>
                <#elseif classGroup.uri == group.uri>
                    <#assign activeGroup = selected />
                </#if>
                <#if group.displayName != "equipment" && group.displayName != "locations" && group.displayName != "courses" >
                    <li>
                        <a href="#">
                            <p  class="stats-count">
                                <#if (group.individualCount > 10000) >
                                    <#assign overTen = group.individualCount/1000>
                                    ${overTen?round}<span>k</span>
                                <#elseif (group.individualCount > 1000)>
                                    <#assign underTen = group.individualCount/1000>
                                    ${underTen?string("0.#")}<span>k</span>
                                <#else>
                                    ${group.individualCount}<span>&nbsp;</span>
                                </#if>
                            </p>
                            <p class="stats-type">${group.displayName?capitalize}</p>
                        </a>
                    </li>
                </#if>
            </#if>
        </#list>
    </#assign>

    <#-- Display the class group browse only if we have at least one populated class group -->
    <#if firstPopulatedClassGroup??>
            ${classGroupList}
    <#else>
        <h3>There is currently no content in the system, or you need to create class groups and assign your classes to them.</h3>
        
        <#if user.loggedIn>
            <#if user.hasSiteAdminAccess>
                <p>You can <a href="${urls.siteAdmin}" title="Manage content">add content and manage this site</a> from the Site Administration page.</p>
            </#if>
        <#else>
            <p>Please <a href="${urls.login}" title="log in to manage this site">log in</a> to manage content.</p>
        </#if>
    </#if>
            
</#macro>

<#-- builds the "research" box on the home page -->
<#macro researchClasses classGroups=vClassGroups>
    <#assign foundClassGroup = false />
    <#list classGroups as group>
        <#if (group.individualCount > 0) && group.displayName == "research" >
            <#assign foundClassGroup = true />
            <#list group.classes as class>
                <#if (class.name == "Academic Article" || class.name == "Book" || class.name == "Conference Paper" ||class.name == "Media Contribution" || class.name == "Report" || class.name == "Library Collection") && (class.individualCount > 0)>
                    <li role="listitem"><span>${class.individualCount!}</span>&nbsp;<a href='${urls.base}/individuallist?vclassId=${class.uri?replace("#","%23")!}'>${class.name}s</a></li>
                </#if>
            </#list>
            <li><a href="${urls.base}/research" alt="view all research">View all ...</a></li>
        </#if>
     </#list>
     <#if !foundClassGroup>
        <p><li>No research records found.</li></p> 
     </#if>
</#macro>

<#-- builds the "academic departments" box on the home page -->
<#macro academicDepartments>
<script>
var academicDepartments = [
<#if academicDeptDG?has_content>
    <#list academicDeptDG as resultRow>
        <#assign uri = resultRow["deptURI"] />
        <#assign label = resultRow["name"] />
        <#assign localName = uri?substring(uri?last_index_of("/")) />
            {"uri": "${localName}", "name": "${label}"}<#if (resultRow_has_next)>,</#if>
    </#list>        
</#if>
];
var urlsBase = "${urls.base}";
</script>
</#macro>
