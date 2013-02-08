<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-datetime.ftl" as dt>

<#if deptGrants?has_content>
<section id="pageList">
    <#list deptGrants as firstRow>
        <h2>Active Grants for ${firstRow["deptLabel"]}</h2>
        <#break>
    </#list>
<table id="pageList" >
    <tr>
        <th>Grant Name</th>
        <th>Close Date</th>
    </tr>
        <#list deptGrants as resultRow>
            <tr>
		        <td><a href="${urls.base}/individual${resultRow["activity"]?substring(resultRow["activity"]?last_index_of("/"))}">${resultRow["activityLabel"]}</a></td> 
		        <td>${dt.formatXsdDateTimeShort(resultRow["dt"], "yearMonthDayPrecision")}</td>
		    </tr>
		</#list>
</table>
<#else>
    There are currently no active grants for this department.
</#if>

</section>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/menupage/pageList.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}

