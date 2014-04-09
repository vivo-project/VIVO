<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#if researchAreaResults?has_content>
    <#-- If the organization is an Academic Department, we use a different datagetter and detail page. This -->
    <#-- is strictly a usability issue so we can refer accurately to "faculty members" within the dept.     -->
    <#assign urlForDetailsPage = "affiliatedResearchAreas" />
    <#assign headingText = "${i18n().affiliated_research_areas}" />
    <#if individual.mostSpecificTypes?seq_contains("Academic Department")>
        <#assign urlForDetailsPage = "deptResearchAreas" />
        <#assign headingText = "${i18n().faculty_research_areas}" />
    </#if>
        <h2 id="facultyResearchAreas" class="mainPropGroup">
            ${headingText} 
        </h2>
        <ul id="individual-hasResearchArea" role="list">
            <#assign moreDisplayed = false>
            <#list researchAreaResults as resultRow>
		            <li class="raLink">
		            <a class="raLink"  href="${urls.base}/${urlForDetailsPage}?orgURI=${individual.uri?url}&raURI=${resultRow["ra"]?url}" title="${i18n().research_area}">
		                ${resultRow["raLabel"]}
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
