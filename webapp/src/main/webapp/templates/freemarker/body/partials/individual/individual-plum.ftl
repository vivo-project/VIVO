<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Overview on individual profile page -->

<#if plumPrintEnabled??>
    <#assign doi = propertyGroups.getProperty("http://purl.org/ontology/bibo/doi")!>
    <#if doi?has_content && doi.statements[0]??>
        <#assign plumIdParam = "doi=${doi.statements[0].value}">
    <#else>
        <#assign pmid = propertyGroups.getProperty("http://purl.org/ontology/bibo/pmid")!>
        <#if pmid?has_content && pmid.statements[0]??>
            <#assign plumIdParam = "pmid=${pmid.statements[0].value}">
        <#else>
            <#assign isbn = propertyGroups.getProperty("http://purl.org/ontology/bibo/isbn13")!>
            <#if !(isbn?has_content && isbn.statements[0]??)>
                <#assign isbn = propertyGroups.getProperty("http://purl.org/ontology/bibo/isbn10")!>
            </#if>
            <#if isbn?has_content && isbn.statements[0]??>
                <#assign plumIdParam = "isbn=${isbn.statements[0].value}">
            <#else>
                <#assign oclc = propertyGroups.getProperty("http://purl.org/ontology/bibo/oclcnum")!>
                <#if oclc?has_content && oclc.statements[0]??>
                    <#assign plumIdParam = "oclc=${oclc.statements[0].value}">
                </#if>
            </#if>
        </#if>
    </#if>
    <#if plumIdParam??>
    <div class="individual-plum-print" style="float: ${plumPrintDisplayTo}; position: relative; z-index: 1;">
        <a class="plumx-plum-print-popup"
           href="https://plu.mx/plum/a/?${plumIdParam}"
           data-popup="${plumPrintPopover}"
           data-hide-when-empty="${plumPrintHideEmpty}"
           data-site="plum" data-size="${plumPrintSize}"
           data-badge="false" data-popover="true"></a>
    </div>
    </#if>
</#if>
