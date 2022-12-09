
#!/bin/bash 

###################################################################
# Script Name   : populate_vivo.sh
# Description   : Insert new person to VIVO
# Args          : 
# Author       	: Michel Héon PhD
# Institution   : Université du Québec à Montréal
# Copyright     : Université du Québec à Montréal (c) 2021
# Email         : heon.michel@uqam.ca
###################################################################
source env.sh
cat << EOF | cat - newUser.ttl | grep -v '@prefix' | sed  '$a}}' > userToInsert.rq
update=
PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX sfnc: <http://vivoweb.org/sparql/function#>
PREFIX vivo: <http://vivoweb.org/ontology/core#>
PREFIX obo: <http://purl.obolibrary.org/obo/>
INSERT DATA {
   GRAPH <http://vitro.mannlib.cornell.edu/default/vitro-kb-2> {
EOF

curl -i -d "email=$USERNAME" -d "password=$PASSWD" -d '@userToInsert.rq' 'http://localhost:8080/vivo/api/sparqlUpdate'