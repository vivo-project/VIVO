<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- START TOOLTIP TEXT -->

<div id="toolTipOne" style="display:none;">
VIVOs Map of Science stellt die aktuelle Expertise einer Universität, Organisation oder Person dar, die auf vergangenen Publikationen basiert, die in VIVO geladen wurden. Hier ist das ${entityLabel}-Kompetenzprofil dargestellt - größere Kreisgrößen bedeuten mehr Publikationen pro Themenbereich.<br /><br />

<a href='${subEntityMapOfScienceCommonURL}about'>Mehr über VIVOs Map of Science lernen?</a>
</div>

<div id="toolTipTwo" style="display:none;">
Die folgende Tabelle fasst die auf der Karte der Wissenschaften dargestellten Publikationen zusammen. Jede Zeile entspricht einer (Unter-)Disziplin auf der Karte.<br /><br />

Die Spalte <b># of pubs.</b> zeigt, wie viele der Publikationen auf jede (Unter-)Disziplin abgebildet wurden. Diese Zählung kann aufgeteilt sein, da einige enthaltenden Sammelwerke mit mehr als einer (Unter-)Disziplin verbunden sind. Jede Publikation an einem solchen Ort trägt nach einem Gewichtungsschema zu allen zugehörigen (Teil-)Disziplinen bei.


Die Spalte <b>% of activity</b> zeigt, welcher Anteil der Publikationen auf jede (Unter-)Disziplin abgebildet wurde.

</div>

<div id="toolTipThree" style="display:none;">
Diese Visualisierung basiert auf den Publikationen, die wir für ${entityLabel} finden konnten, 
und ist daher möglicherweise nicht vollständig repräsentativ für die gesamte Publikationstätigkeit für ${entityLabel}.<br /><br />

Der Abdeckungsgrad dieser Visualisierung kann durch die Aufnahme weiterer Publikationsdaten in das VIVO-System verbessert werden, und indem sichergestellt wird, dass jede Veröffentlichung im System mit einer Zeitschrift verknüpft ist, die die Map of Science erkennt (basierend auf den Beständen von Clarivates WoS-Datenbank und Elseviers Scopus-Datenbank). Zeitschriftentitel, die Tippfehler oder andere Eigenheiten enthalten, müssen möglicherweise bereinigt werden, bevor sie erkannt werden. Wenden Sie sich an einen Systemadministrator, wenn Sie Fragen zum Abdeckungsgrad haben.</div></div>

<div id="exploreTooltipText" style="display:none;">
	Overlay und Prüfung von Kompetenzprofilen für eine Organisation. Farbcodierung nach Disziplin.
</div>

<div id="compareTooltipText" style="display:none;">
	Overlay  und Prüfung von Kompetenzprofilen für eine oder mehrere Organisationen. Farbcodierung nach Organisation.
</div>

<div id="searchInfoTooltipText" style="display:none;">
	Nur (Teil-)Disziplinen auflisten, deren Namen diesen Text enthalten.
</div>


<#-- COMPARISON TOOLTIP TEXT -->

<div id="comparisonToolTipTwo" style="display:none;">
Die aufgeführten Organisationen sind Kinder des Knotens ${entityLabel} in der Organisationshierarchie. 
Sie können "drill down" verwenden, um die Organisationen unterhalb einer bestimmten Unterorganisation zu sehen, indem Sie das Diagrammsymbol neben dem Namen einer ausgewählten Unterorganisation unterhalb des Diagramms auf der rechten Seite auswählen.
<br /><br />

Die Spalte <b># of pubs.</b> zeigt, wie viele der Publikationen auf die einzelnen Teildisziplinen abgebildet wurden. Diese Zählung kann aufgeteilt sein, da einige Publikationsorte mit mehr als einer Teildisziplin verbunden sind. 
Jede Publikation an einem solchen Ort trägt nach einem Gewichtungsschema zu allen zugehörigen Teildisziplinen bei.<br /><br />
Die Spalte <b>% of activity</b> zeigt, welcher Anteil der Publikationen auf die einzelnen Teildisziplinen abgebildet wurde.

</div>

<div id="comparisonSearchInfoTooltipText" style="display:none;">
	<!-- Search for specific subdiscipline (or discipline) label in the first column of the table. -->
	Nur Organisationen <!--(oder Personen) --> deren Name diesen Text enthält.
</div>
<#-- END TOOLTIP TEXT -->
