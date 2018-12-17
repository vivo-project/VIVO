<#-- $This file is distributed under the terms of the license in LICENSE$ -->

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

<#assign orcidTitle = i18n().orcid_title_add />
<#assign orcidStepHeading = i18n().orcid_step1_add />
<#if (orcidInfo.existingOrcids?size > 0) >
    <#assign orcidTitle = i18n().orcid_title_confirm />
    <#assign orcidStepHeading = i18n().orcid_step1_confirm />
</#if>
<#assign step2dimmed = (["START", "FAILED_AUTHENTICATE", "DENIED_AUTHENTICATE"]?seq_contains(orcidInfo.progress))?string("dimmed", "") />
<#assign continueAppears = (["START"]?seq_contains(orcidInfo.progress))/>
<#if orcidApiLevel == "member">
    <#assign continueAppears = (["START", "GOT_PROFILE"]?seq_contains(orcidInfo.progress))/>
</#if>

<div>

<section id="orcid-offer" role="region">
    <h2>${orcidTitle}</h2>

    <div class="step">
      <#if "START" == orcidInfo.progress>
          <h2>${orcidStepHeading}</h2>
          ${i18n().orcid_step1_description}
      <#elseif "DENIED_AUTHENTICATE" == orcidInfo.progress>
          <h2>${orcidStepHeading}</h2>
          ${i18n().orcid_step1_denied}
      <#elseif "FAILED_AUTHENTICATE" == orcidInfo.progress>
          <h2>${orcidStepHeading}</h2>
          ${i18n().orcid_step1_failed}
      <#else>
          <h2>${orcidStepHeading} <span class="completed">${i18n().orcid_step_completed}</span></h2>
          ${i18n().orcid_step1_confirmed(orcidInfo.orcid)}
          <p><a href="${orcidInfo.orcidUri}" target="_blank">${i18n().orcid_view_orcid_record}</a></p>
      </#if>
    </div>

    <#if orcidApiLevel == "member">
        <div class="step ${step2dimmed}">
        <#if "ID_ALREADY_PRESENT" == orcidInfo.progress>
            <h2>${i18n().orcid_step2_heading} <span class="completed">${i18n().orcid_step_completed}</span></h2>
            ${i18n().orcid_step2_already_present}
        <#elseif "DENIED_ID" == orcidInfo.progress>
            <h2>${i18n().orcid_step2_heading}</h2>
            ${i18n().orcid_step2_denied}
        <#elseif "FAILED_ID" == orcidInfo.progress>
            <h2>${i18n().orcid_step2_heading}</h2>
            ${i18n().orcid_step2_failed}
        <#elseif "ADDED_ID" == orcidInfo.progress>
            <h2>${i18n().orcid_step2_heading} <span class="completed">${i18n().orcid_step_completed}</span></h2>
            ${i18n().orcid_step2_added}
            <p><a href="${orcidInfo.orcidUri}" target="_blank">${i18n().orcid_view_orcid_record}</a></p>
        <#else>
            <h2>${i18n().orcid_step2_heading}</h2>
            ${i18n().orcid_step2_description}
        </#if>
        </div>
    </#if>

    <div class=links>
      <form method="GET" action="${orcidInfo.progressUrl}">
        <p>
          <#if continueAppears>
            <input type="submit" name="submit" value="<#if "START" == orcidInfo.progress>${i18n().orcid_button_step1}<#else>${i18n().orcid_button_step1}</#if>" class="submit"/>
            or 
          </#if>
          <a class="cancel" href="${orcidInfo.profilePage}">${i18n().orcid_return_to_vivo}</a>
        </p>
      </form>
    </div>
</section>

</div>
