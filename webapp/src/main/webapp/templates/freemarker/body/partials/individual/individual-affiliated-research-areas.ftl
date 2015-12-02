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
        <#assign numberRows = researchAreaResults?size/>
        <ul id="individual-hasResearchArea" role="list">
            <#assign totalLength = 0 >
            <#assign moreDisplayed = false>
            <#list researchAreaResults as resultRow>
                <#if ( totalLength > 380 ) && !moreDisplayed >
                    <li id="raMoreContainer" style="border:none">(...<a id="raMore" href="javascript:">more</a>)</li>
                    <li class="raLinkMore" style="display:none">
                    <#assign moreDisplayed = true>
                <#elseif ( totalLength > 380 ) && moreDisplayed >
		            <li class="raLinkMore" style="display:none">
		        <#else>
		            <li class="raLink">
		        </#if> 
		            <a class="raLink" href="${urls.base}/deptResearchAreas?orgURI=${individual.uri?replace("#","%23")}&raURI=${resultRow["ra"]}">
		                ${resultRow["raLabel"]}
		            </a>
		        </li>
		        <#assign totalLength = totalLength + resultRow["raLabel"]?length >
            </#list>
            <#if ( totalLength > 380 ) ><li id="raLessContainer" style="display:none">(<a id="raLess" href="javascript:">less</a>)</li></#if>
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
