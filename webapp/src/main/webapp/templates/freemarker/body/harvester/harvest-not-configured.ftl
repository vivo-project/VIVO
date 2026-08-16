<div class="container">
    <h1 class="title-label">${i18n().harvester_not_configured_title}</h1>

    <p>${i18n().harvester_not_configured_message}</p>

    <#if missingProperties?has_content>
        <p>${i18n().harvester_missing_properties}</p>

        <ul>
            <#list missingProperties as missingProperty>
                <li><code>${missingProperty}</code></li>
            </#list>
        </ul>
    </#if>

    <p>${i18n().harvester_not_configured_instructions}</p>
</div>
