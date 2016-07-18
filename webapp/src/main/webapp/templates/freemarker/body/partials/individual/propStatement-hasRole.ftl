<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for the roleRealizedIn, roleContributesTo, researchActivities, hasRole 
     and hasClinicalActivities custom list views. See those list view and the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-datetime.ftl" as dt>
<@showRole statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showRole statement>
<#if statement.hideThis?has_content>
    <span class="hideThis">&nbsp;</span>
    <script type="text/javascript" >
        $('span.hideThis').parent().parent().addClass("hideThis");
        if ( $('h3#RO_0000053-ResearcherRole').attr('class').length == 0 ) {
            $('h3#RO_0000053-ResearcherRole').addClass('hiddenGrants');
        }
        $('span.hideThis').parent().remove();
    </script>
<#else>
    <#local linkedIndividual>
        <#if statement.activity??>
            <a href="${profileUrl(statement.uri("activity"))}" title="${i18n().activity_name}">${statement.activityLabel!statement.activityName}</a>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("role"))}" title="${i18n().missing_activity}">${i18n().missing_activity}</a>
        </#if>
    </#local>
    
    <#local dateTime>
        <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
    </#local>
    
    ${linkedIndividual} ${statement.roleLabel!} ${dateTime!} 
</#if>
</#macro>