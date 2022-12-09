#!/bin/bash 

###################################################################
# Script Name   : popul_dummy_person.sh
# Description   :
# Args          : 
# Author       	: Michel Héon PhD
# Institution   : Université du Québec à Montréal
# Copyright     : Université du Québec à Montréal (c) 2021
# Email         : heon.michel@uqam.ca
###################################################################
source ../env.sh
for ITER in {1000..5000..2}
  do 
   (
        DATA_FILE=$(mktemp --suffix=.txt)
        ITER_1=$(($ITER+5000))
        ITER_2=$(($ITER_1+1000))
        ITER_3=$(($ITER_2+1000))
        cat << EOF > $DATA_FILE
update=
        PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX sfnc: <http://vivoweb.org/sparql/function#>
        PREFIX vivo: <http://vivoweb.org/ontology/core#>
        PREFIX obo: <http://purl.obolibrary.org/obo/>
        INSERT DATA {
        GRAPH <http://vitro.mannlib.cornell.edu/default/vitro-kb-2> {
        <http://localhost:8080/vivo/individual/n${ITER}>
                a                vivo:FacultyMember ;
                rdfs:label       "GN FN dummy ${ITER}"@fr-CA ;
                obo:ARG_2000028  <http://localhost:8080/vivo/individual/n${ITER_1}> .
        
        <http://localhost:8080/vivo/individual/n${ITER_1}>
                a                vcard:Individual ;
                obo:ARG_2000029  <http://localhost:8080/vivo/individual/n${ITER}> ;
                vcard:hasName    <http://localhost:8080/vivo/individual/n${ITER_2}> ;
                vcard:hasTitle   <http://localhost:8080/vivo/individual/n${ITER_3}> .
        
        <http://localhost:8080/vivo/individual/n${ITER_3}>
                a            vcard:Title ;
                vcard:title  "Chercheur"@fr-CA .
        
        <http://localhost:8080/vivo/individual/n${ITER_2}>
                a                 vcard:Name ;
                vcard:familyName  "fn_${ITER}"@fr-CA ;
                vcard:givenName   "dummy_${ITER}"@fr-CA .
        }}
EOF
        echo "send user (${ITER}/5000) 'GN FN dummy ${ITER}' at http://localhost:8080/vivo/individual/n${ITER}"
        curl -s -i -d "email=$USERNAME" -d "password=$PASSWD" -d "@$DATA_FILE" 'http://localhost:8080/vivo/api/sparqlUpdate' 
        rm $DATA_FILE
    ) &
   #
   # Parallel processing
   #
   if ! (($ITER % 20))
   then
      echo "WAIT" ;  wait; 
   else
       sleep 0.4
   fi
 done
 echo "DONE!"
