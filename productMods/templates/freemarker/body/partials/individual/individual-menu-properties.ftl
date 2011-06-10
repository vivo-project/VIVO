<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for property listing on individual profile page -->

<#import "lib-properties.ftl" as p>
<#list propertyGroups.all as group>
   
     <#assign groupName = group.getName(nameForOtherGroup)>
    <section class="property-group" role="region">
        <nav class="scroll-up" role="navigation">
            <a href="#branding">
                <img src="${urls.images}/individual/scroll-up.gif" alt="scroll to property group menus" />
            </a>
        </nav>
        
        <#-- Display the group heading --> 
        <#if groupName?has_content>
            <h2 id="${groupName}">${groupName?capitalize}</h2>
        </#if>
        
        <#-- List the properties in the group -->
        <#list group.properties as property>
            <article class="property" role="article">
                <#-- Property display name -->
                <h3 id="${property.localName}">${property.name} <@p.addLink property editable /> <@p.verboseDisplay property /></h3>               
                <#-- List the statements for each property -->
                <ul class="property-list" role="list">
                	
                    <#-- data property -->
                    <#if property.type == "data">
                        <@p.dataPropertyList property editable />
                    <#-- object property -->
                    <#else>
                        <@p.objectProperty property editable property.template />
                    </#if>
                </ul>
            </article> <!-- end property -->
        </#list>
    </section> <!-- end property-group -->
</#list>