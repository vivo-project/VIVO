<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->


<header id="branding" role="banner">
	<div class="header">
		<div class="hgroup">
			<div class="container">
				<div class="row">
					<div class="col-md-12 col-sm-12 hidden-xs logo-sm">
						<a href="${urls.home}"><img height="66" src="${urls.theme}/images/vivo_logo.png" alt="VIVO Logo" /></a>
					</div>
					<div class="col-xs-12 visible-xs-block">
						<a href="${urls.home}"><img height="66" src="${urls.theme}/images/vivo_logo.png" alt="VIVO Logo" /></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</header>

<#--
<header id="branding" role="banner">
	<div class="header">
		<div class="hgroup">
			<div class="container">
				<div class="row">
					<div class="col-md-6 col-sm-6 hidden-xs">
						<h1>
							<a href="${urls.home}">Find an Expert</a>
						</h1>
					</div>
					<div class="col-xs-12  visible-xs-block" >
						<h1>
							<a href="${urls.home}">Find an Expert</a>
						</h1>
					</div>
					<div class="col-md-6 col-sm-6 hidden-xs logo-sm">
						<img src="${urls.theme}/images/bootstrap-vivo-logo-beta.png" alt="Institution Logo-beta" />
					</div>
					<div class="col-xs-12 visible-xs-block">
						<img src="${urls.theme}/images/bootstrap-vivo-logo-beta.png" alt="Institution Logo" />
					</div>
				</div>
			</div>
		</div>
	</div>
</header>
-->

	<#--<!--<h1 class="vivo-logo"><a title="${i18n().identity_title}" href="${urls.home}"><span class="displace">${siteName}</span></a></h1>
	<#-- Since we are using a graphic text for the tagline, we won't render ${siteTagline}
	<#if siteTagline?has_content>
		<em>${siteTagline}</em>
	</#if>-->

<#--<!--    <nav role="navigation">
		<ul id="header-nav" role="list">
			<#include "languageSelector.ftl">
			<li role="listitem"><a href="${urls.index}" title="${i18n().identity_index}">${i18n().identity_index}</a></li>
			<#if user.loggedIn>
				<#if user.hasSiteAdminAccess>
					<li role="listitem"><a href="${urls.siteAdmin}" title="${i18n().identity_admin}">${i18n().identity_admin}</a></li>
				</#if>
					<li>
						<ul class="dropdown">
							<li id="user-menu"><a href="#" title="${i18n().identity_user}">${user.loginName}</a>
								<ul class="sub_menu">
									 <#if user.hasProfile>
										 <li role="listitem"><a href="${user.profileUrl}" title="${i18n().identity_myprofile}">${i18n().identity_myprofile}</a></li>
									 </#if>
									 <#if urls.myAccount??>
										 <li role="listitem"><a href="${urls.myAccount}" title="${i18n().identity_myaccount}">${i18n().identity_myaccount}</a></li>
									 </#if>
									 <li role="listitem"><a href="${urls.logout}" title="${i18n().menu_logout}">${i18n().menu_logout}</a></li>
								</ul>
							</li>
						 </ul>
					 </li>
					 

				
				${scripts.add('<script type="text/javascript" src="${urls.base}/js/userMenu/userMenuUtils.js"></script>')}
				
			<#else>
				<li role="listitem"><a class="log-out" title="${i18n().menu_loginfull}" href="${urls.login}">${i18n().menu_login}</a></li>
			</#if>
		</ul>
		
	</nav>
-->
