<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Confirmation that an account has been created. -->

<#assign subject = "${i18n().account_created_subject(siteName)}" />

<#assign html>
<html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
        <p>
            ${userAccount.firstName} ${userAccount.lastName}
        </p>
        
        <p>
            <strong>${i18n().congratulations}</strong>
        </p>
        
        <p>
            ${i18n().we_have_created_your_account(siteName,userAccount.emailAddress)}
        </p>
        
        <p>
            ${i18n().did_not_request_text}
        </p>
        
        <p>
            ${i18n().click_to_create_password}
        </p>
        
        <p>
            <a href="${passwordLink}" title="${i18n().password}">${passwordLink}</a>
        </p>
        
        <p>
            ${i18n().if_link_failed}
        </p>
        
        <p>
            ${i18n().thanks}
        </p>
    </body>
</html>
</#assign>

<#assign text>
${userAccount.firstName} ${userAccount.lastName}

${i18n().congratulations}

${i18n().we_have_created_your_account(siteName,userAccount.emailAddress)}

${i18n().did_not_request_text}
        
${i18n().paste_the_link}
        
${passwordLink}
        
${i18n().thanks}
</#assign>

<@email subject=subject html=html text=text />