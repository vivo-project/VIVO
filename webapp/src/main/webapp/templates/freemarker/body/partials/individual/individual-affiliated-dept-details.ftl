<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#if deptResearchAreas?has_content>
    <section id="pageList">
        <#list deptResearchAreas as firstRow>
        <#assign raLink = "${profileUrl(firstRow['ra'])}" />
        <div class="tab">
            <h2>${firstRow["orgLabel"]}</h2>
            <p>${i18n().individuals_with_dept(firstRow['raLabel'],raLink)} <a href="${profileUrl(firstRow["org"])}">${i18n().view_all_individuals_in_dept}</a></p>
        </div>
        <#break>
        </#list>
    </section>

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

