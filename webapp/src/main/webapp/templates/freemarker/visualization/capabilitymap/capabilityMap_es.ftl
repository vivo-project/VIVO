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
                Bienvenido a la herramienta de Mapeo de capacidades.
                Esta herramienta visualiza cómo los investigadores se relacionan con otros
                investigadores a través de términos de búsqueda.
            </p>
            <h3>Empezando</h3>
            <p>
                Ingrese un área de investigación en el campo de búsqueda de arriba y presione 'Buscar'.
                El diagrama resultante muestra el término de búsqueda, renderizado en naranja,
                conectado al grupo azul de investigadores que están activos en esa área.
                Ingrese otro término de búsqueda para ver cómo se relacionan los investigadores de ambas búsquedas.
                Siga añadiendo términos de búsqueda para crear un mapa de capacidades.
            </p>
            <p>
                Sugerencia: puede expandir un término de búsqueda amplio en conceptos más pequeños
                haciendo clic en & lsquo; buscar y expandir & rsquo ;.
            </p>
            <h3>Interactuando con la visualización</h3>
            <p>
                Al hacer clic en cualquier nodo en la visualización,
                información adicional se puede ver en el
                Pestaña 'Información' en el lado derecho.
                Para grupos de personas, los participantes en el grupo
                y su información puede ser vista,
                y los investigadores individuales pueden eliminarse del gráfico.
                Al seleccionar un término de búsqueda, se mostrarán todos los grupos adjuntos.
                Debajo de cada grupo se recupera la información completa de cada persona,
                y el número de subvenciones y publicaciones correspondientes
                para cada investigador dentro de las capacidades mapeadas se muestra.
                Al hacer clic en el nombre de un investigador se llevará a la búsqueda original
                resultados.
            </p>
            <h4>Señales visuales</h4>
            <p>
                Para facilitar la lectura de la visualización,
                los términos y grupos de búsqueda se escalan de acuerdo
                a la cantidad de resultados devueltos.
                Los grupos también reciben diferentes tonos
                de acuerdo con la cantidad de términos de búsqueda conectados.
                Cuanto más oscura es la sombra, más términos de búsqueda se conectan a un grupo.
            </p>
            <h3>Características avanzadas</h3>
            <h4>Cambiar el valor de corte</h4>
            <p>
                La cantidad de investigadores recuperados para cada término de búsqueda
                para está limitado por el valor de corte en el formulario de búsqueda
                (10 por defecto).
                Aumentar este límite aumentará la probabilidad
                de una intersección entre diferentes términos de búsqueda.
                Esto también aumentará la complejidad del gráfico,
                sin embargo, y puede dificultar la identificación de patrones.
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
                    <li><a href="#demo">Términos de búsqueda</a></li>
                    <li><a href="#logg">Información</a></li>
                    <!-- li><a href="#extractData">Data</a></li -->
                </ul>

                <div class="result_body">
                    <div class="result_section" id="demo">
                        <h2>Términos de búsqueda actuales</h2>
                        <ul id="log_printout">
                            <li>Este panel muestra una lista de los términos de búsqueda actualmente
                                 en el gráfico. Busque algo para comenzar.</li>
                        </ul>
                        <p style="position:absolute; bottom:10px">
                            <img src="${urls.base}/images/visualization/capabilitymap/key.png" alt="Key">
                        </p>
                    </div>
                    <div class="result_section" id="logg">
                        <div id="inner-details">
                            <p>
                                Este panel muestra información sobre el individuo
                                términos y grupos de búsqueda. Haga clic en un grupo para mostrar
                                su información.
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

