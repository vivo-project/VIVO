<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- "Quick view" template for core:webpage 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
-->

<#--  *** Use of the quick view template requires that a web service provide snap shots of the web sites that  ***
      *** are defined on a foaf:Person individual's profile page.  The web service can be one that you develop ***
      *** or implement, or it can be a paid service.  The service is called in the src attribute of the image  ***
      *** element that contains the web page snap shot, as shown below in the template. Note that this code is ***
      *** currently commented out and a placeholder is displayed instead.                                      ***
-->
<#assign count = property.statements?size!> 

<#assign identifier>
    <#if statement.url?has_content>${statement.url?replace("[^\\p{L}\\p{N}]","","r")}<#t>
    <#else>
        "noUrl"<#t>
    </#if>    
</#assign>


<@showWebpage statement count identifier/>

<#macro showWebpage statement count identifier>
<#local linkText>
    <#if statement.label?has_content>${statement.label}<#t>
    <#elseif statement.url?has_content>${statement.url}<#t>
    </#if>    
</#local>
<#local imgSize = "&thumbnail=true" >

<#if (statement.rank?? && statement.rank == "1") || ( count == 1 ) >
     <#local imgSize = "" >
</#if>
<#if statement.url?has_content>

<#-- This section commented out until the web service for the web page snapshot is implemented. 
      The assumption is made that the service will require the url of the web page and possibly
      an image size as well.
      
<span id="span-${identifier}" class="webpage-indicator-qv">${strings.loading_website_image}. . .&nbsp;&nbsp;&nbsp;<img  src="${urls.images}/indicatorWhite.gif"></span>
        <a title="${i18n().click_to_view_web_page(linkText)}" href="${statement.url}">
            <img id="img-${identifier}" class="org-webThumbnail" src="http://your.web.service/getsTheImage?url=${statement.url}${imgSize}" alt="${i18n().screenshot_of_webpage(statement.url)}" style="display:none"/>

        </a>
        <#if imgSize == "" >
            </li>
            <li class="weblinkLarge">  
            <a title="${i18n().click_to_view_web_page(linkText)}" href="${statement.url}">
                <img id="icon-${identifier}" src="${urls.images}/individual/weblinkIconLarge.png"  alt="${i18n().click_webpage_icon}" style="display:none"/>  
            </a>
        <#else>
            </li>
            <li class="weblinkSmall">  
            <a title="${i18n().click_to_view_web_page(linkText)}" href="${statement.url}">
                <img id="icon-${identifier}" src="${urls.images}/individual/weblinkIconSmall.png"  alt="${i18n().click_webpage_icon}" style="display:none"/>  
            </a>
        </#if>
-->
<#-- Here is the placeholder link -->
    <a href="${statement.url}" title="${i18n().link_text}">${linkText}</a><script>$("a[title='${i18n().link_text}']").parent('li').css("float","none");</script>
<#else>
    <a href="${profileUrl(statement.uri("link"))}" title="${i18n().link_name}">${statement.linkName}</a> (${i18n().no_url_provided})
</#if>

</#macro>

<script>

$('img#img-${identifier}').load(function(){
    $('span#span-${identifier}').hide();
    $('img#img-${identifier}').fadeIn();
    $('img#icon-${identifier}').fadeIn();
});
</script>