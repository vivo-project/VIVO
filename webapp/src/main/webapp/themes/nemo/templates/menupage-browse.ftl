<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for browsing individuals in class groups for menupages -->

<#import "lib-string.ftl" as str>
<noscript>
<p style="padding: 20px 20px 20px 20px;background-color:#f8ffb7">${i18n().browse_page_javascript_one} <a href="${urls.base}/browse" title="${i18n().index_page}">${i18n().index_page}</a> ${i18n().browse_page_javascript_two}</p>
</noscript>

<section id="noJavascriptContainer" class="hidden">
    <section id="browse-by" role="region" class="row">
        <nav id="side-filters" role="navigation">
            <div class="col-md-3">
                <ul id="browse-classes">
                    <#list vClassGroup?sort_by("displayRank") as vClass>
                        <#------------------------------------------------------------
                        Need to replace vClassCamel with full URL that allows function
                        to degrade gracefully in absence of JavaScript. Something
                        similar to what Brian had setup with widget-browse.ftl
                        ------------------------------------------------------------->
                        <#assign vClassCamel = str.camelCase(vClass.name) />
                        <#-- Only display vClasses with individuals -->
                        <#if (vClass.entityCount > 0)>
                            <li id="${vClassCamel}"><a href="#${vClassCamel}" title="${i18n().browse_all_in_class}" data-uri="${vClass.URI}">${vClass.name} <span class="count-classes">(${vClass.entityCount})</span></a></li>
                        </#if>
                    </#list>
                </ul>
            </div>
        </nav>
        <div class="col-md-9">
            <nav id="alpha-browse-container" class="panel panel-default" role="navigation">
                <div class="panel-heading">   
                    <h3 class="selected-class"></h3>
                    <#assign alphabet = ["A", "B", "C", "D", "E", "F", "G" "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"] />
                    <ul id="alpha-browse-individuals">
                        <li>
                            <a href="#" class="selected" data-alpha="all" title="${i18n().select_all}">${i18n().all}</a>
                        </li>
                        <#list alphabet as letter>
                            <li><a href="#" data-alpha="${letter?lower_case}" title="${i18n().browse_all_starts_with(letter)}">${letter}</a></li>
                        </#list>
                    </ul>
                </div>
            </nav>
            <section id="individuals-in-class" role="region">
                <ul role="list">

                    <#-- Will be populated dynamically via AJAX request -->
                </ul>
            </section>
        </div>
    </section>
</section>
<script type="text/javascript">
    $('section#noJavascriptContainer').removeClass('hidden');
</script>