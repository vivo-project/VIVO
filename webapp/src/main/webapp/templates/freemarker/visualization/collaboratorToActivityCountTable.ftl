<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<table id='${tableID}'>
    <caption>
    ${tableCaption} <a href="${fileDownloadLink}">(.CSV ${i18n().file_capitalized})</a>
    </caption>
    <thead>
    <tr>
        <th>
        ${tableCollaboratorColumnName}
        </th>
        <th>
        ${tableActivityColumnName}
        </th>
    </tr>
    </thead>
    <tbody>

    <#list tableContent.collaborators as collaborator>
        <#if collaborator_index gt 0>
            <tr>
                <td>
                ${collaborator.collaboratorName}
                </td>
                <td>
                ${tableContent.collaborationMatrix[0][collaborator_index]}
                </td>
            </tr>
        </#if>
    </#list>

    </tbody>
</table>