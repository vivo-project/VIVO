<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for browsing individuals in class groups for menupages -->

<#import "lib-string.ftl" as str>

<section id="browse-by" role="region">
    <nav role="navigation">
        <ul id="browse-classes">
            <#list vClassGroup as vClass>
                <#------------------------------------------------------------
                Need to replace vClassCamel with full URL that allows function
                to degrade gracefully in absence of JavaScript. Something
                similar to what Brian had setup with widget-browse.ftl
                ------------------------------------------------------------->
                
                <#if excludedVClassURIs?has_content
                	&& excludedVClassURIs?seq_contains(vClass.URI)>
                	
                	<#-- Don't do anything -->
                	
                <#else>	
                
                <#assign vClassCamel = str.camelCase(vClass.name) />
                <#-- Only display vClasses with individuals -->
                <#if (vClass.entityCount > 0)>
                    <li id="${vClassCamel}"><a href="#${vClassCamel}" title="Browse all individuals in this class" data-uri="${vClass.URI}">${vClass.name} <span class="count-classes">(${vClass.entityCount})</span></a></li>
                </#if>
                
                </#if>
                
                
            </#list>
        </ul>
        <nav role="navigation">
            <#assign alphabet = ["A", "B", "C", "D", "E", "F", "G" "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"] />
            <ul id="alpha-browse-individuals">
                <li><a href="#" class="selected" data-alpha="all">All</a></li>
                <#list alphabet as letter>
                    <li><a href="#" data-alpha="${letter?lower_case}" title="Browse all individuals whose name starts with ${letter}">${letter}</a></li>
                </#list>
            </ul>
        </nav>
    </nav>
    
    <section id="individuals-in-class" role="region">
        <ul role="list">
            <#-- Will be populated dynamically via AJAX request -->
        </ul>
    </section>
</section>