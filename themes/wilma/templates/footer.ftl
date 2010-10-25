<footer>
    <div id="footer-content">
      	<#if copyright??>
			<p class="copyright"><small>&copy;${copyright.year?c} 
				<#if copyright.url??>
		    		<a href="${copyright.url}">${copyright.text}</a>  
				<#else>
		    		${copyright.text}
				</#if> 
			All Rights Reserved | <a class="terms" href="${urls.termsOfUse}">Terms of Use</a></small> | Powered by <a class="powered-by-vivo" href="http://vivoweb.org" target="_blank"><strong>VIVO</strong></a></p>
		</#if>
      <nav>
        <ul id="footer-nav">
          	<li><a href="${urls.about}">About</a></li>
			<#if urls.contact??>
			    <li><a href="${urls.contact}">Contact Us</a></li>
			</#if> 
			<li><a href="http://www.vivoweb.org/support" target="blank">Support</a></li>
        </ul>
      </nav>
    </div>
  </footer>
</div>



<script type="text/javascript" src="http://use.typekit.com/chp2uea.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>
${scripts.add("/js/jquery.js")}
${scripts.tags}


<!--[if lt IE 7]>
<script type="text/javascript" src="${themeDir}/js/jquery_plugins/supersleight.js"></script>
<script type="text/javascript" src="${themeDir}/js/utils.js"></script>
<link rel="stylesheet" href="css/ie6.css" />
<![endif]-->

<!--[if IE 7]>
<link rel="stylesheet" href="css/ie7.css" />
<![endif]-->

<!--[if (gte IE 6)&(lte IE 8)]>
<script type="text/javascript" src="${themeDir}/js/selectivizr.js"></script>
<![endif]-->

<#include "googleAnalytics.ftl">