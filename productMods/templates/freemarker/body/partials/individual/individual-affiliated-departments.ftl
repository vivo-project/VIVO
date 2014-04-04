<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#if departmentsResults?has_content>
    <#-- is strictly a usability issue so we can refer accurately to "faculty members" within the dept.     -->
    <#assign urlForDetailsPage = "affiliatedDepartments" />
    <#assign headingText = "${i18n().affiliated_departments}" />
        <h2 id="facultyResearchAreas" class="mainPropGroup">
            ${headingText} 
        </h2>
        <ul id="individual-hasResearchArea" role="list">
            <#assign moreDisplayed = false>
            <#list departmentsResults as resultRow>
		            <li class="raLink">
		            <a class="raLink"  href="${urls.base}/${urlForDetailsPage}?orgURI=${resultRow["dept"]?url}&raURI=${individual.uri?url}" title="${i18n().organization}">
		                ${resultRow["deptLabel"]}
		            </a>
		        </li>
            </#list>
        </ul>    
</#if>
<script>
$('a#raMore').click(function() {
    $('li.raLinkMore').each(function() {
        $(this).show();
    });
    $('li#raMoreContainer').hide();
    $('li#raLessContainer').show();
});
$('a#raLess').click(function() {
    $('li.raLinkMore').each(function() {
        $(this).hide();
    });
    $('li#raMoreContainer').show();
    $('li#raLessContainer').hide();
});
</script>
