<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for admin panel on individual profile page -->

<#import "lib-form.ftl" as form>

<#if individual.showAdminPanel>
    <section id="admin">
        <h3 id="adminPanel">Admin Panel</h3><a class="edit-individual" href="${individual.controlPanelUrl()}" title="edit this individual">Edit this individual</a>
        
        <section id = "verbose-mode">
        <#if verbosePropertySwitch?has_content>
            <#assign anchorId = "verbosePropertySwitch">
            <#assign currentValue = verbosePropertySwitch.currentValue?string("on", "off")>
            <#assign newValue = verbosePropertySwitch.currentValue?string("off", "on")>
            <span>Verbose property display is <b>${currentValue}</b> | </span>
            <a id="${anchorId}" class="verbose-toggle small" href="${verbosePropertySwitch.url}#${anchorId}" title="verbose control">Turn ${newValue}</a>
        </#if> 
        </section>

        <p class="uri-link">Resource URI: <a href="${individual.uri}" target="_blank" title="resource uri">${individual.uri}</a></p>
    </section>
</#if>