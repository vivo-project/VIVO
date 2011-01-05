<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<table id='${tableID}'>
    <caption>
        ${tableCaption} <a href="${fileDownloadLink}">(.CSV File)</a>
    </caption>
    <thead>
        <tr>
            <th>
                Year
            </th>
            <th>
                ${tableActivityColumnName}
            </th>
        </tr>
    </thead>
    <tbody>

    <#list tableContent?keys as year>
        <tr>
            <td>
                ${year}
            </td>
            <td>
                ${tableContent[year]}
            </td>
        </tr>
    </#list>

    </tbody>
</table>