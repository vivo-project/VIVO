<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#assign aboutImagesRoot = '${urls.images}/visualization/mapofscience/about/'>

<h2>Acerca Mapa de Visualización de Ciencia VIVO</h2>
<h3>Mapa base de referencia</h3>
<p>El mapa VIVO de la visualización ciencia utiliza el mapa UCSD de la ciencia y clasificación
sistema que se calcula a partir de datos de papel a nivel de alrededor de 25 000 revistas de Elsevier
Scopus y y Thomson Reuters Web of Science (WoS) para los años 2001-2010. El mapa UCSD
de la ciencia se encarga de los 25.000 diarios a 554 subdisciplinas que se agregan más
en 13 disciplinas principales de la ciencia. En el mapa, cada disciplina tiene un color distinto
(verde para 'Biología', marrón de 'Ciencias de la Tierra', etc) y una etiqueta. (Sub) disciplinas que
son similares más cerca el uno al otro en el mapa. (Sub) disciplinas que son especialmente similares
están conectados por líneas grises.</p>

<h3>Data Overlay</h3>
<p>La actividad de publicación de una universidad, organización o persona puede superponerse a la
asignar para generar perfiles experiencia. El proceso es el siguiente: (1) El conjunto de único
se identifica revistas, (2) el número de veces que cada revista sirve como un lugar de publicación
se calcula, y (3) el tamaño del área de las 13 disciplinas y 554 es subdisciplinas
calculado sobre la base de estas publicaciones de revistas recuentos lugar. Tenga en cuenta que algunas revistas están
asociado con exactamente una disciplina (sub), mientras que otros, por ejemplo, los interdisciplinares como
<em>Ciencia</em> o <em>Nature</em>, son marginalmente asociados con múltiples
(sub) disciplinas. Subdisciplinas heredan los colores de sus disciplinas principales.
(Sub) disciplinas sin ningún tipo de publicaciones asociadas se muestran en gris.</p>

<img src="${aboutImagesRoot}/scimap_discipline.jpg" width="450" height="327" />
<img src="${aboutImagesRoot}/scimap_subdiscipline.jpg" width="450" height="327" />

<h3>Experiencia Perfil Comparación Mapa</h3>
<p>La actividad de publicación de hasta tres personas u organizaciones pueden compararse a través de "Comparar
organizaciones. "en la tabla de la izquierda, seleccione hasta tres organizaciones. La experiencia
el perfil de cada organización se muestra como datos de overlay. Todas las organizaciones es
representada en un color distinto y una lista de 10 de subdisciplinas con el mayor número
de las publicaciones es la siguiente el mapa comparación. Los datos pueden ser guardados como archivos CSV.</p>

<img src="${aboutImagesRoot}/scimap_comparison.jpg" width="803" height="781" style=
"margin-left: 50px;"/>

<h3>Interactividad</h3>
<p>El mapa puede ser explorado en dos niveles-por 13 disciplinas o subdisciplinas 554. Al hacer clic en
en un nodo en el mapa aparece el número de publicaciones en revistas fraccionada asociada
y el porcentaje de publicaciones asignadas a esta disciplina (sub). Pase el ratón sobre una disciplina
en la tabla de la izquierda para ver lo que los círculos corresponde a en el mapa. Usar control deslizante situado debajo
mapa, sobre el derecho a reducir el número de subdisciplinas demostrado mejorar la legibilidad.</p>

<h3>Enlaces</h3>
<p>Para obtener más información sobre el mapa UCSD de la ciencia y el sistema de clasificación, véase
<a href="http://sci.cns.iu.edu/ucsdmap" target="_blank">http://sci.cns.iu.edu/ucsdmap</a>.
Para otros mapas de la ciencia, ver
<a href="http://scimaps.org" target="_blank">http://scimaps.org</a> y
<a href="http://mapofscience.com" target="_blank">http://mapofscience.com</a>.</p>
