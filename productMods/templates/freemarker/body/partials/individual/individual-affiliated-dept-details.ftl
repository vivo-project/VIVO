<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#if deptResearchAreas?has_content>
    <section id="pageList">
        <#list deptResearchAreas as firstRow>
        <#assign raLink = "${urls.base}/individual?uri=${firstRow['ra']}" />
        <div class="tab">
            <h2>${firstRow["orgLabel"]}</h2>
            <p>${i18n().individuals_with_dept(firstRow['raLabel'],raLink)} <a  href="${urls.base}/individual?uri=${firstRow["org"]}">${i18n().view_all_individuals_in_dept}</a></p>
        </div>
        <#break>
        </#list>
    </section>

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

