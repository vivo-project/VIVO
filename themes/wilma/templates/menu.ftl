<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#-- This is a temporary file and will be removed once we have completed the transition to freemarker -->

<header id="branding">
    <h2 class="vivo-logo"><a href="${urls.home}"><span class="displace">${siteName}</span></a></h2>
	<!-- Since we are using a graphic text for the tagline, we won't render ${siteTagline}
	<#if siteTagline?has_content>
		<em>${siteTagline}</em>
    </#if>-->
<#import "lib-list.ftl" as l>
    <nav>
      <ul id="header-nav">	
		<#if loginName??>
	        <li><span class="pictos-arrow-10">U</span> ${loginName}</li>
			<li><a href="${urls.logout}">Log out</a></li>
	        <li><a href="${urls.siteAdmin}">Site Admin</a></li>
	    <#else>
	         <li><a title="log in to manage this site" href="${urls.login}">Log in</a></li>
	    </#if>
		<#-- List of links that appear in submenus, like the header and footer. -->

        <li><a href="${urls.about}">About</a></li>
        <#if urls.contact??>
            <li><a href="${urls.contact}">Contact Us</a></li>
        </#if> 
        <li><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
      </ul>
    </nav>
    <section id="search">
        <fieldset>
           <legend>Search form</legend>
           <form id="searchForm" action="${urls.search}" name="searchForm"> 
               <#if showFlag1SearchField??>
                   <select id="search-form-modifier" name="flag1" class="form-item" >
                       <option value="nofiltering" selected="selected">entire database (${loginName})</option>
                       <option value="${portalId}">${siteTagline!}</option>
                   </select>
               <#else>
                   <input type="hidden" name="flag1" value="${portalId}" />
               </#if> 
             <div id="search-field">
               <input type="text" name="querytext" class="search-vivo" value="${querytext!}"  />
               <a class ="submit" href="javascript:document.searchForm.submit();">Search</a> </div>
                <!-- <input class ="submit" name="submit" type="submit"  value="Search" /> -->
           </form>
         </fieldset>
    </section>
  </header>
  <nav>
	    <ul id="main-nav">
	        <#list tabMenu.items as item>
	            <li>
	                <a href="${item.url}" <#if item.active> class="selected" </#if>>
	                    ${item.linkText}
	                </a>
	            </li>          
	        </#list>
	    </ul>
  </nav>