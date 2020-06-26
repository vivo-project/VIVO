${scripts.add(
    '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.12.1.min.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/d3v3.min.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/jquery.color.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/jsr_class.js"></script>',
    '<script type="text/javascript" src="${urls.base}/js/visualization/capabilitymap/graph_new.js"></script>'
)}

${stylesheets.add(
    '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.12.1.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/templates/freemarker/edit/forms/css/autocomplete.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/personlevel/page.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/visualization.css" />',
    '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/capabilitymap/graph.css" />'
)}

<script language="JavaScript" type="text/javascript">
    var i18nStringsCap = {
        term: '${i18n().term_capitalized}',
        group: '${i18n().group_capitalized}',
        pause: '${i18n().pause}',
        resume: '${i18n().resume}',
        show_group_labels: '${i18n().show_group_labels}',
        hide_group_labels: '${i18n().hide_group_labels}',
        delete_selected: '${i18n().delete_selected}',
        remove_capability: '${i18n().remove_capability}',
        remove_group: '${i18n().remove_group}',
        expand: '${i18n().expand}'
    };
    var contextPath = "${urls.base}";
    $(document).ready(function() {
        document.title = "${i18n().capability_map}";
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
        <h2>Forschungsnetz</h2>
		<p>Erstellen Sie eine Capability Map durch Eingabe von Suchbegriffen.</p>       
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
                <!-- a style="display:inline-block; float:right; line-height:32px; height:32px; cursor:pointer" onclick="showhideadvanced(this)">Erweitertes anzeigen</a -->
            </span>
        </p>
    </div>

    <hr style="clear:both;">

    <div id="container">
        <div id="helptext">
            <p>
                Willkommen beim Capability-Mapping-Tool.
                Dieses Tool veranschaulicht die Beziehung zwischen verschiedenen Forschenden über Suchbegriffe.
            </p>
            <h3>Erste Schritte</h3>
            <p>
				Geben Sie ein Forschungsfeld in das Suchfeld oben ein und klicken Sie auf 'Suchen'.
				Das resultierende Diagramm zeigt den Suchbegriff in orange an,
				in Verbindung mit der blauen Gruppe von Forschenden, die in diesem Bereich tätig sind.
				Geben Sie einen anderen Suchbegriff ein, um zu sehen, wie die Forscher aus beiden Suchen zusammenhängen.
				Fügen Sie weitere Suchbegriffe hinzu, um eine Capability Map ('Fähigkeitskarte') zu erstellen.
            </p>
            <p>
				Tipp: Sie können einen groben Suchbegriff 
				durch Klicken auf &lsquo;suchen und erweitern&rsquo; in speziellere Begriffe erweitern.                
            </p>
            <h3>Interagieren mit der Visualisierung </h3>
            <p>
                Durch Anklicken eines beliebigen Knotens in der Visualisierung 
				können zusätzliche Informationen in der Registerkarte "Info" 
				auf der rechten Seite angezeigt werden.
                Für Personengruppen können die Angehörigen der Gruppe und 
				Informationen über sie eingesehen sowie einzelne Forscher 
				aus der Visualisierung entfernt werden.
                Wenn Sie einen Suchbegriff auswählen, werden alle angehängten Gruppen angezeigt.
                Unter jeder Gruppe werden alle Informationen für jede Person abgerufen und die 
				Anzahl der passenden Stipendien und Publikationen für jeden Forscher innerhalb
				der abgebildeten Fähigkeiten angezeigt.
                Ein Klick auf den Namen eines Forschers führt zu den ursprünglichen Suchergebnissen.
            </p>
            <h4>Visuelle Hinweise</h4>
            <p>
                Um die Visualisierung besser lesbar zu machen,
				werden Suchbegriffe und Gruppen entsprechend 
				der Anzahl der zurückgegebenen Ergebnisse skaliert.
                Je nach Anzahl der verknüpften Suchbegriffe erhalten
				die Gruppen auch unterschiedliche Schattierungen.
                Je dunkler der Farbton, desto mehr Suchbegriffe sind mit einer Gruppe verbunden.
            </p>
            <h3>Erweiterte Funktionen</h3>
            <h4>Ändern des Cutoff-Wertes</h4>
            <p>
				Die Anzahl der gefundenen Forscher pro Suchbegriff ist 
				durch den Cutoff-Wert im Suchformular begrenzt 
				(standardmäßig 10).
				Eine Erhöhung dieses Grenzwertes erhöht die Wahrscheinlichkeit
				einer Überschneidung zwischen verschiedenen Suchbegriffen.
				Dies erhöht jedoch auch die Komplexität des Graphen und 
				kann die Erkennung von Mustern erschweren.
                
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
                    <li><a href="#demo">Suchbegriffe</a></li>
                    <li><a href="#logg">Info</a></li>
                    <!-- li><a href="#extractData">Data</a></li -->
                </ul>

                <div class="result_body">
                    <div class="result_section" id="demo">
                        <h2>Aktuelle Suchbegriffe</h2>
                        <ul id="log_printout">
                            <li>Dieses Fenster zeigt eine Liste der Suchbegriffe an, die sich derzeit in der Grafik befinden. Um zu beginnen, bitte nach etwas suchen.</li>
                        </ul>
                        <p style="position:absolute; bottom:10px">
                            <img src="${urls.base}/images/visualization/capabilitymap/key.png" alt="Key">
                        </p>
                    </div>
                    <div class="result_section" id="logg">
                        <div id="inner-details">
                            <p>
                                Dieses Fenster zeigt Informationen über einzelne 
								Suchbegriffe und Gruppen an. Klicken Sie auf eine
								Gruppe, um Informationen über sie anzuzeigen.
                            </p>
                        </div>
                    </div>

                    <!-- div class="result_section" id="extractData">
                        <h2>Daten ex- und importieren</h2>
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

