<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#if membershipResults?has_content>
        <h2 id="facultyMemberships" class="mainPropGroup">${i18n().faculty_memberships}</h2>
        <#assign numberRows = membershipResults?size/>
        <ul id="individual-facultyMemberships" role="list">
            <#list membershipResults as resultRow>
		        <li class="raLink">
		            <a class="raLink" href="${urls.base}/deptGradFields?deptURI=${individual.uri}&actURI=${resultRow["activity"]}" title="${i18n().activity_name}">
		                ${resultRow["actLabel"]}
		            </a>
		        </li>
            </#list>
        </ul>
</#if>
