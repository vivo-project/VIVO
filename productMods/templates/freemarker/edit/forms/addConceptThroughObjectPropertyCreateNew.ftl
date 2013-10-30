<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#--Overriding default form here to allow just concepts to show in the list-->
	 
	 <#if rangeOptionsExist  = true >
	        <p>${i18n().no_appropriate_entry}:</p>
	 <#else>
	        <p>${i18n().create_new_entry}</p>           
	 </#if>
	 
	 <#if editConfiguration.objectUri?has_content>
	    <#assign objectUri = editConfiguration.objectUri>
	 <#else>
	    <#assign objectUri = ""/>
	 </#if>
	 
	<#assign typesList = editConfiguration.pageData.createNewTypes/>
	<form class="editForm" action="${editConfiguration.mainEditUrl}" role="input" />        
	    <input type="hidden" value="${editConfiguration.subjectUri}" name="subjectUri" role="input" />  
	    <input type="hidden" value="${editConfiguration.predicateUri}" name="predicateUri" role="input" />  
	    <input type="hidden" value="${objectUri}" name="objectUri" role="input" />      
	    <input type="hidden" value="create" name="cmd" role="input" />     
	        
	    <select id="typeOfNew" name="typeOfNew" role="selection">
	    <#assign typeKeys = typesList?keys />
	    <#list typeKeys as typeKey>
	        <option value="${typeKey}" role="option"> ${typesList[typeKey]} </option>
	    </#list>
	    </select>
	    
	    <input type="submit" id="offerCreate" class="submit"  value="${i18n().add_new_of_type}" role="button" />  
	    <#if rangeOptionsExist  = false >
	        <span class="or"> ${i18n().or} </span>
	        <a title="${i18n().cancel_title}" class="cancel" href="${cancelUrl}">${i18n().cancel_link}</a>
	    </#if>
	</form>          