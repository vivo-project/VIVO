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
        group: '${i18n().group_capitalized}'
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
        <h2>Cartographie d'expertises</h2>
        <p>Pour construire une première cartographie, saisir un terme de recherche représentant un large domaine d'expertise..</p>
    </div>

    <div id="queryform">
        <p>
            <span>
                <input name="query" id="query" size="34" value="" onfocus="" accesskey="q" onblur="" type="text" onkeydown="queryKeyDown(event);">
                <label id="cutofflabel" for="queryCutoff">Limite:</label>
                <input id="queryCutoff" name="queryCutoff" type="text" title="Limite:" size="4" value="10">
                <input type="submit" value="Chercher" id="add" type="button" onclick="addKwd();">
                <input value="Étendre" type="submit" id="sExpand" onclick="expandLastQuery = 1; addKwd();">
                <input value="Vider" id="resetButton" type="submit" onclick="reset()" disabled>
                <!-- a style="display:inline-block; float:right; line-height:32px; height:32px; cursor:pointer" onclick="showhideadvanced(this)">Show advanced</a -->
            </span>
        </p>
    </div>

    <hr style="clear:both;">

    <div id="container">
        <div id="helptext">
            <p>
                La cartographie d'expertises est un outil simple et efficace qui permet la recherche, 
                l'exploration et la découverte d'experts à travers une cartographie visuelle 
                représentant les liens entre les chercheurs et leurs domaines de recherche.
            </p>
            <h3>Pour commencer</h3>
            <p>
                Saisir un terme de recherche et cliquer sur 'Chercher'.
                Le graphe résultant affiche les expertises sous forme de carrés orangés. 
                À ces sujets sont reliés des points de couleur bleu représentant les chercheurs. 
                Ajouter un autre terme de recherche pour voir comment les résultats de combinent.
                Ajouter d'autres termes au besoin.
            </p>
            <p>
                Conseil: pour étendre la recherche à d'autres concepts reliés
                cliquewr sur  &lsquo;Étendre&rsquo;.
            </p>
            <h3>Interagir avec la cartographie</h3>
            <p>
                En cliquant sur un noeud, de l'information
                supplémentaire est affichée dans l'onglet
                'Informations' du côté droit de la fenêtre.
                Dans le cas de groupes de chercheurs, les participants
                et leurs métadonnées sont affichées. 
                Les personnes peuvent être retirées du graphe individuellement.
                La sélection d'un terme permet d'afficcher tous les groupes associés.
                Sous chaque groupe les données de chaque individu sont affichées.
                Le nombre de publications et de subventions correspondant à chaque
                chercheur sont également présentées.
                En cliquant sur le nom du chercheur on accède à sa fiche complète
                
            </p>
            <h4>Conseils de visualisation</h4>
            <p>
                Pour rendre la visualisation plus facile à lire, 
                les termes de recherche sont classés selon le nombre de résultats retournés.
                Les groupes reçoivent également différentes nuances de couleur 
                en fonction du nombre de termes de recherche connectés. 
                Plus l'ombre est foncée, plus le nombre de termes recherchés 
                auxquels un groupe est connecté est élevé.
            </p>
            <h3>Fonctions avancées</h3>
            <h4>Changer la limite des résultats</h4>
            <p>
                Le nombre maximum de chercheurs retrouvés pour un terme donné 
                est déterminé par la valeur 'limite' qui est fixée à 10 par défaut.
                Hausser cette limite augmentera le nombre possible de connexions
                entre différentes expertises.
                Toutefois, garder à l'esprit qu'une limite trop élevée
                peut rendre la lecture de la cartographie plus difficile.
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
                    <li><a href="#demo">Expertises</a></li>
                    <li><a href="#logg">Informations</a></li>
                    <!-- li><a href="#extractData">Données</a></li -->
                </ul>

                <div class="result_body">
                    <div class="result_section" id="demo">
                        <h2>Termes de recherche courants</h2>
                        <ul id="log_printout">
                            <li>Cet espace présente la liste des termes de la recherche en cours
                                dans la cartographie. Inscrire un terme de recherche pour débuter.</li>
                        </ul>
                        <p style="position:absolute; bottom:10px">
                            <img src="${urls.base}/images/visualization/capabilitymap/key.png" alt="Key">
                        </p>
                    </div>
                    <div class="result_section" id="logg">
                        <div id="inner-details">
                            <p>
                                Cet espace affiche de l'information relative 
                                aux termes et groupes de termes. Cliquer sur un groupe 
                                pour afficher les informations qui y sont associées.
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

