<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- START TOOLTIP TEXT -->

<div id="toolTipOne" style="display:none;">
VIVO vizuelizacija 'Map of Science' predstavlja ekspertize određenog univerziteta, organizacije ili osobe,
zasnovano na publikacijama koje se nalaze u VIVO sistemu. Ovde je prikazan profil ekspertiza za ${entityLabel} 
-- veći krugovi predstavljaju više publikacija u datoj oblasti<br /><br />

<a href='${subEntityMapOfScienceCommonURL}about'>Pročitajte više o 'Map of Science' vizuelizaciji?</a>
</div>

<div id="toolTipTwo" style="display:none;">
Tabela ispod prikazuje publikacije predstavljene na 'Map of Science' grafu. Svaki red odgovara jednoj
(pod)disciplini na mapi <br /><br />

<b>'Broj publikacija'</b> kolona prikazuje koliko publikacija je mapirano na svako od (pod)disciplina. Ovj broj
ne mora biti ceo, s obzirom da su neke publikacije povezane sa više (pod)disciplina. Takve publikacije
ne doprinose ceo broj ukupnom broju publikacija date discipline, već samo razlomak na osnovu težinske šeme.<br /><br />

Kolona <b>% aktivnosti</b> prikazuje koji procenat publikacija je mapirano na svaku od (pod)disciplina.

</div>

<div id="toolTipThree" style="display:none;">
Ova vizuelizacija je zasnovana na publikacijama koje smo mogli da nađemo za ${entityLabel}, i
zbog toga možda ne predstavlja u potpunosti sve publikacije za ${entityLabel}.<br /><br />

Procenat ukupnih publikacija koji je uključen u  ovu vizuelizaciju se može poboljšati tako što će se uneti veći
broj publikacija u VIVO sistem, i tako što će svaka publikacija biti povezana sa naučnim časopisom koji
je prepoznat od strane 'Map of Science' vizuelizacije (zasnovano na podacima iz 'Clarivate Analytics' Web of Science' i
'Elsevier's Scopus' bazama podataka). Nazivi časopisa koji imaju slovnu ili bilo koju drugu grešku treba da se preprave
pre nego što mogu da budu prepoznati tokom vizuelizacije. Ako smatrate da broj publikacija koji se nalaze na grafu ne odgovara
broju publikacija unutar VIVO sistema, kontaktirajte vašeg administratora.</div>

<div id="exploreTooltipText" style="display:none;">
     Istražite profile ekspertiza za jednu ili više organizacija. Boje su podeljene po disciplinama.
</div>

<div id="compareTooltipText" style="display:none;">
    Istražite profile ekspertiza za jednu ili više organizacija. Boje su podeljene po disciplinama.
</div>

<div id="searchInfoTooltipText" style="display:none;">
    Prikažite samo one (pod)discipline čiji naziv sadrži sledeći tekst.
</div>


<#-- COMPARISON TOOLTIP TEXT -->

<div id="comparisonToolTipTwo" style="display:none;">
Prikazane organizacije su pod-čvorovi ${entityLabel} čvora unutar organizacione hijerarhije.
Možeze prikazati više informacija o organizacijama koje se nalaze ispod neke druge pod-organizacije 
tako što ćete kliknuti na ikonicu pored imane odabrane pod-discipline, što se nalaze ispod grafa, sa
desne strane.
<br /><br />

<b>'Broj publikacija'</b> kolona prikazuje koliko publikacija je mapirano na svako od (pod)disciplina. Ovj broj
ne mora biti ceo, s obzirom da su neke publikacije povezane sa više (pod)disciplina. Takve publikacije
ne doprinose ceo broj ukupnom broju publikacija date discipline, već samo razlomak na osnovu težinske šeme.

<br /><br />
Kolona sa nazivom <b>% aktivnosti</b> prikazuje koji deo publikacija je mapiran na svaku od pod-disciplina.

</div>

<div id="comparisonSearchInfoTooltipText" style="display:none;">
    <!-- Uradite pretragu na osnovu naziva određene pod-discipline (ili discipline) u prvoj koloni tabele -->
	Prikažite samo organizacije <!--(ili osobe) --> čije ime sadrži ovaj tekst.
</div>
<#-- END TOOLTIP TEXT -->
