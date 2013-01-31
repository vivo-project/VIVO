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
    <#if statement.url?has_content>
        ${statement.url?replace(":","")?replace("/","")?replace(".","-")?replace("&amp;","")?replace("%","")?replace("?","")?replace("=","")}<#t>
    <#else>
        "noUrl"<#t>
    </#if>    
</#assign>


<@showWebpage statement count identifier/>

<#macro showWebpage statement count identifier>
<#local linkText>
    <#if statement.anchor?has_content>${statement.anchor}<#t>
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
      
<span id="span-${identifier}" class="webpage-indicator-qv">Loading website image. . .&nbsp;&nbsp;&nbsp;<img  src="${urls.images}/indicatorWhite.gif"></span>
        <a title="Click to view the ${linkText} web page" href="${statement.url}">
            <img id="img-${identifier}" class="org-webThumbnail" src="http://your.web.service/getsTheImage?url=${statement.url}${imgSize}" alt="screenshot of webpage ${statement.url}" style="display:none"/>

        </a>
        <#if imgSize == "" >
            </li>
            <li class="weblinkLarge">  
            <a title="Click to view the ${linkText} web page" href="${statement.url}">
                <img id="icon-${identifier}" src="${urls.images}/individual/weblinkIconLarge.png"  alt="click webpage icon" style="display:none"/>  
            </a>
        <#else>
            </li>
            <li class="weblinkSmall">  
            <a title="Click to view the ${linkText} web page" href="${statement.url}">
                <img id="icon-${identifier}" src="${urls.images}/individual/weblinkIconSmall.png"  alt="click webpage icon" style="display:none"/>  
            </a>
        </#if>
-->
<#-- Here is the placeholder link -->
    <a href="${statement.url}" title="link text">${linkText}</a><script>$("a[title='link text']").parent('li').css("float","none");</script>
<#else>
    <a href="${profileUrl(statement.uri("link"))}" title="link name">${statement.linkName}</a> (no url provided for link)
</#if>

</#macro>

<script>

$('img#img-${identifier}').load(function(){
    $('span#span-${identifier}').hide();
    $('img#img-${identifier}').fadeIn();
    $('img#icon-${identifier}').fadeIn();
});
</script>