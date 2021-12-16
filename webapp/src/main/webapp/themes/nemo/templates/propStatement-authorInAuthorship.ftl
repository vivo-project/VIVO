<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "selected publications". See the PropertyConfig.3 file for details. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
 
<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showAuthorship statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAuthorship statement>
<#if statement.hideThis?has_content>
<#-- This could potentially cause problems in an instance if a user has chosen to hide any publications -->
    <span class="hideThis">&nbsp;</span>
    <script type="text/javascript" >
        $('span.hideThis').parent().parent().addClass("hideThis");
        if ( $('h3#relatedBy-Authorship').attr('class').length == 0 ) {
            $('h3#relatedBy-Authorship').addClass('hiddenPubs');
        }
        $('span.hideThis').parent().remove(); 
    </script>
<#else>
    <#local citationDetails>
        <#if statement.subclass??>
            <#if statement.subclass?contains("Article")>
                <#if statement.journal??>
                    <em>${statement.journal!}</em>.&nbsp;
                    <#if statement.volume?? && statement.startPage?? && statement.endPage??>
                        ${statement.volume!}:${statement.startPage!}-${statement.endPage!}.
                    <#elseif statement.volume?? && statement.startPage??>
                        ${statement.volume!}:${statement.startPage!}.
                    <#elseif statement.volume??>
                        ${statement.volume!}.
                    <#elseif statement.startPage?? && statement.endPage??>
                        ${statement.startPage!}-${statement.endPage!}.
                    <#elseif statement.startPage??>
                        ${statement.startPage!}.
                    </#if>
                </#if>
            <#elseif statement.subclass?contains("Chapter")>
                <#if statement.journal??>
                    <em>${statement.journal!}</em>.
                <#elseif statement.appearsIn??>
                    <em>${statement.appearsIn!}</em>.
                <#elseif statement.partOf??>
                    <em>${statement.partOf!}</em>.
                </#if>
                <#if statement.editor??>
                    ${i18n().editor_abbreviated}&nbsp;${statement.editor!}.&nbsp;
                </#if>
                <#if statement.locale?? && statement.publisher??>
                    ${statement.locale!}:&nbsp;${statement.publisher!}.
                <#elseif statement.locale??>
                    ${statement.locale!}.
                <#elseif statement.publisher??>
                    ${statement.publisher!}.
                </#if>
                <#if statement.startPage?? && statement.endPage??>
                    ${statement.startPage!}-${statement.endPage!}.
                <#elseif statement.startPage??>
                    ${statement.startPage!}.
                </#if>
            <#elseif statement.subclass?contains("Book")>
                <#if statement.volume?? && (statement.volume!?length > 0 )>
                    ${i18n().volume_abbreviated}&nbsp;${statement.volume!}.&nbsp;
                </#if>
                <#if statement.editor??>
                    ${i18n().editor_abbreviated}&nbsp;${statement.editor!}.&nbsp;
                </#if>
                <#if statement.locale?? && statement.publisher??>
                    ${statement.locale!}:&nbsp;${statement.publisher!}.
                <#elseif statement.locale??>
                    ${statement.locale!}.
                <#elseif statement.publisher??>
                    ${statement.publisher!}.
                </#if>
            <#else>
                <#if statement.journal??>
                    <em>${statement.journal!}</em>.
                <#elseif statement.appearsIn??>
                    <em>${statement.appearsIn!}</em>.
                <#elseif statement.partOf??>
                    <em>${statement.partOf!}</em>.
                </#if>
                <#if statement.editor??>
                    ${i18n().editor_abbreviated} ${statement.editor!}.&nbsp;
                </#if>
                <#if statement.startPage?? && statement.endPage??>
                    ${statement.startPage!}-${statement.endPage!}.
                <#elseif statement.startPage??>
                    ${statement.startPage!}.
                </#if>
            </#if>
        </#if>
    </#local>

    <#local altmetric>
         <#if statement.doi??>    
                <div data-badge-popover="left" data-badge-type="donut" data-doi="${statement.doi!}" data-hide-no-mentions="true" class="altmetric-embed">   </div>
         </#if>
    </#local>
    <#local resourceTitle>
        <#if statement.infoResource??>
            <#if citationDetails?has_content>
                <a href="${profileUrl(statement.uri("infoResource"))}"  title="${i18n().resource_name}">${statement.infoResourceName}</a>.&nbsp;
            <#else>
                <a href="${profileUrl(statement.uri("infoResource"))}"  title="${i18n().resource_name}">${statement.infoResourceName}</a>
            </#if>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("authorship"))}" title="${i18n().missing_info_resource}">${i18n().missing_info_resource}</a>
        </#if>
    </#local>

     <td> <@dt.yearSpan "${statement.dateTime!}" /> </td>
     <td class="citation-data"> ${resourceTitle} ${citationDetails} <@dt.yearSpan "${statement.dateTime!}" /><br/>
         <#if statement.figshareArticleId??>
             <div class="embed-responsive embed-responsive-4by3">
             <iframe src="https://widgets.figshare.com/articles/${statement.figshareArticleId}/embed?show_title=1" class="embed-responsive-item"></iframe>
             </div>
         </#if>
      </td>
     <td> ${altmetric}</td>
</#if>
</#macro>
