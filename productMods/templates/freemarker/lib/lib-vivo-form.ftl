<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Macros for form controls -->

<#---------------------------------------------------------------------------->

<#-- 
    Macro: unsupportedBrowser
    
    Output: html notifying the user that the browser is an unsupported version
    
    Input: none.
    
    Usage: <@unsupportedBrowser />
-->
<#macro unsupportedBrowser >
<div id="ie67DisableWrapper">
    <div id="ie67DisableContent">
	    <img src="/vivo/site_icons/iconAlertBig.png" alt="Alert Icon"/>
	    <p>This form is not supported in versions of Internet Explorer below version 8. Please upgrade your browser, or
	    switch to another browser, such as FireFox.</p>
    </div>
</div>
</#macro>