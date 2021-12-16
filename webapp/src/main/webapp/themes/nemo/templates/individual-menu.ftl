<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Menu management page (uses individual display mechanism) -->

<#include "individual-setup.ftl">

<#assign hasElement = propertyGroups.pullProperty("${namespaces.display}hasElement")!>
<#assign returnURL = "/vivo/individual?uri=http%3A%2F%2Fvitro.mannlib.cornell.edu%2Fontologies%2Fdisplay%2F1.1%23DefaultMenu&switchToDisplayModel=true" />

<#if hasElement?has_content>
    <script type="text/javascript">
        var menuItemData = [];
    </script>
    
    <h3>${i18n().menu_ordering}</h3>
    
    <#-- List the menu items -->
    <ul class="menuItems">
        <#list hasElement.statements as statement>
            <li class="menuItem"><#include "${hasElement.template}"> <span class="controls"><!--p.editingLinks "hasElement" "" statement editable /--></span></li>
        </#list>
    </ul>
    
    <#-- Link to add a new menu item -->
    <#if editable>
	    <form id="pageListForm" action="${urls.base}/editRequestDispatch" method="get">
	        <input type="hidden" name="typeOfNew" value="http://vitro.mannlib.cornell.edu/ontologies/display/1.1#Page">              
	        <input type="hidden" name="switchToDisplayModel" value="1">
	        <input type="hidden" name="editForm" value="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.ManagePageGenerator" role="input">
	   		<input type="hidden" name="addMenuItem" value="true" />
	   		<input id="submit" value="Add new menu page" role="button" type="submit" >
	   		<input type="hidden" name="returnURL" value="${returnURL?url}" />
	   	<#if verbosePropertySwitch.url?contains("pageManagement")>
	   	    <span class="or"> ${i18n().or} </span>
	   	    <a  style="margin-left:7px" href="${urls.base}/pageList" title="Return to Profile Page">Return to Page Management</a>
	   	</#if>
	    
	    </form>
	    <br />
	    <p class="note">${i18n().refresh_page_after_reordering}</p>
    </#if>
    
    ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />',
                      '<link rel="stylesheet" href="${urls.base}/css/individual/menuManagement-menuItems.css" />')}
                      
    ${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
    
    <#assign positionPredicate = "${namespaces.display}menuPosition" />
    
    <script type="text/javascript">
        // <#-- We need the controller to provide ${reorderUrl}. This is where ajax request will be sent on drag-n-drop events. -->
        var menuManagementData = {
            reorderUrl: '${reorderUrl}',
            positionPredicate: '${positionPredicate}'
        };
        var i18nStrings = {
            dragDropMenus: '${i18n().drag_drop_to_reorder_menus}',
            reorderingFailed: '${i18n().reordering_menus_failed}'
        };
    </script>
    
    ${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/menuManagement.js"></script>')}
<#else>
    <p id="error-alert">${i18n().display_has_element_error}</p>
</#if>
