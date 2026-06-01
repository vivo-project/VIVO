${scripts.add(
    '<script type="text/javascript" src="${urls.base}/webjars/jquery-ui/jquery-ui.min.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/d3v3.min.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/jquery.color.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/jsr_class.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/graph_new.js"></script>'
)}

${stylesheets.add(
    '<link rel="stylesheet" href="${urls.base}/webjars/jquery-ui-themes/smoothness/jquery-ui.min.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/templates/freemarker/edit/forms/css/autocomplete.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/personlevel/page.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/visualization.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/capabilitymap/graph.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/capabilitymap/key.css" />'
)}

<script language="JavaScript" type="text/javascript">
    var i18nStringsCap = {
        term: '${i18n().term_capitalized?js_string}',
        group: '${i18n().group_capitalized?js_string}',
        pause: '${i18n().pause?js_string}',
        resume: '${i18n().resume?js_string}',
        reset: '${i18n().cap_map_reset?js_string}',
        show_group_labels: '${i18n().show_group_labels?js_string}',
        hide_group_labels: '${i18n().hide_group_labels?js_string}',
        delete_selected: '${i18n().delete_selected?js_string}',
        remove_capability: '${i18n().remove_capability?js_string}',
        remove_group: '${i18n().remove_group?js_string}',
        expand: '${i18n().expand?js_string}',
        view: '${i18n().view?js_string}',
        capability_map_remove_person: '${i18n().capability_map_remove_person?js_string}',
        capability_map_svg_title: '${i18n().capability_map_svg_title?js_string}',
        capability_map_svg_desc: '${i18n().capability_map_svg_desc?js_string}'
    };
    var contextPath = "${urls.base}";
    $(document).ready(function() {
        document.title = "${i18n().capability_map?js_string}";
        var loadedConcepts = $.ajax({
            url: contextPath + "/visualizationAjax?vis=capabilitymap&data=concepts",
            type: "GET",
            async: false,
            success: function(result) {
                return result;
            }
        });
        var conceptArray = JSON.parse(loadedConcepts.responseText);
        $("#query").autocomplete({
            source: conceptArray,
            minLength: 1,
            open: function(event, ui) {
                $("#query").attr("aria-expanded", "true");
                $(".ui-autocomplete").attr("id", "query-autocomplete-list").attr("role", "listbox");
                $(".ui-autocomplete li").attr("role", "option");
            },
            close: function(event, ui) {
                $("#query").attr("aria-expanded", "false");
            },
            select: function(event, ui) {
                $("#query").val(ui.item.value);
                return false;
            }
        }).data("ui-autocomplete")._renderItem = function(ul, item) {
            return $("<li>")
                .attr("role", "option")
                .append($("<div>").text(item.label))
                .appendTo(ul);
        };

    });
</script>
<div class="main" id="main-content" role="main">
    <div class="col-8">
        <h2>${i18n().capability_map}</h2>
        <p>${i18n().cap_map_intro}</p>
    </div>

    <div id="queryform">
        <p>
            <span>
                <input name="query" id="query" size="34" value="" onfocus="" accesskey="q" onblur="" type="text" onkeydown="queryKeyDown(event);" aria-label="${i18n().capability_map_input?js_string}" role="combobox" aria-autocomplete="list" aria-haspopup="listbox" aria-expanded="false" aria-owns="query-autocomplete-list">
                <label id="cutofflabel" for="queryCutoff">Cutoff:</label>
                <input id="queryCutoff" name="queryCutoff" type="text" title="Cutoff" size="4" value="10">
                <input value="${i18n().cap_map_search}" type="submit" id="add" type="button" onclick="addKwd();">
                <input value="${i18n().cap_map_search_expand}" type="submit" id="sExpand" onclick="expandLastQuery = 1; addKwd();">
                <input value="${i18n().cap_map_reset}" id="resetButton" type="submit" onclick="reset()" disabled>
                <!-- a style="display:inline-block; float:right; line-height:32px; height:32px; cursor:pointer" onclick="showhideadvanced(this)">Show advanced</a -->
            </span>
        </p>
    </div>

    <hr style="clear:both;">

    <div id="container">
        <div id="helptext">
            <p>
                ${i18n().cap_map_text_intro}
            </p>
            <h3>${i18n().cap_map_text_headline1}</h3>
            <p>
                ${i18n().cap_map_text1}
            </p>
            <p>
                ${i18n().cap_map_text2}
            </p>
            <h3>${i18n().cap_map_text_headline2}</h3>
            <p>
                ${i18n().cap_map_text3}
            </p>
            <h4>${i18n().cap_map_text_headline3}</h4>
            <p>
                ${i18n().cap_map_text4}
            </p>
            <h3>${i18n().cap_map_text_headline4}</h3>
            <h4>${i18n().cap_map_text_headline5}</h4>
            <p>
                ${i18n().cap_map_text5}
            </p>
        </div>

        <div id="center-container">
            <div id="log"></div>
            <div id="infovis"></div>
            <div class="capability-progress"><div id="progressbar"></div></div>
        </div>

        <div id="right-container">
            <div class="tabs">
                <ul  class="titles" role="tablist" aria-orientation="horizontal">
                    <li>
                        <a href="#tabpanel-demo" id="tab-demo" role="tab" aria-controls="tabpanel-demo" aria-selected="true" tabindex="0">${i18n().cap_map_search_terms}</a>
                    </li>
                    <li>
                        <a href="#tabpanel-logg" id="tab-logg" role="tab" aria-controls="tabpanel-logg" aria-selected="false" tabindex="0">${i18n().cap_map_info}</a>
                    </li>
                    <!-- li><a href="#extractData">Data</a></li -->
                </ul>

                <div class="result_body">
                    <div class="result_section" id="tabpanel-demo" role="tabpanel" aria-labelledby="tab-demo" tabindex="0">
                        <h2 id="cur-search-terms">${i18n().cap_map_cur_search_terms}</h2>
                        <div id="log_printout" role="group" aria-labelledby="cur-search-terms">
                            <ul>
                                <li>
                                    ${i18n().cap_map_text6}
                                </li>
                            </ul>
                        </div>
                        <p style="position:absolute; bottom:10px">
                        <div class="capability">${i18n().cap_map_key1}</div>
                        <div class="edge">${i18n().cap_map_key2}</div>
                        <div class="group">${i18n().cap_map_key3}</div>
                        <div class="links2">${i18n().cap_map_key4}</div>
                        <div class="links3">${i18n().cap_map_key5}</div>
                        <div class="links4">${i18n().cap_map_key6}</div>
                        </p>
                    </div>
                    <div class="result_section" id="tabpanel-logg" role="tabpanel" aria-labelledby="tab-logg" tabindex="0" hidden>
                        <div id="inner-details">
                            <p>
                                ${i18n().cap_map_text7}
                            </p>
                        </div>
                    </div>

                    <!-- div class="result_section" id="extractData">
                        <h2>Extract Data</h2>
                        <p>
                            Import:
                            <button disabled>SVG</button>
                            <button onclick="importGraphDetails();">JSON</button>
                            <br>
                            Export:
                            <button onclick="generateGraphSVG();">SVG</button>
                            <button onclick="download(g.export(), 'json')">JSON</button>
                            <button onclick="download(g.toDOT(), 'gv')">DOT</button>
                            <button onclick="generateGraphPersonList();">MRW</button>
                        </p>
                        <textarea id="graphDetails" style="width:99%; height:450px; border:1px solid #EEE; padding:0px;"></textarea>
                    </div -->
                </div>
            </div>
        </div>
    </div>
</div>
