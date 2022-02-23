<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

</header>

<#include "developer.ftl">

<nav class="navbar navbar-default navbar-static-top navbar-inverse" id="sticky-nav" role="navigation">
	<div class="container">

	<#-- This has been commented out as we have implemented the system name in identity.ftl. -->
		<#-- 
		<div class="navbar-header">
			<button 
				type="button" 
				class="navbar-toggle collapsed" 
				data-toggle="collapse" 
				data-target="#sticky" 
				aria-expanded="false" 
				aria-controls="navbar"
			>
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="${urls.home}">${siteName}</a>
		</div> 
		-->

		<form 
			class="navbar-form navbar-left" 
			role="search" 
			action="${urls.search}" 
			method="post"
		>
			<div class="form-group">
				<input 
					type="text" 
					class="form-control" 
					name="querytext" 
					placeholder="${i18n().search_form}" 
					autocapitalize="off" 
					value="" 
				>				
				<#-- Search button for search form in navbar -->
				<button type="submit" class="btn btn-default sympl-search hidden-xs">
					<span class="glyphicon glyphicon-search" role="submit"></span>
				</button>
			
				<#-- Button for submitting search has been repeated with btn-block and hidden on lg and md devices to fix mobile support -->
				<button type="submit" class="btn btn-default btn-block sympl-search hidden-sm hidden-lg hidden-md">
					<span class="glyphicon glyphicon-search" role="submit"></span>
				</button>				
			</div>
		</form>		
						
		<#-- From Symplectic's Version of Template Commented Out for Generic Template -->
		<#--		
		<form 
			class="navbar-form navbar-left" 
			role="search" 
			action="/s/search.html?collection=vivo-lilliput&form=lilliputsimple&profile=_default_preview" 
			method="get"
		>
			<div class="form-group">
				<input 
					type="text" 
					class="form-control" 
					name="query" 
					placeholder="Find an expert..." 
				>
			</div>
            <input type="hidden" name="collection" value="vivo-lilliput" />
            <input type="hidden" name="form" value="lilliputsimple" />
        -->    
			<#-- Search button for search form in navbar -->
		<#--
			<button type="submit" class="btn btn-default sympl-search hidden-xs">
				<span class="glyphicon glyphicon-search" role="submit"></span>
			</button>
		-->
			<#-- Button for submitting search has been repeated with btn-block and hidden on lg and md devices to fix mobile support -->
		<#--
			<button type="submit" class="btn btn-default btn-block sympl-search hidden-sm hidden-lg hidden-md">
				<span class="glyphicon glyphicon-search" role="submit"></span>
			</button>
		</form>
		-->
	
		<div id="navbar-browse" class="dropdown navbar-left">
			
			<button id="lg-browse" class="btn btn-default navbar-btn dropdown-toggle hidden-xs" type="button" data-toggle="dropdown">
				<#--
				<span class="caret"></span>
				-->
				<span class="glyphicon glyphicon-menu-hamburger"></span>
				Browse
			</button>
		
			<#-- Browse button repeated as block for xs devices -->
			<button id="xs-browse" class="btn btn-default navbar-btn btn-block hidden-sm hidden-lg hidden-md dropdown-toggle" type="button" data-toggle="dropdown">
				<#--
				<span class="caret"></span>
				-->
				<span class="glyphicon glyphicon-menu-hamburger"></span>
				Browse
			</button>
		
			<ul id="main-nav" role="list" class="dropdown-menu">
				<#list menu.items as item>
					<li class="dropdown" role="listitem">
						<a href="${item.url}" title="${item.linkText} ${i18n().menu_item}" 
							<#if item.active> class="active"</#if>>
								${item.linkText}
						</a>
					</li>
				</#list>
			</ul>
		</div>
	</div>
</nav>

<!-- The below div was removed to make styles easy to apply on specific pages -->
<div class="container"> <#--This container has to be left open to contain all search result pages, however it must be closed at the top of footer.ftl or the footer will not extend the width of the browser. -->        
	<#if flash?has_content>
		<#if flash?starts_with(i18n().menu_welcomestart) >
			<section  id="welcome-msg-container" role="container">
				<section  id="welcome-message" role="alert">${flash}</section>
			</section>
		<#else>
			<section id="flash-message" role="alert">
				${flash}
			</section>
		</#if>
	</#if>
	
	<!--[if lte IE 8]>
	<noscript>
		<p class="ie-alert">This site uses HTML elements that are not recognized by Internet Explorer 8 and below in the absence of JavaScript. As a result, the site will not be rendered appropriately. To correct this, please either enable JavaScript, upgrade to Internet Explorer 9, or use another browser. Here are the <a href="http://www.enable-javascript.com"  title="java script instructions">instructions for enabling JavaScript in your web browser</a>.</p>
	</noscript>
	<![endif]-->
