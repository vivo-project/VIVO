<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#import "lib-datetime.ftl" as dt>

<#if deptGrants?has_content>
<section id="pageList">
    <#list deptGrants as firstRow>
        <#assign firstDeptLabel = firstRow["deptLabel"]?upper_case />
        <#assign i18TextString = "" />
        <#if ( firstDeptLabel?index_of("THE") == 0 ) >
            <#assign i18TextString = "${i18n().active_grants_for?replace('the','')}" />
        <#else>
            <#assign i18TextString = "${i18n().active_grants_for}" />
        </#if>
        <h2>${i18TextString} ${firstRow["deptLabel"]} ${i18n().department}</h2>
        <#break>
    </#list>
<table id="table-listing" >
    <tr>
        <th>${i18n().grant_name}</th>
        <th>${i18n().close_date}</th>
    </tr>
        <#list deptGrants as resultRow>
            <tr>
		        <td><a href="${urls.base}/individual${resultRow["grant"]?substring(resultRow["grant"]?last_index_of("/"))}" title="${i18n().grant_name}">${resultRow["grantLabel"]}</a></td> 
		        <td>${dt.formatXsdDateTimeShort(resultRow["dt"], "yearMonthDayPrecision")}</td>
		    </tr>
		</#list>
</table>
<#else>
    ${i18n().no_active_grants}
</#if>

</section>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/menupage/pageList.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}

