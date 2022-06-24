<#-- $This file is distributed under the terms of the license in LICENSE$  -->
<#if deptResearchAreas?has_content>
    <section id="pageList">
        <#list deptResearchAreas as firstRow>
        <div class="tab">
            <h2>${firstRow["raLabel"]}</h2>
            <p>${i18n().faculty_with_researh_area(firstRow["orgLabel"])} <a href="${profileUrl(firstRow["ra"])}">${i18n().view_all_faculty_in_area}</a></p>
        </div>
        <#break>
        </#list>

    <section id="deptResearchAreas">
        <ul role="list" class="deptDetailsList">
            <#list deptResearchAreas as resultRow>
               	<#if !personUri?has_content || personUri != resultRow["person"]>
			        <li class="deptDetailsListItem">
			                <a href="${profileUrl(resultRow["person"])}" title="${i18n().person_name}">${resultRow["personLabel"]}</a>
			        </li>
			    </#if>
		        <#assign personUri = resultRow["person"] />
            </#list>
        </ul>

    </section>
</#if>

</section>
