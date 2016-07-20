${scripts.add(
    '<script type="text/javascript" src="${urls.base}/js/d3.min.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/jquery.color.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/jsr_class.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/graph_new.js"></script>'
)}

${stylesheets.add(
    '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />',
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
</script>
<div class="main" id="main-content" role="main">
    <div class="col-8">
        <h2>Capability Map</h2>
        <p>Build a &lsquo;first pass&rsquo; capability map by typing in a<!-- set of--> search term<!--s--> that could be said to represent a broad research capability.</p>
    </div>

    <div id="queryform">
        <p>
            <span>
                <input name="query" id="query" size="34" value="" onfocus="" accesskey="q" onblur="" type="text" onkeydown="queryKeyDown(event);">
                <label id="cutofflabel" for="queryCutoff">Cutoff:</label>
                <input id="queryCutoff" name="queryCutoff" type="text" title="Cutoff" size="4" value="10">
                <input type="submit" value="Search" id="add" type="button" onclick="addKwd();">
                <input value="Search and Expand" type="submit" id="sExpand" onclick="expandLastQuery = 1; addKwd();">
                <input value="Reset" id="resetButton" type="submit" onclick="reset()" disabled>
                <!-- a style="display:inline-block; float:right; line-height:32px; height:32px; cursor:pointer" onclick="showhideadvanced(this)">Show advanced</a -->
            </span>
        </p>
    </div>

    <hr style="clear:both;">

    <div id="container">
        <div id="helptext">
            <p>
                Welcome to the Capability Mapping tool.
                This tool visualises how researchers relate to other
                researchers via search terms.
            </p>
            <h3>Getting Started</h3>
            <p>
                Enter a research area into the search field above and press 'Search'.
                The resulting diagram displays the search term, rendered in orange,
                connected to the blue group of researchers that are active in that area.
                Enter another search term to see how researchers from both searches relate.
                Keep adding search terms to build a capability map.
            </p>
            <p>
                Tip: you can expand a broad search term into smaller concepts
                by clicking &lsquo;search and expand&rsquo;.
            </p>
            <h3>Interacting with the visualisation</h3>
            <p>
                By clicking on any node in the visualisation,
                additional information can be viewed in the
                'Info' tab on the right-hand side.
                For groups of people, the participants in the group
                and their information can be viewed,
                and individual researchers can be removed from the graph.
                Selecting a search term will display all attached groups.
                Under each group full information for each person is retrieved,
                and the number of matching grants and publications
                for each researcher within the mapped capabilities is shown.
                Clicking on a researcher's name will lead to the original search
                results.
            </p>
            <h4>Visual cues</h4>
            <p>
                To make the visualisation easier to read,
                search terms and groups are scaled according
                to the number of results returned.
                Groups are also given different shades
                according to the number of connected search terms.
                The darker the shade, the more search terms a group is connected to.
            </p>
            <h3>Advanced features</h3>
            <h4>Changing the cutoff value</h4>
            <p>
                The amount of researchers retrieved for each search term
                for is limited by the cutoff value in the search form
                (10 by default).
                Increasing this cutoff will increase the likelihood
                of an intersection between different search terms.
                This will also increase the complexity of the graph,
                however, and may make it difficult to identify patterns.
            </p>
        </div>

        <div id="center-container">
            <div id="log"></div>
            <div id="infovis"></div>
            <div class="progress"><div id="progressbar"></div></div>
        </div>

        <div id="right-container">
            <div class="tabs">
                <ul  class="titles">
                    <li><a href="#demo">Search terms</a></li>
                    <li><a href="#logg">Info</a></li>
                    <!-- li><a href="#extractData">Data</a></li -->
                </ul>

                <div class="result_body">
                    <div class="result_section" id="demo">
                        <h2>Current search terms</h2>
                        <ul id="log_printout">
                            <li>This panel displays a list of the search terms currently
                                on the graph. Search for something to begin.</li>
                        </ul>
                        <p style="position:absolute; bottom:10px">
                            <img src="${urls.base}/images/visualization/capabilitymap/key.png" alt="Key">
                        </p>
                    </div>
                    <div class="result_section" id="logg">
                        <div id="inner-details">
                            <p>
                                This panel displays information about individual
                                search terms and groups. Click on a group to display
                                its information.
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

