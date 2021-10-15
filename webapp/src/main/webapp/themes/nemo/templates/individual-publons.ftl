<!-- Publons article badge -->

<#assign doi = propertyGroups.getProperty("http://purl.org/ontology/bibo/doi")!>
<#if doi?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <#if doi.statements[0]??>
        <div class="publons-badge" data-type="square" data-doi="${doi.statements[0].value}" style="float: right; width: 60px; height: 60px;"></div>
        <script src="https://publons.com/static/badges-v1.js" async></script>
    </#if>
</#if>

<!-- /Publons article badge -->
