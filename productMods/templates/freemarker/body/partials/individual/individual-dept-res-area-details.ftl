<#-- $This file is distributed under the terms of the license in /doc/license.txt$  -->
<#if deptResearchAreas?has_content>
    <section id="pageList">
        <#list deptResearchAreas as firstRow>
        <div class="tab">
            <h2>${firstRow["raLabel"]}</h2>
            <p>${i18n().faculty_with_researh_area(firstRow["orgLabel"])} <a href="${urls.base}/individual?uri=${firstRow["ra"]}">${i18n().view_all_faculty_in_area}</a></p>
        </div>
        <#break>
        </#list>

    <section id="deptResearchAreas">
        <ul role="list" class="deptDetailsList">
            <#list deptResearchAreas as resultRow>
		        <li class="deptDetailsListItem">
		                <a href="${urls.base}/individual${resultRow["person"]?substring(resultRow["person"]?last_index_of("/"))}" title="${i18n().person_name}">${resultRow["personLabel"]}</a>
		        </li>
            </#list>
        </ul>
    
    </section>
</#if>

</section>
