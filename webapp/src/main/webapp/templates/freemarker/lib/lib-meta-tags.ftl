<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#macro addCitationMetaTag uri="" content="">
    <#if metaTags?? && uri?? && content??>
        <#switch uri>
            <#case "http://vivoweb.org/ontology/core#Authorship">
                ${metaTags.add("<meta tag=\"citation_author\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://vivoweb.org/ontology/core#dateTimeValue">
                ${metaTags.add("<meta tag=\"citation_date\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/Journal">
                ${metaTags.add("<meta tag=\"citation_journal_title\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/volume">
                ${metaTags.add("<meta tag=\"citation_volume\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/issue">
                ${metaTags.add("<meta tag=\"citation_issue\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/pageStart">
                ${metaTags.add("<meta tag=\"citation_firstpage\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/pageEnd">
                ${metaTags.add("<meta tag=\"citation_lastpage\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/doi">
                ${metaTags.add("<meta tag=\"citation_doi\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/pmid">
                ${metaTags.add("<meta tag=\"citation_pmid\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/issn">
                ${metaTags.add("<meta tag=\"citation_issn\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/isbn10">
                ${metaTags.add("<meta tag=\"citation_isbn\" content=\"" + content + "\" />")}
                <#break>
            <#case "http://purl.org/ontology/bibo/isbn13">
                ${metaTags.add("<meta tag=\"citation_isbn\" content=\"" + content + "\" />")}
                <#break>
        </#switch>
    </#if>
</#macro>

http://purl.org/ontology/bibo/volume
http://purl.org/ontology/bibo/issue
http://purl.org/ontology/bibo/pageStar