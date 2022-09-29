<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#assign aboutImagesRoot = '${urls.images}/visualization/mapofscience/about/'>

<h2>O VIVO-ovoj vizualizaciji "Naučne Mape"</h2>
<h3>Referenca na baznu mapu</h3>
<p>VIVO-ova vizualizacije Naučne Mape koristi UCSD naučnu mapu i klasifikacioni sistem
koji je kreiran uz pomoć papirnih podataka iz oko 25,000 primeraka časopisa "Elsevier's 
Scopus" i "Clarivate Analytics' Web of Science (WoS)" između 2001. i 2010.godine. UCSD naučna 
mapa grupiše 25,000 časopisa u 554 poddiscipline koje se dalje sumiraju u 13 glavnih naučnih
disciplina. Unutar mape, svaka disciplina ima zasebnu boju (zelena za "Biologiju", braon za "Nauke o 
Zemlju" itd.) i labelu. (Pod)discipline koje su slične se nalaze bliže jedna drugoj na mapi. 
(Pod)discpline koje su posebno slične su povezane sivom linijom </p>

<h3>Preklapanje podataka</h3>
<p>Izdavaštvo univerziteta, organizacije, ili osobe se može prikazati preko već postojeće
mape kako bi se generisali ekspertski profili. Procedura je sledeća: (1) Identifikuje se set 
jedinstvenih časopisa, (2) izračuna se koliko puta je svaki časopis bio mesto objavljivanja, 
i (3) površina 13 disciplina i 554 poddiscipline se računa na osnovu predhodno izračunatog broja 
objavljivanja u okviru nekog časopisa. Napomena da su neki časopisi povezani sa tačno jednom
(pod)disciplinom dok su neki interdisciplinarni poput časopisa <em>Science</em> ili <em>Nature</em>.
Poddiscipline nasleđuju boje svojih roditeljskih (nad)disciplina.
(Pod)discipline sa kojima nije povezano ni jedno izdavaštvo univerziteta, organizacije ili osobe
su prikazane sivom bojom.</p>

<img src="${aboutImagesRoot}/scimap_discipline.jpg" width="450" height="327" />
<img src="${aboutImagesRoot}/scimap_subdiscipline.jpg" width="450" height="327" />

<h3>Mapa poređenja ekspertskih profila</h3>
<p>Moguće je porediti publikacje dve ili tri organizacije/osobe koristeći funkcionalnost 
"Poredite organizacije." U tablei sa leve strane odaberite do 3 organizacije. Profili 
ekspertiza odabranih organizacija biće predstavljeni na istoj naučnoj mapi. Svaka organizacija
će biti predstavljena zasebnom bojom, a lista od po 10 poddisciplina iz kojih organizacije
imaju najviše publikacija će se nalaziti ispod mape. Podaci mogu biti sačuvani kao CSV file.</p>

<img src="${aboutImagesRoot}/scimap_comparison.jpg" width="803" height="781" style=
"margin-left: 50px;"/>

<h3>Interakcija sa mapom</h3>
<p>Mapu možete istraživati na dva nivoa - po 13 disciplina ili 554 poddiscipline. Klikom  
na čvor unutar mape videćete brojne povezane časopise, radove i procenat radova mapiranih
na datu (pod)disciplinu. Ako prevučete miš preko određene discipline unutar tabele sa leve
strane videće te koji joj krugovi odgovaraju na mapi. Možete koristiti slajder ispod 
mape, kako bi ste smanjili broj poddosciplina prikazan na mapi i istu učinili čitljivijom.</p>

<h3>Linkovi</h3>
<p>Za više informacija o UCSD naučnoj mapi i njihovom klasifikacionom sistemu, pogledajte 
<a href="https://doi.org/10.1371/journal.pone.0039464" target="_blank">https://doi.org/10.1371/journal.pone.0039464</a>. 
Za druge naučne mape, pogledajte
<a href="http://scimaps.org" target="_blank">http://scimaps.org</a> and 
<a href="http://mapofscience.com" target="_blank">http://mapofscience.com</a>.</p>
