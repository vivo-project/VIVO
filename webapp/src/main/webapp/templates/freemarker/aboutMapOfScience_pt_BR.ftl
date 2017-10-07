<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign aboutImagesRoot = '${urls.images}/visualization/mapofscience/about/'>

<h2> Sobre Visualização do Vivo Mapa da Ciência </h2>
<h3> Referência Basemap </h3>
<P> A visualização do VIVO Mapa da Ciência usamos o mapa UCSD da ciência e da classificação do
sistema que foi calculado usando dados de paper-levela partir de cerca de 25.000 revistas da Elsevier de
Scopus and Web, além da Thomson Reuters 'of Science (WoS) dos anos de 2001-2010. O mapa UCSD
da ciência atribui os 25.000 revistas para 554 subdisciplinas que estão agregadas principalmente
em 13 disciplinas da ciência. No mapa, cada disciplina tem uma cor distinta
(Verde para 'Biologia', marrom para "Ciências da Terra", etc.) e um rótulo. (SUB)disciplinas que
são mais semelhança umas as outras no mapa. (Sub)disciplinas que são especialmente semelhante
são ligados por linhas cinzentas. </p>

<h3> Dados Oprimidos </h3>
<P> Publicação de uma universidade, organização ou pessoa podem ser sobrepostos para 
gerar perfis especificos. O processo é o seguinte: (1) Um conjunto único de
revistas é identificado, (2) o número de vezes que cada revista serviu como publicação será calculado,
e (3) O tamanho da área das disciplinas 13 e das 554 subdisciplinas são calculados
com base nessas contagens de publicação da revista. Note-se que algumas revistas são
associada exatamente com (sub)disciplina de outros, por exemplo, aqueles interdisciplinares como
<Em>Science</em> ou <em>Nature</em>, são fracos associada a múltiplas
(sub)disciplinas. Subdisciplines herdar as cores de suas disciplinas mãe.
(SUB)disciplinas sem quaisquer publicações associadas são dadas em cinza.</P>

<img src="${aboutImagesRoot}/scimap_discipline.jpg" width="450" height="327" />
<img src="${aboutImagesRoot}/scimap_subdiscipline.jpg" width="450" height="327" />

<h3> Mapa da Comparação do Conhecimento dos Perfis </h3>
<P> Publicação de até três organizações ou pessoas podem ser comparados através de "Comparar
organizações ". Na tabela à esquerda, selecione até três organizações. A perícia
perfil de cada organizações será representado como sobreposição de dados. Cada organizações é
representada em uma cor distinta e uma lista top-10 de subdisciplinas com o maior número
de publicações é dada abaixo do mapa de comparação. Os dados podem ser salvos como arquivo CSV.</P>

<img src="${aboutImagesRoot}/scimap_comparison.jpg" width="803" height="781" style=
"margin-left: 50px;"/>

<h3> Interatividade </h3>
<P> O mapa pode ser explorado em dois níveis-por 13 disciplinas ou 554 subdisciplinas. Clicando
em um nó no mapa traz o número de publicações em revistas associadas
e a percentagem de publicações mapeadas para esta (sub)disciplina. Passe o mouse sobre uma disciplina
na tabela à esquerda para ver o que circunda corresponde a no mapa. Use controle deslizante abaixo
mapa, sobre o direito de reduzir o número de subdisciplinas mostrados para melhorar a legibilidade</p>

<h3> Links </h3>
<P> Para mais informações sobre o mapa UCSD de ciência e sistema de classificação, veja
<a href="http://sci.cns.iu.edu/ucsdmap" target="_blank">http://sci.cns.iu.edu/ucsdmap</a>.
Para outros mapas de ciência, veja
<a href="http://scimaps.org" target="_blank">http://scimaps.org</a> e 
<a href="http://mapofscience.com" target="_blank">http://mapofscience.com</a>.</p>