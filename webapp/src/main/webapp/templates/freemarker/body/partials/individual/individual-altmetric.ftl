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
            </#if>
        <#else>
            <#assign isbn = propertyGroups.getProperty("http://purl.org/ontology/bibo/isbn13")!>
            <#if isbn?has_content>
            <#else>
                <#assign isbn = propertyGroups.getProperty("http://purl.org/ontology/bibo/isbn10")!>
            </#if>
            <#if isbn?has_content>
                <#if isbn.statements[0]??>
                <div class="individual-altmetric-badge">
                    <div class="altmetric-embed"
                         style="float: ${altmetricDisplayTo}; padding-left: 15px; padding-right: 15px;"
                         data-badge-type="${altmetricBadgeType}"
                         <#if altmetricPopover??>data-badge-popover="${altmetricPopover}"</#if>
                         <#if altmetricDetails??>data-badge-details="${altmetricDetails}"</#if>
                         <#if altmetricHideEmpty??>data-hide-no-mentions="true"</#if>
                         data-link-target="_blank"
                         data-isbn="${isbn.statements[0].value}"></div>
                </div>
                </#if>
            </#if>
        </#if>
    </#if>
</#if>
