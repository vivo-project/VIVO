<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- Icon controls displayed in upper-right corner -->

<button id="uriIcon" class="nostyle" aria-label="${i18n().qr_icon_label}">
	<img title="${individual.uri}" src="${urls.images}/individual/share-uri-icon.png" alt="${i18n().share_the_uri}" aria-hidden="true" />
</button>
<#if checkNamesResult?has_content >
    <button id="qrIcon" class="nostyle" aria-label="${i18n().uri_icon_label}">
		<img src="${urls.images}/individual/qr-code-icon.png" alt="${i18n().qr_icon}" aria-hidden="true" />
	</button>
	<span id="qrCodeImage" class="hidden">${qrCodeLinkedImage!}
		<a class="qrCloseLink" href="#"  title="${i18n().qr_code}">${i18n().close_capitalized}</a>
	</span>
</#if>


<#--

Some contact information is displayed on the profile page by default; e.g., phone numbes and
email addresses. If an institution has an additional location for contact info, such as a
university directory, a third "contact" icon is available that can be used to direct users to
that directory. The <a> tag below shows an example using Cornell University's directory.

<#assign netid = individual.selfEditingId()!>
<#if netid?has_content>
    <a href="http://www.cornell.edu/search/?tab=people&netid=${netid}" title="Cornell University directory entry for ${netid}" target="_blank">
        <img src="${urls.images}/individual/contact-info-icon.png"  title="view additional contact information" alt="contact info" />
    </a>
</#if>

-->
