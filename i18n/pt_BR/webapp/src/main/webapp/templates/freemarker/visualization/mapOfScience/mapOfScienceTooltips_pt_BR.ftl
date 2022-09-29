<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- START TOOLTIP TEXT -->

<div id = style "toolTipOne" = "display: none;">
VIVO Mapa de visualização Ciêntifica retrata a experiência de uma universidade, organização ou pessoa tem
com base na últimas publicações carregadas no VIVO. Aqui é mostrado o perfil de especialização da ${entityLabel}--largo círculos denotam mais as publicações por área temática. <br /> <br />

<a href='${subEntityMapOfScienceCommonURL}about'> Saiba mais sobre VIVO Mapas de visualização Ciêntifica? </a>
</div>

<div id="toolTipTwo" style="display:none;">
A tabela abaixo resume as publicações plotadas no Mapa da Ciência. Cada linha corresponde a uma
(Sub)disciplina no mapa <br /> <br />

A coluna <b># de pubs.</b> mostra quantas das publicações foram mapeadas para cada (sub)disciplina. Esta contagem pode ser fraccionada, porque alguns publicação estão associadas a mais do que uma (sub)disciplina. Cada publicação
em tal local contribui fração de todas as (sub)disciplinas associadas de acordo com um sistema de ponderação.<br /> <br />

A coluna <b>% of activity </b> mostra que proporção das publicações foram mapeados para cada (sub)disciplina.

</div>

<div id="toolTipThree" style="display:none;">
Esta visualização é baseada nas publicações, fomos capazes de 'localizar ciência' para ${entityLabel}, e
portanto, não pode ser plenamente representativo a atividade de publicação global para ${entityLabel}. <br /> <br />

A cobertura da visualização desta publicação pode ser melhorados através da inclusão de mais dados na publicação VIVO
sistema, e por garantir que cada publicação no sistema dentro VIVO é associada com um jornal que o Mapa de
Ciência reconheca (com base nas participações do banco de dados da Clarivate Analytics Web of Science e banco de dados Scopus da Elsevier).  
Os nomes dos jornais que contenham erros de digitação podem precisar de ser revisados antes de serem reconhecidos. 
Você pode entrar em contato com o administrador do sistema VIVO se a cobertura da publicação é uma preocupação.</div>

<div id="exploreTooltipText" style="display:none;">
	Overlay e examine o pergil para uma organização. Cor definida para disciplina.
</div>

<div id="compareTooltipText" style="display:none;">
	Overlay e examine o pergil para uma ou mais organizações. Cor definida para organização.
</div>

<div id="searchInfoTooltipText" style="display:none;">
	Lista única de (sub)disciplinas cuja os nomes contenham este texto.
</div>


<#-- COMPARISON TOOLTIP TEXT -->

<div id="comparisonToolTipTwo" style="display:none;">
As organizações listadas são filhos do nó ${entityLabel} na hierarquia organizacional.
Você pode 'drill down' para ver as organizações abaixo de um determinado sub-organização, selecionando o ícone gráfico
ao lado do nome de uma sub-organização selecionada abaixo do gráfico à direita.
<br /> <br />

A coluna <b># de pubs.</b> mostra quantos dos publicações foram mapeados para cada especialidade. Este
contagem pode ser fraccionada porque alguns publicação associadas a mais do que um subcampo.
Cada publicação em tal local contribui fragmentação de todas as sub-disciplinas associadas de acordo com a
um sistema de ponderação.

<br /> <br />
A coluna <b>% da atividade</b> mostra que proporção das publicações foram mapeados para cada especialidade.

</div>

<div id="comparisonSearchInfoTooltipText" style="display:none;">
	<!-- Search for specific subdiscipline (or discipline) label in the first column of the table. -->
	Listar apenas organizações <!--(or people) --> cujo nome contém este texto.
</div>
<# - END TOOLTIP TEXTO ->
