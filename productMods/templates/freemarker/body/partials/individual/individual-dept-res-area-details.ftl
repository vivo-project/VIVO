<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#if deptResearchAreas?has_content>
    <section id="pageList">
        <#list deptResearchAreas as firstRow>
        <div class="tab">
            <h2>${firstRow["raLabel"]}</h2>
            <p>Here are the faculty in the ${firstRow["deptLabel"]} department who have an interest in this research area. <a href="${urls.base}/display${firstRow["raURI"]?substring(firstRow["raURI"]?last_index_of("/"))}">View all Cornell faculty with an interest in this area.</a></p>
        </div>
        <#break>
        </#list>

    <section id="deptResearchAreas">
        <ul role="list" class="deptDetailsList">
            <#list deptResearchAreas as resultRow>
		        <li class="deptDetailsListItem">
		                <a href="${urls.base}/individual${resultRow["person"]?substring(resultRow["person"]?last_index_of("/"))}">${resultRow["personLabel"]}</a>
		        </li>
            </#list>
        </ul>
    
    </section>
</#if>

</section>
