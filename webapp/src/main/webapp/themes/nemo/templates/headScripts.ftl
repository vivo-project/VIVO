<#-- Template for scripts that must be loaded in the head -->
<script>
var i18nStrings = {
    allCapitalized: '${i18n().all_capitalized}',
};
var baseUrl = '${urls.base}';
</script>
<!-- <script type="text/javascript" src="${urls.base}/js/jquery.js"></script> -->

<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->

<script type="text/javascript" src="${urls.base}/webjars/jquery/jquery.min.js"></script>

<script type="text/javascript" src="${urls.base}/js/vitroUtils.js"></script>

<script src="${urls.base}/js/bootstrap/js/bootstrap.min.js"></script> 

<script src="${urls.theme}/js/publicationTableFilter.js"></script>

<!-- <script src="${urls.theme}/js/stickynav.js"></script> -->

<#-- script for enabling new HTML5 semantic markup in IE browsers -->
<!--[if lt IE 9]>
<script type="text/javascript" src="${urls.base}/js/html5.js"></script>
<![endif]-->

${headScripts.list()} 
