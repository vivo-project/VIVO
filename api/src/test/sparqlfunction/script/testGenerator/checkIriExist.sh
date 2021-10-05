#!/bin/bash 

###################################################################
# Script Name   : testNewIRI.sh
# Description   : This script is useful to evaluate if the iri 
#    generation tool is working properly. After running the 
#    population script, the test script offers IRIs that are not 
#    in the TripleStore. Normally, there should be no IRIs with 
#    an even numbered pragma between 1000 and 12000
# Args          : 
# Author       	: Michel Héon PhD
# Institution   : Université du Québec à Montréal
# Copyright     : Université du Québec à Montréal (c) 2021
# Email         : heon.michel@uqam.ca
###################################################################
source ../env.sh
# Check 10 times
for IRI_NO in {1000..2000} 
do
    cat << EOF > checkIRI.rq
query=
    PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> 
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
    PREFIX sfnc: <http://vivoweb.org/sparql/function#> 
    PREFIX vivo: <http://vivoweb.org/ontology/core#> 
    PREFIX obo: <http://purl.obolibrary.org/obo/>
    select * 
    
    WHERE {
        BIND (IRI("http://localhost:8080/vivo/individual/n$IRI_NO") as ?IRI) .
        ?IRI sfnc:isIriExist ?isExist .
    } 
EOF
    curl -i -d "email=$USERNAME" -d "password=$PASSWD" -d '@checkIRI.rq'  -H 'Accept: text/plain' 'http://localhost:8080/vivo/api/sparqlQuery'
done
