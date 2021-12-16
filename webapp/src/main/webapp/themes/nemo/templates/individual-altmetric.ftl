<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Overview on individual profile page -->

<#if altmetricEnabled??>
    <#assign doi = propertyGroups.getProperty("http://purl.org/ontology/bibo/doi")!>
    <#if doi?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <#if doi.statements[0]??>
            <div class="individual-altmetric-badge">
                <div class="altmetric-embed"
                     style="float: ${altmetricDisplayTo}; padding-left: 15px; padding-right: 15px;"
                     data-badge-type="${altmetricBadgeType}"
                    <#if altmetricPopover??>data-badge-popover="${altmetricPopover}"</#if>
                    <#if altmetricDetails??>data-badge-details="${altmetricDetails}"</#if>
                    <#if altmetricHideEmpty??>data-hide-no-mentions="true"</#if>
                    data-link-target="_blank"
                    data-doi="${doi.statements[0].value}">
                </div>
            </div>
<span class="__dimensions_badge_embed__" data-doi="${doi.statements[0].value}"></span><script async src="https://badge.dimensions.ai/badge.js" charset="utf-8"></script>
        </#if>
    <#else>
        <#assign pmid = propertyGroups.getProperty("http://purl.org/ontology/bibo/pmid")!>
            <#if pmid?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
                <#if pmid.statements[0]??>
                    <div class="individual-altmetric-badge">
                        <div class="altmetric-embed"
                             style="float: ${altmetricDisplayTo}; padding-left: 15px; padding-right: 15px;"
                             data-badge-type="${altmetricBadgeType}"
                             <#if altmetricPopover??>data-badge-popover="${altmetricPopover}"</#if>
                             <#if altmetricDetails??>data-badge-details="${altmetricDetails}"</#if>
                             <#if altmetricHideEmpty??>data-hide-no-mentions="true"</#if>
                             data-link-target="_blank"
                             data-pmid="${pmid.statements[0].value}"></div>
                    </div>
<span class="__dimensions_badge_embed__" data-pmid="${pmid.statements[0].value}"></span><script async src="https://badge.dimensions.ai/badge.js" charset="utf-8"></script>
            </#if>
        </#if>
    </#if>
</#if>
