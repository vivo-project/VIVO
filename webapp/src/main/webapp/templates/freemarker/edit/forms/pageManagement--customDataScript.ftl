<#-- $This file is distributed under the terms of the license in LICENSE$ -->
<#--Sets up script for custom form data. Including here instead of as separate javascript
file to ensure this script is always loaded first.  Can also make sure placed first in
scripts list.-->

<#--Overwrites original in Vitro by including internalClass as the data getter instead of individuals for classes-->
<#assign defaultHeight="200" />
<#assign defaultWidth="75%" />
<#assign defaultButton="bold,italic,underline,separator,link,bullist,numlist,separator,sub,sup,charmap,separator,undo,redo,separator,code"/>   
<#assign defaultToolbarLocation = "top" />
<#if !height?has_content>
	<#assign height=defaultHeight/>
</#if>

<#if !width?has_content>
	<#assign width=defaultWidth />
</#if>

<#if !buttons?has_content>
	<#assign buttons = defaultButton />
</#if>

<#if !toolbarLocation?has_content>
	<#assign toolbarLocation = defaultToolbarLocation />
</#if>

<script type="text/javascript">
    var customFormData = {
    menuAction:"${menuAction}",
    addMenuItem:"${addMenuItem}",
      dataGetterLabelToURI:{
      		//maps labels to URIs
      		"browseClassGroup": "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData",
      		"internalClass": "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter",
      		"sparqlQuery":"java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SparqlQueryDataGetter",
      		"fixedHtml":"java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.FixedHTMLDataGetter",
      		"searchIndividuals":"java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SearchIndividualsDataGetter"
      },
      tinyMCEData : {
                theme : "advanced",
                mode : "textareas",
                theme_advanced_buttons1 : "${buttons}",
                theme_advanced_buttons2 : "",
                theme_advanced_buttons3 : "",
                theme_advanced_toolbar_location : "${toolbarLocation}",
                theme_advanced_toolbar_align : "left",
                theme_advanced_statusbar_location : "bottom",
                theme_advanced_path : false,
                theme_advanced_resizing : true,
                height : "${height}",
                width  : "${width}",
                valid_elements : "tr[*],td[*],tbody[*],table[*],a[href|name|title|style],br,p[style],i,em,cite,strong/b,u,sub,sup,ul,ol,li,h1[dir|style|id],h2[dir|style|id],h3[dir|style|id],h4,h5,h6,div[style|class],span[dir|style|class]",
                fix_list_elements : true,
                fix_nesting : true,
                cleanup_on_startup : true,
                gecko_spellcheck : true,
                forced_root_block: false,
                plugins : "paste",
                paste_use_dialog : false,
                paste_auto_cleanup_on_paste : true,
                paste_convert_headers_to_strong : true,
                paste_strip_class_attributes : "mso",
                paste_remove_spans : true,
                paste_remove_styles : true,
                paste_retain_style_properties : ""
        }
    };
</script>

${scripts.add('<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/tiny_mce/jquery.tinymce.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/edit/initTinyMce.js"></script>')}
