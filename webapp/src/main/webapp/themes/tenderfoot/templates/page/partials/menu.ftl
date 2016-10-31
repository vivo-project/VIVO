<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<#include "developer.ftl">

<div id="nav">
	<div class="container">
		<div class="navbar-collapse collapse">
			<nav role="navigation">
				<ul id="main-nav" role="list" class="nav navbar-nav">
					<#list menu.items as item>
						<li role="listitem" <#if item.active> class="active" </#if>><a href="${item.url}" title="${item.linkText} ${i18n().menu_item}">${item.linkText}</a></li>
					</#list>
				</ul>
				<ul class="nav pull-right navbar-nav">
				  <li>
					<#include "search.ftl">
				  </li>
				</ul>
			</nav>
		</div>
	</div>
</div>
