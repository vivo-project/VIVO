<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 

    This version of individual--foaf-person.ftl is a "router" template. The original VIVO 
    version of this template now resides in the /themes/wilma/templates directory.
    
    This version of the template is used when the profile page types feature is enabled. 
    This template serves to "rout" the user to the correct template based (1) the 
    profile page type of the foaf person being displayed or (2) the targeted view that 
    the user wants to see. For example, when a user is routed to a quick view template, 
    the user has the option of displaying the full view. If the user chooses that option, 
    the targetedView variable gets set. 
    
    This template could also be used to load just the "individual--foaf-person-2column.ftl"
    without enabling profile page types.
        
 -->

<#include "individual-setup.ftl">

<#-- 
    First, check to see if profile page types are enabled. If not, get the 2 column template:
    "individual--foaf-person-2column.ftl".

    NOTE: the assumption here is that if this template is being loaded, rather than the
    individual--foaf-person.ftl template that resides in the theme directory, than the site
    administrator wants to use 2 column template by itself or with the quick view template.
-->

<#assign selectedTemplate = "individual--foaf-person-2column.ftl" >


<#if profilePageTypesEnabled >
    <#assign profilePageType = profileType >

    <#-- targetedView takes precedence over the profilePageType. -->
 
    <#if targetedView?has_content>
        <#if targetedView != "standardView">
            <#assign selectedTemplate = "individual--foaf-person-quickview.ftl" >
        </#if>
    <#elseif profilePageType == "quickView" >
        <#assign selectedTemplate = "individual--foaf-person-quickview.ftl" >
    </#if>
</#if>
<#include selectedTemplate >

${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/individualUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/propertyGroupControls.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>')}

<script type="text/javascript">
    i18n_confirmDelete = "${i18n().confirm_delete}"
</script>
              
