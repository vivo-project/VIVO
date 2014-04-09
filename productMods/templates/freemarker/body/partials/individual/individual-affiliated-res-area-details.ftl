<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#if affiliatedResearchAreas?has_content>
    <section id="pageList">
        <#list affiliatedResearchAreas as firstRow>
        <#assign firstOrgLabel = firstRow["orgLabel"]?upper_case />
        <#assign deptLink = "${urls.base}/individual?uri=${firstRow['org']}" />
        <#assign i18TextString1 = "" />
        <#if ( firstOrgLabel?index_of("THE") == 0 ) >
            <#assign i18TextString1 = "${i18n().individuals_with_researh_area_one(firstRow['orgLabel'],deptLink)}" />
        <#else>
            <#assign i18TextString1 = "${i18n().individuals_with_researh_area_two(firstRow['orgLabel'],deptLink)}" />
        </#if>
        <div class="tab">
            <h2>${firstRow["raLabel"]}</h2>
            <p>${i18TextString1} <a  href="${urls.base}/individual?uri=${firstRow["ra"]}">${i18n().view_all_individuals_in_area}</a></p>
        </div>
        <#break>
        </#list>
    </section>

    <section id="deptResearchAreas">
        <ul role="list" class="deptDetailsList">
            <#list affiliatedResearchAreas as resultRow>
		        <li class="deptDetailsListItem">
		                <a href="${urls.base}/individual${resultRow["person"]?substring(resultRow["person"]?last_index_of("/"))}" title="${i18n().person_name}">${resultRow["personLabel"]}</a>
		        </li>
            </#list>
        </ul>
    
    </section>
</#if>

