<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign internalClassUri = editConfiguration.pageData.internalClassUri />
<section id="internal-class" role="region">
    <#if editConfiguration.pageData.internalClass?has_content>
        <#assign enableInternalClass = '' />
        <#assign disableClass = 'class="inline"' />
    <#else>
        <#assign enableInternalClass = '<p class="note">To enable this option, you must first select an <a href="${urls.base}/processInstitutionalInternalClass" title="institutional internal class">institutional internal class</a> for your instance</p>' />
        <#assign disableClass = 'class="disable inline" disabled="disabled"' />
    </#if>

                <input type="checkbox" ${disableClass} name="display-internalClass" value="${internalClassUri}" id="display-internalClass" <#if editConfiguration.pageData.internalClass?has_content && editConfiguration.pageData.isInternal?has_content>checked</#if> role="input" />
    <label ${disableClass} class="inline" for="display-internalClass">Only display <em> </em> within my institution</label>

    ${enableInternalClass}
</section>