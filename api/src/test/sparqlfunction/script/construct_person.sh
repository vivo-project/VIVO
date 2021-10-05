#!/bin/bash 

###################################################################
# Script Name   :
# Description   :
# Args          : 
# Author       	: Michel Héon PhD
# Institution   : Université du Québec à Montréal
# Copyright     : Université du Québec à Montréal (c) 2021
# Email         : heon.michel@uqam.ca
###################################################################
source env.sh

cat << EOF | curl -d "email=$USERNAME" -d "password=$PASSWD" -d @-  -H 'Accept: text/n3' 'http://localhost:8080/vivo/api/sparqlQuery'
query=
PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> 
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
PREFIX sfnc: <http://vivoweb.org/sparql/function#> 
PREFIX vivo: <http://vivoweb.org/ontology/core#> 
PREFIX obo: <http://purl.obolibrary.org/obo/> 
CONSTRUCT  { 
?vivoIndv a vivo:FacultyMember ; 
     rdfs:label ?name ; 
     obo:ARG_2000028  ?vcardIndv . 
?vcardIndv a vcard:Individual ; 
     obo:ARG_2000029 ?vivoIndv ; 
     vcard:hasName ?vcardHasName  ; 
     vcard:hasTitle ?vcardHasTitle . 
?vcardHasName a vcard:Name ; 
     vcard:familyName ?familyName ; 
     vcard:givenName ?givenName .     
?vcardHasTitle a vcard:Title ; 
     vcard:title  ?title . 
  }  WHERE { 
    BIND ("Michel"@fr-CA as ?givenName) . 
    BIND ("Héon"@fr-CA as ?familyName) . 
    BIND ("Chercheur"@fr-CA as ?title) . 
    BIND (strlang(concat(?givenName, " ", ?familyName),"fr-CA") as ?name) . 
    <http://localhost:8080/vivo/individual/n> sfnc:hasNewIRI ?vivoIndv ; 
        sfnc:hasNewIRI ?vcardIndv ; 
        sfnc:hasNewIRI ?vcardHasName ; 
        sfnc:hasNewIRI ?vcardHasTitle .   
} 

EOF

 
