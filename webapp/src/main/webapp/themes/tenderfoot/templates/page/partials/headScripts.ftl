<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<#-- Template for scripts that must be loaded in the head -->
<script>
var i18nStrings = {
    allCapitalized: '${i18n().all_capitalized?js_string}',
};
var baseUrl = '${urls.base}';
</script>
<script type="text/javascript" src="${urls.base}/webjars/jquery/jquery.min.js"></script>
<script type="text/javascript" src="${urls.base}/webjars/jquery-migrate/jquery-migrate.min.js"></script>
<script type="text/javascript" src="${urls.base}/js/vitroUtils.js"></script>

<#-- script for enabling new HTML5 semantic markup in IE browsers -->
<!--[if lt IE 9]>
<script type="text/javascript" src="${urls.base}/js/html5.js"></script>
<![endif]-->

<script src="${urls.base}/js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
        $('#nav-wrapper').height($("#nav").height());

		$('#nav').affix({
			offset: {
				top: $('header').height()
			}
		});

        $( window ).resize(function() {
            $('#nav-wrapper').height($("#nav").height());
        });

    });
</script>

${headScripts.list()}
