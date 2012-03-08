<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Confirmation that an account has been created. -->

<#assign subject = "Your ${siteName} account has been created." />

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
            <strong>Congratulations!</strong>
        </p>
        
        <p>
            We have created your new VIVO account associated with ${userAccount.emailAddress}.
        </p>
        
        <p>
            If you did not request this new account you can safely ignore this email. 
            This request will expire if not acted upon for 30 days.
        </p>
        
        <p>
            Click the link below to create your password for your new account using our secure server.
        </p>
        
        <p>
            <a href="${passwordLink}" title="password">${passwordLink}</a>
        </p>
        
        <p>
            If the link above doesn't work, you can copy and paste the link directly into your browser's address bar.
        </p>
        
        <p>
            Thanks!
        </p>
    </body>
</html>
</#assign>

<#assign text>
${userAccount.firstName} ${userAccount.lastName}

Congratulations!

We have created your new VIVO account associated with 
${userAccount.emailAddress}.

If you did not request this new account you can safely ignore this email. 
This request will expire if not acted upon for 30 days.
        
Paste the link below into your browser's address bar to create your password 
for your new account using our secure server.
        
${passwordLink}
        
Thanks!
</#assign>

<@email subject=subject html=html text=text />