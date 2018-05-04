<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign aboutImagesRoot = '${urls.images}/visualization/mapofscience/about/'>

<h2>Über VIVOs Map of Science</h2>
<h3>Referenz  der Basiskarte</h3>

<p>Die VIVO Map of Science (Wissenschaftskarte) verwendet die Map of Science und das Klassifikationssystems des UCSD, 
die unter Verwendung von artikelbezogenen Daten aus etwa 25.000 Zeitschriften von Elseviers Scopus 
und Thomson Reuters' Web of Science (WoS) für die Jahre 2001-2010 berechnet wurde. 
Die UCSD-Wissenschaftskarte ordnet die 25.000 Zeitschriften 554 Teildisziplinen zu, die in 
13 Hauptwissenschaften zusammengefasst sind. In der Karte hat jede Disziplin eine eigene Farbe 
(grün für 'Biologie', braun für 'Geowissenschaften' usw.) und ein Label. (Unter-)Disziplinen, 
die sich ähneln, liegen auf der Karte nahe beieinander. (Teil-)Disziplinen, die sich besonders 
ähnlich sind, sind durch graue Linien verbunden.</p>

<h3>Daten-Overlay</h3>
<p>Die Publikationstätigkeit einer Universität, Organisation oder Person kann auf der Karte überlagert werden, um Kompetenzprofile zu erstellen. Der Ablauf ist wie folgt: (1) Die Menge der einzelnen Zeitschriften wird identifiziert, (2) die Anzahl, die jede Zeitschrift als Veröffentlichungsort diente, wird berechnet, und (3) die Flächengröße der 13 Disziplinen und 554 Teildisziplinen wird auf der Grundlage dieser Zeitschriftenveröffentlichungsorte berechnet. Beachten Sie, dass einige Zeitschriften genau einer (Sub-)Disziplin zugeordnet sind, während andere, z.B. interdisziplinäre Zeitschriften wie <em>Science</em> oder <em>Nature</em>, mit mehreren (Sub-)Disziplinen verbunden sind. Unterdisziplinen erben die Farben ihrer übergeordneten Disziplinen. (Teil-)Disziplinen ohne zugehörige Publikationen sind grau dargestellt.</p>

<img src="${aboutImagesRoot}/scimap_discipline.jpg" width="450" height="327" />
<img src="${aboutImagesRoot}/scimap_subdiscipline.jpg" width="450" height="327" />

<h3>Karte zum Vergleich von Kompetenzprofilen</h3>
<p>Die Publikationstätigkeit von bis zu drei Organisationen oder Personen kann über "Organisationen vergleichen" verglichen werden. Wählen Sie in der Tabelle links bis zu drei Organisationen aus. Das Kompetenzprofil jeder Organisation wird als Daten-Overlay dargestellt. Jede Organisation ist in einer eigenen Farbe dargestellt und eine Top-10-Liste der Subdisziplinen mit der höchsten Anzahl an Publikationen ist unterhalb der Vergleichskarte aufgeführt. Die Daten können als CSV-Datei gespeichert werden.</p>

<img src="${aboutImagesRoot}/scimap_comparison.jpg" width="803" height="781" style=
"margin-left: 50px;"/>

<h3>Interaktivität</h3>
<p>Die Karte kann auf zwei Ebenen erkundet werden - über 13 Disziplinen oder 554 Unterdisziplinen. Ein Klick auf einen Knoten in der Karte zeigt die Anzahl der teilweise assoziierten Zeitschriftenpublikationen und den Prozentsatz der Publikationen, die dieser (Teil-)Disziplin zugeordnet sind. Fahren Sie mit der Maus über eine Disziplin in der Tabelle links, um zu sehen, welchen Kreisen sie auf der Karte entspricht. Benutzen Sie den Schieberegler unter der Karte rechts, um die Anzahl der angezeigten Teildisziplinen zu verringern, um die Lesbarkeit zu verbessern.</p>

<h3>Links</h3>
<p>Weitere Informationen zur Wissenschaftskarte und des Klassifikationssystems der UCSD finden Sie unter 
<a href="http://sci.cns.iu.edu/ucsdmap" target="_blank">http://sci.cns.iu.edu/ucsdmap</a>. 
Für weitere Wissenschaftskarten siehe
<a href="http://scimaps.org" target="_blank">http://scimaps.org</a> und 
<a href="http://mapofscience.com" target="_blank">http://mapofscience.com</a>.</p>
