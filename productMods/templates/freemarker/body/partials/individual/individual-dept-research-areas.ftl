<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#if researchAreaResults?has_content>
        <h2 id="facultyResearchAreas" class="mainPropGroup">
            ${i18n().faculty_research_areas} 
        </h2>

        <#assign numberRows = researchAreaResults?size/>
        <ul id="individual-hasResearchArea" role="list">
            <#assign totalLength = 0 >
            <#assign moreDisplayed = false>
            <#list researchAreaResults as resultRow>
                <#if ( totalLength > 380 ) && !moreDisplayed >
                    <li id="raMoreContainer" style="border:none">(...<a id="raMore" href="javascript:">${i18n().display_more}</a>)</li>
                    <li class="raLinkMore" style="display:none">
                    <#assign moreDisplayed = true>
                <#elseif ( totalLength > 380 ) && moreDisplayed >
		            <li class="raLinkMore" style="display:none">
		        <#else>
		            <li class="raLink">
		        </#if> 
		            <a class="raLink" href="${urls.base}/deptResearchAreas?deptURI=${individual.uri}&raURI=${resultRow["ra"]}" title="${i18n().research_area}">
		                ${resultRow["raLabel"]}
		            </a>
		        </li>
		        <#assign totalLength = totalLength + resultRow["raLabel"]?length >
            </#list>
            <#if ( totalLength > 380 ) ><li id="raLessContainer" style="display:none">(<a id="raLess" href="javascript:">${i18n().display_less}</a>)</li></#if>
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
