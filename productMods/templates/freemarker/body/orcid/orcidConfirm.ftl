<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--
The body map contains the orcidInfo structure, which is set up like this:

orcidInfo
    progress       - a string set to one of these values: START, DENIED_AUTHENTICATE, 
                     FAILED_AUTHENTICATE, GOT_PROFILE, ID_ALREADY_PRESENT, DENIED_ID, 
                     FAILED_ID, ADDED_ID
    individualUri  - the URI of the person
    profilePage    - the URL of the individual's profile page
    orcid          - the confirmed ORCID (just xxxx-xxxx-xxxx-xxxx), 
                     or the empty string.
    orcidUri       - the confirmed ORCID (full URI), or the empty string.
    externalIds    - empty if we haven't read their profile. Otherwise, a sequence
                     of maps, one for each external ID in their profile. These
                     might include SCOPUS ID, etc. Each map looks like this:
                     commonName - e.g., "VIVO Cornell"
                     reference  - e.g., their VIVO localname
                     uri        - e.g., their VIVO URI
    hasVivoId      - true, if we have read the profile and they already have 
                     their VIVO URI as an external ID. False otherwise.
    existingOrcids - A sequence of the ORCIDs (full URI) that we already associate 
                     with this individual.
    progressUrl    - The URL to go to, that will continue this process. If the 
                     process is complete or has failed, this is empty.

-->

<style TYPE="text/css">
#orcid-offer .step {
    background-color: #F7F7F4;
    border: 1px solid #cdcdcd;
    border-radius: 10px;
    padding: 0 1em 1em;
    margin: 12px
} 

#orcid-offer .links {
	text-align: left;
	margin-left: 12px;
}

#orcid-offer ul {
	list-style: disc inside;
	line-height: 28px;
}

#orcid-offer ul.inner {
	list-style: none;
	padding-left: 8px;
}

#orcid-offer li {
	padding-left: 10px;
}

#orcid-offer .dimmed  {
    opacity:0.35;
    filter:alpha(opacity=35);
}
span.completed {
    color: #9a9a9a;
    font-size: .8em;
}
</style>

<#assign orcidTextOne = "add an" />
<#assign orcidTextTwo = "Adding" />
<#if (orcidInfo.existingOrcids?size > 0) >
    <#assign orcidTextOne = "confirm your" />
    <#assign orcidTextTwo = "Confirming" />
</#if>
<#assign step2dimmed = (["START", "FAILED_AUTHENTICATE", "DENIED_AUTHENTICATE"]?seq_contains(orcidInfo.progress))?string("dimmed", "") />
<#assign continueAppears = (["START", "GOT_PROFILE"]?seq_contains(orcidInfo.progress))/>

<div>

<section id="orcid-offer" role="region">
    <h2>Do you want to ${orcidTextOne} ORCID iD?</h2>
   
    <div class="step">
      <#if "START" == orcidInfo.progress>
        <h2>Step 1: ${orcidTextTwo} your ORCID iD</h2>
        <ul>
          <li>VIVO redirects you to ORCID's web site.</li>
          <li>You log in to your ORCID account.
            <ul class="inner"><li>If you don't have an account, you can create one.</li></ul>
            </li>
          <li>You tell ORCID that VIVO may read your ORCID record. (one-time permission)</li>
          <li>VIVO reads your ORCID record.</li>
          <li>VIVO notes that your ORCID iD is confirmed.</li>
        </ul>
      <#elseif "DENIED_AUTHENTICATE" == orcidInfo.progress>
        <h2>Step 1: ${orcidTextTwo} your ORCID iD</h2>
        <p>You denied VIVO's request to read your ORCID record.</p>
        <p>Confirmation can't continue.</p>
      <#elseif "FAILED_AUTHENTICATE" == orcidInfo.progress>
        <h2>Step 1: ${orcidTextTwo} your ORCID iD</h2>
        <p>VIVO failed to read your ORCID record.</p>
        <p>Confirmation can't continue.</p>
      <#else>
        <h2>Step 1: ${orcidTextTwo} your ORCID iD <span class="completed">(step completed)</span></h2>
        <p>Your ORCID iD is confirmed as ${orcidInfo.orcid}</p>
        <p><a href="${orcidInfo.orcidUri}" target="_blank">View your ORCID record.</a></p>
      </#if>
    </div>
    
    <div class="step ${step2dimmed}">
      <#if "ID_ALREADY_PRESENT" == orcidInfo.progress>
        <h2>Step 2 (recommended): Linking your ORCID record to VIVO <span class="completed">(step completed)</span></h2>
        <p>Your ORCID record already includes a link to VIVO.</p>
      <#elseif "DENIED_ID" == orcidInfo.progress>
        <h2>Step 2 (recommended): Linking your ORCID record to VIVO</h2>
        <p>You denied VIVO's request to add an External ID to your ORCID record.</p>
        <p>Linking can't continue.</p>
      <#elseif "FAILED_ID" == orcidInfo.progress>
        <h2>Step 2 (recommended): Linking your ORCID record to VIVO</h2>
        <p>VIVO failed to add an External ID to your ORCID record.</p>
        <p>Linking can't continue.</p>
      <#elseif "ADDED_ID" == orcidInfo.progress>
        <h2>Step 2 (recommended): Linking your ORCID record to VIVO <span class="completed">(step completed)</span></h2>
        <p>Your ORCID record is linked to VIVO</p>
        <p><a href="${orcidInfo.orcidUri}" target="_blank">View your ORCID record.</a></p>
      <#else>
        <h2>Step 2 (recommended): Linking your ORCID record to VIVO</h2>
        <ul>
          <li>VIVO redirects you to ORCID's web site</li>
          <li>You tell ORCID that VIVO may add an "external ID" to your ORCID record. (one-time permission)</li>
          <li>VIVO adds the external ID.</li>
        </ul>
      </#if>
    </div>
    
    <div class=links>
      <form method="GET" action="${orcidInfo.progressUrl}">
        <p>
          <#if continueAppears>
            <input type="submit" name="submit" value="Continue <#if "START" == orcidInfo.progress>Step 1<#else>Step 2</#if>" class="submit"/>
            or 
          </#if>
          <a class="cancel" href="${orcidInfo.profilePage}">Return to your VIVO profile page</a>
        </p>
      </form>
    </div>
</section>

</div>