<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#if deptMemberships?has_content>
    <section id="pageList">
        <#list deptMemberships as firstRow>
        <div class="tab">
            <h2>${firstRow["orgLabel"]}</h2>
            <p>Here are the faculty in the ${firstRow["deptLabel"]} department who are members of this organization. <a href="${urls.base}/display${firstRow["orgURI"]?substring(firstRow["orgURI"]?last_index_of("/"))}" title="view all cornell faculty">View all the members of this organization.</a></p>
        </div>
        <#break>
        </#list>

    <section id="deptGraduateFields">
        <ul role="list"  class="deptDetailsList">
            <#list deptMemberships as resultRow>
		        <li class="deptDetailsListItem">
		                <a href="${urls.base}/individual${resultRow["person"]?substring(resultRow["person"]?last_index_of("/"))}">${resultRow["personLabel"]}</a>
		        </li>
            </#list>
        </ul>
    
    </section>

</#if>

</section>
