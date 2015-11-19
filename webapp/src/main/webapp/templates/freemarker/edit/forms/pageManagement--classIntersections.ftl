<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign internalClassUri = editConfiguration.pageData.internalClassUri />
<section id="internal-class" role="region">
    <#if editConfiguration.pageData.internalClass?has_content>
        <#assign enableInternalClass = '' />
        <#assign disableClass = 'class="inline"' />
    <#else>
        <#assign enableInternalClass = '<p class="note">${i18n().enable_internal_class_one} <a href="${urls.base}/processInstitutionalInternalClass" title="${i18n().internal_class}">${i18n().internal_class}</a> ${i18n().enable_internal_class_two}</p>' />
        <#assign disableClass = 'class="disable inline" disabled="disabled"' />
    </#if>

                <input type="checkbox" ${disableClass} name="display-internalClass" value="${internalClassUri}" id="display-internalClass" <#if editConfiguration.pageData.internalClass?has_content && editConfiguration.pageData.isInternal?has_content>checked</#if> role="input" />
    <label ${disableClass} class="inline" for="display-internalClass">${i18n().only_display} <em> </em> ${i18n().within_my_institution}</label>

    ${enableInternalClass}
</section>