<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showGlobalCitationCount statement />

<#macro showGlobalCitationCount statement>
    
<#local citationCount>
        <#-- hasGlobalCountDate -->
        <div class="count-date">
            <#if statement.countDate?has_content>
                <em>${statement.countDate!}</em>
            </#if>
        </div>

        <#-- hasGlobalCountValue -->
        <div class="count-value">
            <#if statement.countValue?has_content>
                <em>${statement.countValue!}</em>
            </#if>
        </div>

        <#-- hasGlobalCountSource -->  
        <div class="count-source">
            <#if statement.countSource?has_content>
                <a href="${profileUrl(statement.uri("countSource"))}">${statement.countSourceName!}</a>
            </#if>
        </div>
    </#local>

    ${citationCount}
</#macro>
