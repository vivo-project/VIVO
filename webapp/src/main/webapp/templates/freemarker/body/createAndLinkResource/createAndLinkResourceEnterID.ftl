<form id="createAndLink" method="post">
    <#if personLabel??>
        <div class="claim-for">
            <h3>${i18n().create_and_link_claim_for(personLabel)}</h3>
            <#if personThumbUrl??>
                <img src="${urls.base}${personThumbUrl}" />
            </#if>
        </div>
    </#if>
    <#if showConfirmation??>
        <h2>${i18n().create_and_link_thank_you}</h2>
        ${i18n().create_and_link_finished}<br /><br />
        <#if profileUri??>
            <a href="${profileUrl(profileUri)}">${i18n().create_and_link_go_profile}</a><br /><br />
        </#if>
    </#if>
    <h2>${i18n().create_and_link_enter(label)}</h2>
    <#switch provider>
        <#case "doi">
            ${i18n().create_and_link_enter_dois_intro}<br />
            <i>ID</i>:  10.1038/nature01234<br />
            <i>URL</i>: https://doi.org/10.1038/nature01234<br />
            <br />
            ${i18n().create_and_link_enter_dois_supported}<br /><br />
        <#break>
        <#case "pmid">
            ${i18n().create_and_link_enter_pmid_intro}<br /><br />
            ${i18n().create_and_link_enter_pmid_supported}<br /><br />
        <#break>
    </#switch>
    <textarea name="externalIds" rows="15" cols="50"></textarea><br />
    <input type="submit" value="${i18n().create_and_link_submit_ids}" class="submit" /><br />
    <input type="hidden" name="action" value="findID" />
    <input type="hidden" name="profileUri" value="${profileUri!}" />
</form>
