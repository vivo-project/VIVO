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
        <h2>Mapa de Capacidade</h2>
        <p>Crie um 'primeiro passe' mapa de capacidade digitando um termo de pesquisa que poderia ser dito representar uma ampla capacidade de pesquisa.</p>
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
                Bem-vindo à ferramenta Mapeamento de Capacidade.
                Esta ferramenta visualiza como os pesquisadores se relacionam com outros
                pesquisadores através de termos de pesquisa.
            </p>
            <h3>Começando</h3>
            <p>
                Digite uma área de pesquisa no campo de pesquisa acima e pressione 'Pesquisar'.
                O diagrama resultante exibe o termo de pesquisa, renderizado em laranja,
                conectado ao grupo azul de pesquisadores que atuam nessa área.
                Digite outro termo de pesquisa para ver como os pesquisadores de ambas as pesquisas se relacionam.
                Continue adicionando termos de pesquisa para criar um mapa de capacidade.
            </p>
            <p>
                Dica: você pode expandir um longo termo de pesquisa em conceitos menores
                clicando em & lsquo; procure e expanda & rsquo ;.
            </p>
            <h3>Interagindo com a visualização</h3>
            <p>
                Ao clicar em qualquer nó na visualização,
                informações adicionais podem ser visualizadas no
                Guia 'Informações' no lado direito.
                Para grupos de pessoas, os participantes do grupo
                e suas informações podem ser vistas,
                e os pesquisadores individuais podem ser removidos do gráfico.
                A seleção de um termo de pesquisa exibirá todos os grupos anexados.
                Sob cada grupo, as informações completas para cada pessoa são recuperadas,
                e o número de bolsas e publicações correspondentes
                Para cada pesquisador dentro dos recursos mapeados é mostrado.
                Clicar no nome de um pesquisador levará à busca original
                resultados.
            </p>
            <h4>Pistas visuais</h4>
            <p>
                Para tornar a visualização mais fácil de ler,
                Os termos e grupos de pesquisa são dimensionados de acordo com
                para o número de resultados retornados.
                Os grupos também recebem tons diferentes
                de acordo com o número de termos de pesquisa conectados.
                Quanto mais escura a sombra, mais termos de pesquisa estão conectados a um grupo.
            </p>
            <h3>Características avançadas</h3>
            <h4>Alterando o valor de corte</h4>
            <p>
                A quantidade de pesquisadores recuperados para cada termo de pesquisa
                pois é limitado pelo valor de corte no formulário de pesquisa
                (10 por padrão).
                Aumentar esse ponto de corte aumentará a probabilidade
                de uma interseção entre diferentes termos de pesquisa.
                Isso também aumentará a complexidade do gráfico,
                no entanto, e pode dificultar a identificação de padrões.
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
                    <li><a href="#demo">Termos de pesquisa</a></li>
                    <li><a href="#logg">Info</a></li>
                    <!-- li><a href="#extractData">Data</a></li -->
                </ul>

                <div class="result_body">
                    <div class="result_section" id="demo">
                        <h2>Termos de pesquisa atuais</h2>
                        <ul id="log_printout">
                            <li>Este painel exibe uma lista dos termos de pesquisa atualmente
                                 no gráfico. Procure por algo para começar.</li>
                        </ul>
                        <p style="position:absolute; bottom:10px">
                            <img src="${urls.base}/images/visualization/capabilitymap/key.png" alt="Key">
                        </p>
                    </div>
                    <div class="result_section" id="logg">
                        <div id="inner-details">
                            <p>
                                Este painel exibe informações sobre o indivíduo
                                Pesquisar termos e grupos. Clique em um grupo para exibir
                                suas informações.
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

