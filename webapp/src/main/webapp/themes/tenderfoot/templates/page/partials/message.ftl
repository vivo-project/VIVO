<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#if flash?has_content>
    <#if flash?starts_with(i18n().menu_welcomestart) >
        <section id="welcome-msg-container" role="container">
            <section  id="welcome-message" role="alert">${flash}</section>
        </section>
    <#else>
        <section id="flash-msg-container" role="container">
            <section id="flash-message" role="alert">
                ${flash}
            </section>
        </section>
    </#if>
</#if>
