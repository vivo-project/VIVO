<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
	</div> <#-- .container -->
</div> <#-- .row -->
<#-- The above two closing divs stop the row and container classes from menu.ftl extending into the footer -->

<!-- #wrapper-content -->
<#include "scripts.ftl">
<#-- Funnelback scripts -->
<#--

<script src="/s/resources-global/js/jquery/jquery-ui-1.10.3.custom.min.js"></script>
<script src="/s/resources-global/js/jquery/jquery.tmpl.min.js"></script>
<script src="/s/resources-global/js/jquery.funnelback-completion-15.2.0.js"></script>
<script>
 jQuery(document).ready( function() {
        // Query completion setup.
    jQuery('input[name="query"]').fbcompletion({
      'enabled'    : 'enabled',
      'standardCompletionEnabled': true,
      'collection' : 'vivo-lilliput',
      'program'    : '/s/suggest.json',
      'format'     : 'extended',
      'alpha'      : '.5',
      'show'       : '10',
      'sort'       : '0',
      'length'     : '3',
      'delay'      : '0',
      'profile'    : '',
      'query'      : 'query=data&amp;collection=vivo-lilliput&amp;form=lilliputsimple'//,
      //Search based completion
     // 'searchBasedCompletionEnabled': 'enabled',
     // 'searchBasedCompletionProgram': '/s/search.json',
    });
  });
</script>
<script id="fb-completion-tmpl" type="text/x-jquery-tmpl">
    ${name}
   <img src="/vivo${imageUrl}?width=100&amp;height=100&amp;type=crop_center" class="img-circle" width="50px"/>
</script>
-->

<#-- End Funnelback Scripts -->
<footer role="contentinfo" class="footer">
	<div class="row">
		<div class="container">
			<div class="col-md-12">	
				<nav role="navigation">
					<ul id="footer-nav" role="list">
						<li role="listitem">
							<a 
								href="${urls.about}" 
								title="${i18n().menu_about}"
							>
								${i18n().menu_about}
							</a>
						</li>

						<#if urls.contact??>
						<li role="listitem">
							<a 
								href="${urls.contact}" 
								title="${i18n().menu_contactus}"
							>
								${i18n().menu_contactus}
							</a>
						</li>
						</#if>
<#--
						<li role="listitem">
							<a 
								href="https://forms.office.com/Pages/ResponsePage.aspx?id=5J-Z2K92s0C0NR2Jd6vAjFJPNQde4ZZKpVP4yKR5B29UQVFaR1FRV0NLRVhBTjhEOUc5MEpaVU5SVS4u" 
								target="blank" 
								title="${i18n().menu_contactus}"
							>
								${i18n().menu_contactus}
							</a>
						</li>
-->
						<li role="listitem">
							<a 
								href="${urls.base}/support" 
								target="blank" 
								title="${i18n().menu_support}"
							>
								${i18n().menu_support}
							</a>
						</li>

						<li role="listitem">
							<a 
								href="${urls.base}/research" 
								target="_blank" 
								title="${i18n().menu_support}"
							>
								Browse Research
							</a>
						</li>
					</ul>
				</nav>
				
				<p class="copyright">
					<#if copyright??>
					       &copy;${copyright.year?c}

						<#if copyright.url??>
							<a 
								href="${copyright.url}" 
								title="${i18n().menu_copyright}"
							>
							${copyright.text}
							</a>

						<#else>
							 ${copyright.text} 
						</#if>

						| <a 
							class="terms" 
							href="${urls.termsOfUse}" 
							title="${i18n().menu_termuse}"
						>
							${i18n().menu_termuse}
						</a> | 

					</#if>
					${i18n().menu_powered} <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank" title="${i18n().menu_powered} VIVO"><strong>VIVO</strong>
					</a>
					<#if user.hasRevisionInfoAccess>
						 | ${i18n().menu_version} <a href="${version.moreInfoUrl}" title="${i18n().menu_version}">${version.label}</a>
					</#if>
				</p>
			</div>
		</div>
	</div>
</footer>
