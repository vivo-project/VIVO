---
title: 'VIVO: a system for research discovery'
tags:
  - ontology
  - semantic-web
  - linked-data
  - research-discovery
  - open-scholarship
  - linked-open-data
  - open-science
  - Java
authors:
 - name: Michael Conlon
   orcid: 0000-0002-1304-8447
   affiliation: 1
 - name: Andrew Woods
   orcid: 0000-0002-8318-4225
   affiliation: 2
 - name: Graham Triggs
   orcid: 0000-0001-8530-8917
   affiliation: "2, 3"
 - name: Ralph O'Flinn
   orcid: 0000-0003-4002-1335
   affiliation: 5
 - name: Muhammad Javed
   orcid: 0000-0001-9770-7640
   affiliation: 4
 - name: Jim Blake
   orcid: 0000-0002-2487-2554
   affiliation: 4
 - name: Benjamin Gross
   orcid: 0000-0002-7908-1987
   affiliation: "9, 10"
 - name: Qazi Azim Ijaz Ahmad
   orcid: 0000-0001-8959-6370
   affiliation: 3
 - name: Sabih Ali
   orcid: 0000-0002-7140-8754
   affiliation: 6
 - name: Martin Barber
   orcid: 0000-0001-7924-0741
   affiliation: 3
 - name: Don Elsborg
   Orcid: 0000-0003-0100-716X
   affiliation: 7
 - name: Kitio Fofack 
   orcid: 0000-0002-6187-4436
   affiliation: 8
 - name: Christian Hauschke
   orcid: 0000-0003-2499-7741 
   affiliation: 3
 - name: Violeta Ilik
   orcid: 0000-0003-2588-3084
   affiliation: 11
 - name: Huda Khan
   orcid: 0000-0001-5156-4621
   affiliation: 4
 - name: Ted Lawless
   orcid: 0000-0002-3287-5202
   affiliation: "10, 12"
 - name: Jacob Levernier
   orcid: 0000-0003-1563-7314
   affiliation: 13
 - name: Brian Lowe
   orcid: 0000-0002-8143-6345
   affiliation: 14
 - name: Jose Luis Martin
   affiliation: 15
 - name: Steve McKay
   affiliation: 16
 - name: Simon Porter
   orcid: 0000-0002-6151-8423
   affiliation: 6
 - name: Tatiana Walther
   orcid: 0000-0001-8127-2988
   affiliation: 3
 - name: Marijane White
   Orcid: 0000-0001-5059-4132
   affiliation: 17
 - name: Stefan Wolff
   Orcid: 0000-0003-0015-9671
   affiliation: 18
 - name: Rebecca Younes
   Orcid: 0000-0002-5105-1401
   affiliation: 4
affiliations:
 - name: University of Florida, Gainesville, Florida, US
   index: 1
 - name: Duraspace, Inc., Beaverton, OR, US
   index: 2
 - name: Technische Informationsbibliothek (TIB) – German National Library of Science and Technology, Hannover, DE
   index: 3
 - name: Cornell University, Ithaca, NY, US
   index: 4
 - name: University of Alabama Birmingham, Birmingham, AL, US
   index: 5
 - name: Digital Science, London, UK
   index: 6
 - name: University of Colorado, Boulder, CO, US
   index: 7
 - name: Université du Québec à Montréal, Montréal, QC, CA
   index: 8
 - name: UNAVCO, Inc., Boulder, CO, US
   index: 9
 - name: Clarivate Analytics, Inc., Philadelphia, PA, US
   index: 10
 - name: Columbia University, New York, NY, US
   index: 11
 - name: Brown University, Providence, RI, US
   index: 12
 - name: University of Pennsylvania, Philadelphia, PA, US
   index: 13
 - name: Ontocale SRL, Bucharest, RO
   index: 14
 - name: Universidad Carlos III de Madrid, Madrid, ES
   index: 15
 - name: Plum Analytics, Inc., Philadelphia, PA, US
   index: 16
 - name: Oregon Health & Science University, Portland, OR, US
   index: 17
 - name: Sächsische Landesbibliothek Staats und Universitätsbibliothek, Dresden, DE
   index: 18
date: 19 July 2018
bibliography: paper.bib
---

# Summary

VIVO [Pronunciation: vee-voh] is member-supported, enterprise open source 
software and an ontology for representing scholarship. VIVO supports recording, editing, 
searching, browsing and visualizing scholarly activity. VIVO encourages research 
discovery, expert finding, network analysis and assessment of research impact. 
VIVO is easily extended to support additional domains of scholarly activity [@borner_vivo:_2012].

VIVO uses an ontology to represent people, papers, grants, projects, datasets, resources,
and other elements of research and scholarship as linked open data. The ontology can
be used to create RDF that can be loaded into VIVO. VIVO RDF data is easily exported for
use in other applications.

VIVO includes Vitro [@about_vitro:2018], a domain-free engine for managing linked open data, the JFact 
reasoner [@jfact_2018], SolR [@solr_2018] for search, SPARQL query [@sparql_2018], Jena as a triple store [@jena_2018], supporting both TDB [@jena_tdb_2018] 
and SDB [@jena_sdb_2018] on MySQL [@mysql_2018], uses D3 [@bostock_d3_2018] for visualizations, and provides multiple APIs, including
Triple Pattern Fragments [@verborgh_triple_2016] for rapid remote access to specified data.

Using VIVO, organizations can represent the activities and accomplishments of their 
scholars as linked open data, and share that data with others.

# Acknowledgements

The authors wish to acknowledge the foundational work done on VIVO, and VIVO concepts by 
the team at the Mann Agricultural Library, Cornell University, led by Jon Corson-Rikert. 
The authors also wish to acknowledge NIH grant 1U24RR029822-01 to the first author, which 
funded the work of more than 120 co-investigators in the further development of the 
VIVO software, and to Dr. Melissa Haendel of Oregon Health Science 
University for her significant advances in the VIVO Integrated Semantic Framework [@vivo-isf-ontology:_2018], 
which VIVO uses to represent scholarship. Finally, the authors wish to acknowledge the 
many hundreds of members of the VIVO community around the world, who volunteer their 
time and effort to advance the art of representing scholarship as linked open data. The 
work described here builds on the work of many others.

# References
