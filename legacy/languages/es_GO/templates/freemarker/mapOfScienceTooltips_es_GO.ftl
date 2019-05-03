<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- START TOOLTIP TEXT -->

<div id="toolTipOne" style="display:none;">
Mapa de la visualización de la Ciencia de VIVO representa la experiencia tópica una universidad, organización o persona tiene
sobre la base de las publicaciones anteriores cargados en vivo. Aquí se muestra el perfil de especialización de la ${entityLabel} -- grande
tamaños círculo denotan más publicaciones por área temática.<br /><br />

<a href='${subEntityMapOfScienceCommonURL}about'>Obtenga más información sobre mapas de visualización Ciencias de VIVO</a>
</div>

<div id="toolTipTwo" style="display:none;">
La siguiente tabla resume las publicaciones representan en el mapa de la Ciencia. Cada fila corresponde a un
(sub) disciplina en el mapa.<br /><br />

El número de la columna de publicaciones muestra cómo muchas de las publicaciones se asigna a cada disciplina (sub). Esta cuenta puede ser fraccionada debido a que algunos lugares de publicación están asociados con más de una disciplina (sub). Cada publicación de tal lugar contribuye fraccionadamente a todas las disciplinas asociadas (sub) de acuerdo con un esquema de ponderación.<br /><br />

El porcentaje de la columna de actividad muestra qué proporción de las publicaciones se asigna a cada disciplina (sub).

</div>

<div id="toolTipThree" style="display:none;">
Esta visualización se basa en las publicaciones que hemos podido "ciencia locate 'para ${entityLabel}, y por lo tanto, puede que no sea plenamente representativo de la actividad general de publicaciones de ${entityLabel}.<br /><br />

La cobertura de la publicación de esta visualización se puede mejorar mediante la inclusión de más datos de publicación en el VIVO sistema, y garantizando que cada publicación en el sistema in vivo se asocia con un diario que el Mapa de La ciencia reconoce (basado en la tenencia de la base de datos ISI de Thomson y de la base de datos Scopus de Elsevier). revista nombres que contengan errores tipográficos u otras idiosincrasias pueden necesitar ser limpiado antes de ser reconocidos. Usted puede ponerse en contacto con un administrador de sistema in vivo si la cobertura publicación es una preocupación.</div>

<div id="exploreTooltipText" style="display:none;">
	Superposición y examinar los perfiles de competencias para una organización. El código de colores por la disciplina.
</div>

<div id="compareTooltipText" style="display:none;">
	Superposición y examinar los perfiles de competencias de una o más organizaciones. El código de colores según la organización.
</div>

<div id="searchInfoTooltipText" style="display:none;">
	Enumere sólo disciplinas (sub) cuyos nombres contengan el texto.
</div>


<#-- COMPARISON TOOLTIP TEXT -->

<div id="comparisonToolTipTwo" style="display:none;">
Las organizaciones mencionadas son hijos del nodo de ${entityLabel} en la jerarquía organizacional. Es posible que 'profundizar' para ver las organizaciones por debajo de un determinado sub-organización, seleccione el icono de gráfico junto al nombre de un sub-organización seleccionada por debajo de la gráfica de la derecha.
<br /><br />

El número de publicaciones columna muestra la cantidad de las publicaciones fueron asignadas a cada especialidad. Esta cuenta puede ser fraccionario ya que algunos lugares de publicación están asociados con más de una especialidad. Cada publicación de tal lugar contribuye fraccionadamente a todas las subdisciplinas asociados de acuerdo con un esquema de ponderación.
<br /><br />

El porcentaje de la columna de actividad muestra qué proporción de las publicaciones que se asigna a cada especialidad.

</div>

<div id="comparisonSearchInfoTooltipText" style="display:none;">
	<!-- Search for specific subdiscipline (or discipline) label in the first column of the table. -->
	Listar sólo las organizaciones cuyo nombre contenga el texto.
</div>
<#-- END TOOLTIP TEXT -->
