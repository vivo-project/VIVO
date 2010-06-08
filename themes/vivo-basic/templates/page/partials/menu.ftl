<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<div id="primaryAndOther">
    <ul id="primary">
        <#list tabMenu.items as item>
            <li>
                <a href="${item.url}" <#if item.active> class="activeTab" </#if>>
                    ${item.linkText}
                </a>
            </li>          
        </#list>
    </ul>
</div>