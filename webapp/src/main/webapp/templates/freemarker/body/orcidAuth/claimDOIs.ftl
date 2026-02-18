<style>
    .claim-for {
        float: right;
        border: 2px solid #3e8baa; /* #E0E0E0; */
        padding: 5px;
    }
    .claim-for h3 {
        text-align: center;
    }
</style>
<#if newAccount>
    <h2>Welcome ${profileName}</h2>
<#else>
    <h2>Welcome back, ${profileName}</h2>
</#if>
<br />
<#if externalIds??>
    <p>We found works for you to claim.  For each work, you will have an opportunity to indicate your role in the work.</p>
    <p><b>There are ${externalCount} work<#if externalCount != 1>s</#if> to claim.</b></p>
    <p>Click the "Claim these works" to claim the works.  Click Cancel if you do not wish to claim works at this time.</p>
    <form action="${urls.base}/createAndLink/doi" method="post">
        <input type="hidden" name="externalIds" value="${externalIds}" />
        <input type="hidden" name="action" value="findID" />
        <input type="hidden" name="profileUri" value="${profileUri!}" />
        <input type="submit" class="submit" value="Claim these works" /> <a href="${profileUrl(profileUri)}">Cancel</a>
    </form>
<#else>
    <#if newAccount>
        <p>We can automatically populate your account with any public works in your ORCiD profile that have a DOI.</p>
        <p>We did not find any works to import at this time.</p>
        <p>If you have works that you would like to have imported, please ensure that they have DOIs, and are set to public. Then log in again.</p>
        <p>You can also claim additional works by DOI or PubMed ID at any time by using the claim buttons on your profile.</p>
    </#if>
    <a href="${profileUrl(profileUri)}">Go To My Profile</a>
</#if>
