<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- List of positions for the individual -->
<#assign positions = propertyGroups.pullProperty("${core}relatedBy", "${core}Position")!>
<#if positions?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <#assign localName = positions.localName>
    <h2 id="${localName}" class="mainPropGroup" title="${positions.publicDescription!}">${positions.name?capitalize} <@p.addLink positions editable /></h2>
    <@p.verboseDisplay positions />
    <ul id="individual-personInPosition" role="list">
        <@positionsList positions editable />
    </ul>
</#if>

<#macro positionsList property editable statements=property.statements template=property.template>
    <#list statements as statement>
        <#if property.rangeUri?? >
            <#local rangeUri = property.rangeUri />
        <#else>
            <#local rangeUri = "" />
        </#if>
        <#if statement.dateTimeEnd??>
        <#else>
            <li role="listitem" class="currentPosition">
                <#include "${template}">
                <@p.editingLinks "${property.localName}" "${property.name}" statement editable rangeUri/>
            </li>
        </#if>
    </#list>
    <#list statements as statement>
        <#if property.rangeUri?? >
            <#local rangeUri = property.rangeUri />
        <#else>
            <#local rangeUri = "" />
        </#if>
        <#if statement.dateTimeEnd??>
            <li role="listitem" class="pastPosition">
                <#include "${template}">
                <@p.editingLinks "${property.localName}" "${property.name}" statement editable rangeUri/>
            </li>
        </#if>
    </#list>
</#macro>

<script language="JavaScript">
    $('ul#individual-personInPosition').each(function(){

        var LiN = $(this).find('li').length;

        if( LiN > 1) {
            $('li', this).eq(0).nextAll().hide().addClass('toggleable');
            $(this).append('<li class="more">More...</li>');
        }

    });


    $('ul#individual-personInPosition').on('click','.more', function(){
        if( $(this).hasClass('less') ){
            $(this).text('More...').removeClass('less');
        }else{
            $(this).text('Less...').addClass('less');
        }

        $(this).siblings('li.toggleable').slideToggle();

    });
</script>
