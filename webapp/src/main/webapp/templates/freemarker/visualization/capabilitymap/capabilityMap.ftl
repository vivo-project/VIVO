
${scripts.add(
    '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.12.1.min.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/d3v3.min.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/jquery.color.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/jsr_class.js"></script>'
)}

${stylesheets.add(
    '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.12.1.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/templates/freemarker/edit/forms/css/autocomplete.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/personlevel/page.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/visualization.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/capabilitymap/graph.css" />'
)}

<script language="JavaScript" type="text/javascript">
    var contextPath = "${urls.base}";
    $(document).ready(function() {
        var loadedConcepts = $.ajax({
            url: contextPath + "/visualizationAjax?vis=capabilitymap&data=concepts",
            type: "GET",
            async: false,
            success: function(result) {
                return result;
            }
        });
        var conceptArray = $.parseJSON(loadedConcepts.responseText);
        $("#query").autocomplete({
            source: conceptArray
        });
    });
    

var i18nStringsMap = {
    capability_map_term: '${i18n().capability_map_term}',
    capability_map_remove_capability: '${i18n().capability_map_remove_capability}',
    capability_map_expand: '${i18n().capability_map_expand}',
    capability_map_group: '${i18n().capability_map_group}',
    capability_map_remove_group: '${i18n().capability_map_remove_group}',
    capability_map_reset: '${i18n().capability_map_reset}',
    capability_map_pause: '${i18n().capability_map_pause}',
    capability_map_hide_group_labels: '${i18n().capability_map_hide_group_labels}',
    capability_map_show_group_labels: '${i18n().capability_map_show_group_labels}',
    capability_map_delete_selected: '${i18n().capability_map_delete_selected}',
    capability_map_cutoff: '${i18n().capability_map_cutoff}'
};
</script>

<div class="main" id="main-content" role="main">
    <div class="col-8">
        <h2>${i18n().capability_map_title}</h2>
        <p>${i18n().capability_map_general_instruction}</p>
    </div>

    <div id="queryform">
        <p>
            <span>
                <input name="query" id="query" size="34" value="" onfocus="" accesskey="q" onblur="" type="text" onkeydown="queryKeyDown(event);">
                <label id="cutofflabel" for="queryCutoff">${i18n().capability_map_cutoff_label}</label>
                <input id="queryCutoff" name="queryCutoff" type="text" title="${i18n().capability_map_cutoff_title}" size="4" value="10">
                <input type="submit" value="${i18n().capability_map_search_button_label}" id="add" type="button" onclick="addKwd();">
                <input value="${i18n().capability_map_search_and_expand_button_label}" type="submit" id="sExpand" onclick="expandLastQuery = 1; addKwd();">
                <input value="${i18n().capability_map_reset_button_label}" id="resetButton" type="submit" onclick="reset()" disabled>
                <!-- a style="display:inline-block; float:right; line-height:32px; height:32px; cursor:pointer" onclick="showhideadvanced(this)">${i18n().capability_map_show_advanced}</a -->
            </span>
        </p>
    </div>

    <hr style="clear:both;">

    <div id="container">
        <div id="helptext">
            <p>
            	${i18n().capability_map_welcome}
            </p>
            <h3>${i18n().capability_map_getting_started}</h3>
            <p>
            	${i18n().capability_map_description}
            </p>
            <p>
            	${i18n().capability_map_tip}
            </p>
            
            <h3>${i18n().capability_map_subtitle}</h3>
            <p>
            	${i18n().capability_map_text_1}    
            </p>
            <h4>${i18n().capability_map_visual_cues}</h4>
            <p>${i18n().capability_map_text_2}
            </p>
            <h3>${i18n().capability_map_adv_features}</h3>
            <h4>${i18n().capability_map_changing_cutoff_subtitle}</h4>
            <p>${i18n().capability_map_cuttoff_description}
                
            </p>
        </div>

        <div id="center-container">
            <div id="log"></div>
            <div id="infovis"></div>
            <div class="capability-progress"><div id="progressbar"></div></div>
        </div>

        <div id="right-container">
            <div class="tabs">
                <ul  class="titles">
                    <li><a href="#demo">${i18n().capability_map_search_terms_tab_title}</a></li>
                    <li><a href="#logg">${i18n().capability_map_info_tab_title}</a></li>
                    <!-- li><a href="#extractData">${i18n().capability_map_data_tab_title}</a></li -->
                </ul>

                <div class="result_body">
                    <div class="result_section" id="demo">
                        <h2>${i18n().capability_map_current_search_terms}</h2>
                        <ul id="log_printout">
                            <li>${i18n().capability_map_search_terms_panel_description}</li>
                        </ul>
                        <p style="position:absolute; bottom:10px">
                            <img src="${urls.base}/images/visualization/capabilitymap/key.png" alt="Key">
                        </p>
                    </div>
                    <div class="result_section" id="logg">
                        <div id="inner-details">
                            <p>${i18n().capability_map_result_section}
                                
                            </p>
                        </div>
                    </div>

                    <!-- div class="result_section" id="extractData">
                        <h2>${i18n().capability_map_extract_data}</h2>
                        <p>
                            ${i18n().capability_map_import}
                            <button disabled>${i18n().capability_map_import_SVG_button_label}SVG</button>
                            <button onclick="importGraphDetails();">${i18n().capability_map_import_JSON_button_label}JSON</button>
                            <br>
                            ${i18n().capability_map_export}
                            <button onclick="generateGraphSVG();">${i18n().capability_map_SVG_export_button_label}</button>
                            <button onclick="download(g.export(), 'json')">${i18n().capability_map_export_json_button_label}</button>
                            <button onclick="download(g.toDOT(), 'gv')">${i18n().capability_map_DOT_button_label}</button>
                            <button onclick="generateGraphPersonList();">${i18n().capability_map_MRW_button_label}</button>
                        </p>
                        <textarea id="graphDetails" style="width:99%; height:450px; border:1px solid #EEE; padding:0px;"></textarea>
                    </div -->
                </div>
            </div>
        </div>
    </div>
</div>
${scripts.add(
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/graph_new.js"></script>'
)}
