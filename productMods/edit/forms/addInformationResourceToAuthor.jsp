<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding an information resource to an author

Classes: 
foaf:Person - the individual being edited
core:Authorship - primary new individual being created
core:InformationResource - new or existing individual being linked to 

Data properties of Authorship:
core:authorRank

Object properties (domain : range)

core:informationResourceInAuthorship (InformationResource : Authorship) 
core:linkedInformationResource (Authorship : InformationResource) - inverse of informationResourceInAuthorship

core:linkedAuthor (Authorship : Person) 
core:authorInAuthorship (Person : Authorship) - inverse of linkedAuthor

--%>

hello world