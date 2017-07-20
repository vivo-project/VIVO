<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#if flash?has_content>
    <div id="wrapper-content" role="main" class="container">
        <#if flash?starts_with(i18n().menu_welcomestart) >
            <section  id="welcome-msg-container" role="container">
                <section  id="welcome-message" role="alert">${flash}</section>
            </section>
        <#else>
            <section id="flash-message" role="alert">
                ${flash}
            </section>
        </#if>
    </div>
</#if>
