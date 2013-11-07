<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Open Social Gadgets -->
<#-- VIVO OpenSocial Extension by UCSF -->
<#if openSocial??>
	<#if openSocial.visible>
	    <div id="openSocial">
	        <h2>OpenSocial</h2>
		    <#-- It would likely make sense to remove the #if logic as it is safe and -->
		    <#-- arguably better to just have both divs in all conditions -->
		    <#if editable>								  
        	    <div id="gadgets-edit" class="gadgets-gadget-parent"></div>
            <#else>
        	    <div id="gadgets-view" class="gadgets-gadget-parent" ></div>
            </#if>
        </div>
    </#if>	
</#if>