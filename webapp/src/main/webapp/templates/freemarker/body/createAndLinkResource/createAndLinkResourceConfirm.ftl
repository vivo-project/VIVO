<#setting number_format="computer">
<form id="createAndLink" method="post">
    <#if personLabel??>
        <div class="claim-for">
            <h3>${i18n().create_and_link_claim_for(personLabel)}</h3>
            <#if personThumbUrl??>
                <img src="${urls.base}${personThumbUrl}" />
            </#if>
        </div>
    </#if>
    <h2>${i18n().create_and_link_confirm_works}</h2>
    ${i18n().create_and_link_confirm_works_intro}<br /><br />
    <h4>${i18n().create_and_link_authors}</h4>
    <div class="description">${i18n().create_and_link_authors_desc}</div><br />
    <h4>${i18n().create_and_link_editors}</h4>
    <div class="description">${i18n().create_and_link_editors_desc}</div><br /><br />
    ${i18n().create_and_link_not_mine_desc}<br /><br />
    <#list citations as citation>
        <div class="entryId">
            <#if citation.externalProvider??>
                ${citation.externalProvider?upper_case}: ${citation.externalId?html}
            <#else>
                ID: ${citation.externalId?html}
            </#if>
        </div>
        <#if citation.type?has_content>
            <select name="type${citation.externalId}">
                <#list publicationTypes as publicationType>
                    <option value="${publicationType.uri}" <#if publicationType.uri == citation.typeUri>selected</#if>>${publicationType.label}</option>
                </#list>
            </select><br/>
        </#if>
        <div class="entry">
            <#if citation.showError>
                <div class="citation_error">
                    ${i18n().create_and_link_error}
                </div>
            <#else>
                <!-- Output Citation -->
                <#if citation.alreadyClaimed>
                    <div class="citation_claimed">
                </#if>
                <input type="hidden" name="externalId" value="${citation.externalId!}" />
                <div class="citation">
                    <#assign proposedAuthor=false />
                    <#if citation.title??><span class="citation_title">${citation.title?html}</span><br /></#if>
                    <#assign formatted_citation>
                        <#if citation.journal??><span class="citation_journal">${citation.journal?html}</span></#if>
                        <#if citation.publicationYear??><span class="citation_year">${citation.publicationYear!?html};</span></#if>
                        <#if citation.volume??><span class="citation_volume">${citation.volume!?html}</#if>
                        <#if citation.issue??><span class="citation_issue">(${citation.issue!?html})</#if>
                        <#if citation.pagination??><span class="citation_pages">:${citation.pagination!?html}</#if>
                    </#assign>
                    <#if formatted_citation??>
                        ${formatted_citation}<br />
                    </#if>
                    <#if citation.authors??>
                        <#list citation.authors as author>
                            <#if author??>
                                <span class="citation_author">
                                    <#if citation.alreadyClaimed>
                                        <span>${author.name!?html}</span>
                                    <#else>
                                        <#if author.name??>
                                            <#if !author.linked>
                                                <input type="radio" id="author${citation.externalId}-${author?counter}" name="contributor${citation.externalId}" value="author${author?counter}" <#if author.proposed>checked</#if> class="radioWithLabel" />
                                                <label for="author${citation.externalId}-${author?counter}" class="labelForRadio">${author.name!?html}</label>
                                                <#if author.proposed><#assign proposedAuthor=true /></#if>
                                            <#else>
                                                <span class="linked">${author.name!?html}</span>
                                            </#if>
                                        </#if>
                                    </#if>
                                </span>
                            </#if>
                            <#sep>; </#sep>
                        </#list><br />
                    </#if>
                </div>

                <#if citation.alreadyClaimed>
                    <span class="claimed">${i18n().create_and_link_already_claimed}</span>
                <#else>
                    <input type="radio" id="author${citation.externalId}" name="contributor${citation.externalId}" value="author" <#if !proposedAuthor>checked</#if> class="radioWithLabel" /><label for="author${citation.externalId}" class="labelForRadio"> ${i18n().create_and_link_unlisted_author}</label><br />
                    <input type="radio" id="editor${citation.externalId}" name="contributor${citation.externalId}" value="editor" class="radioWithLabel" /><label for="editor${citation.externalId}" class="labelForRadio"> ${i18n().create_and_link_editor}</label><br />
                    <input type="radio" id="notmine${citation.externalId}" name="contributor${citation.externalId}" value="notmine" class="radioWithLabel" /><label for="notmine${citation.externalId}" class="labelForRadio"> ${i18n().create_and_link_not_mine}</label><br />
                </#if>
                <input type="hidden" name="externalResource${citation.externalId}" value="${citation.externalResource!?html}" />
                <input type="hidden" name="externalProvider${citation.externalId}" value="${citation.externalProvider!?html}" />
                <input type="hidden" name="vivoUri${citation.externalId}" value="${citation.vivoUri!?html}" />
                <input type="hidden" name="profileUri" value="${profileUri!}" />
                <#if citation.alreadyClaimed>
                    </div>
                </#if>
            </#if>
            <div style="clear: both;"></div>
        </div>
        <br/>
        <!-- End Citation -->
    </#list>
    <#if remainderIds??>
        <input type="hidden" name="remainderIds" value="${remainderIds}" />
    </#if>
    <div class="buttons">
        <input type="hidden" name="action" value="confirmID" />
        <input type="submit" value="${i18n().create_and_link_submit_confirm}" class="submit" />
        <#if remainderCount??>
            <span class="remainder">${i18n().create_and_link_remaining(remainderCount)}</span>
        </#if>
    </div>
</form>
