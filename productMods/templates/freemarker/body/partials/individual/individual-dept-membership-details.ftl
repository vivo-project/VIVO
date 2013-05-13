<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#if deptMemberships?has_content>
    <section id="pageList">
        <#list deptMemberships as firstRow>
        <div class="tab">
            <h2>${firstRow["orgLabel"]}</h2>
            <p>${i18n().faculty_who_are_members_of_org(firstRow["deptLabel"])} <a href="${urls.base}/display${firstRow["orgURI"]?substring(firstRow["orgURI"]?last_index_of("/"))}" title="${i18n().view_all_faculty}">${i18n().view_all_members_of_org}</a></p>
        </div>
        <#break>
        </#list>

    <section id="deptGraduateFields">
        <ul role="list"  class="deptDetailsList">
            <#list deptMemberships as resultRow>
		        <li class="deptDetailsListItem">
		                <a href="${urls.base}/individual${resultRow["person"]?substring(resultRow["person"]?last_index_of("/"))}" title="${i18n().person_name}">${resultRow["personLabel"]}</a>
		        </li>
            </#list>
        </ul>
    
    </section>

</#if>

</section>
