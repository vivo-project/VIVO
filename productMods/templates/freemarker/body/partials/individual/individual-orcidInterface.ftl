<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
    If authorized to confirm ORCID IDs, add the function that will replace the add link.
    The OrcidIdDataGetter is attached to this template; it sets the orcidInfo structure,
    which looks like this:
    
    orcidInfo = map {
        authorizedToConfirm: boolean
        orcidUrl: link to the orcid controller
        orcids: map of String to boolean [
            orcid: String (full URI)
            confirmed: boolean
        ]
    }
-->
<#assign confirmThis = "" /> 
<#if orcidInfo??>
    
    <#list orcidInfo.orcids?keys as key>
        <#if "no" == orcidInfo.orcids[key]?string("yes","no") >
            <#assign confirmThis = "Confirm the ID" />
            </#if>
    </#list>
        
    <#if orcidInfo.authorizedToConfirm>
        <script>
            $(document).ready(function(){
                $('#orcidId a.add-orcidId').replaceWith("<a class='add-orcidId' style='padding-left:20px' href='${orcidInfo.orcidUrl}'><#if orcidInfo.orcids?size == 0>Add an iD<#else>${confirmThis}</#if></a> ");
            });
        </script>
    </#if>
</#if>
 