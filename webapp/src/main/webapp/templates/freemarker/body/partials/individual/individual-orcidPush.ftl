<#if orcidInfo??>

    <form action="${urls.base}/orcid/allowPush" method="get">
            <input type="hidden" name="profileUri" value="${individual.uri}" />
            <input type="submit" class="submit" value="${i18n().allow_orcid_push}" />
    </form>

</#if>