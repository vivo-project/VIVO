# Summary
This directory contains a set of scripts allowing to test the gereration of IRI from a SPARQL query sent in VIVO

# To Run
1. `cp env.sh.sample env.sh`
2. Setup variables with appropriate value in `env.sh`
3. Build a set of triples with unique IRIs in the VIVO triplestore with `construct_person.sh`. it generate `newUser.ttl` file
3. run the script `populate_vivo.sh`. The script generates the file  `userToInsert.rq` which is used bu the SPARQLUpdate

# Dummy User Site Scenario
**in the testGenerator directoy**

This scenario consists in creating a triplet database composed of 2000 Individuals of type `vivo:FacultyMember` from a SPARQLUptate command. In the scenario, the IRIs needed to create each Individual are generated incrementally. 

After the population of the VIVO triplestore with `popul_dummy_person.sh` the scripts `checkIriExist.sh` and `testNewIRI.sh` allow to verify the existence of an IRI and allows to generate new IRI not existing in the triplestore.

This test case is useful to debug the SPARQLFuntion `sfnc:isIriExist` and `sfnc:hasNewIRI`

# Usage

## sfnc:isIriExist Sparql Function

This function allows you to validate the existence of an IRI in the tripleStore. 

### The Syntax 

In a statement, the function is positioned in place of the predicate. The subject indicates the IRI to be searched by the function and the object contains the result of the function which is a boolean indicating the existence of the IRI

```PREFIX sfnc: <http://vivoweb.org/sparql/function#>
 SELECT * 
 WHERE {
   BIND (IRI("http://localhost:8080/vivo/individual/n1046") as ?IRI) .
   ?IRI sfnc:isIriExist ?isExist .
}
```


## sfnc:hasNewIRI Sparql Function

This function is used to search for an IRI not used in the triplestore

### The Syntax 

In a statement, the function is placed in place of the predicate. The subject indicates the base name of the IRI to search. As for the subject, it contains the result of the function which is a complete IRI not existing in the triplestore. However, the IRI is not yet created in the tripleStore but it can be used for a subsequent creation operation

#### example 1

usage in a simple case

```PREFIX sfnc: <http://vivoweb.org/sparql/function#>
 CONSTRUCT {
    ?newIRI a vivo:FacultyMember .
 } 
 WHERE {
   "http://localhost:8080/vivo/individual/n" sfnc:hasNewIRI ?newIRI .
}
```


#### example 2 

usage for the creation in French context of a new valid faculty member individual in VIVO

**file `construct_person.sh`**
```cat << EOF | curl -d "email=$USERNAME" -d "password=$PASSWD" -d @-  -H 'Accept: text/n3' 'http://localhost:8080/vivo/api/sparqlQuery'
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
```

The 'construct' request is encapsulated by the 'cat' command and submitted to VIVO for evaluation via the 'curl' command

## Insert Data in VIVO with SPARQL Update

This individual is insert in VIVO by the following SPARQL update commands

**the update request `insert_facutyMember.sh`**

```
givenName="Michel"
familyName="Héon"
title="Chercheur"

cat << EOF | curl -s -d "email=$USERNAME" -d "password=$PASSWD" -d '@-' 'http://localhost:8080/vivo/api/sparqlUpdate'
update=
PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> 
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
PREFIX sfnc: <http://vivoweb.org/sparql/function#> 
PREFIX vivo: <http://vivoweb.org/ontology/core#> 
PREFIX obo: <http://purl.obolibrary.org/obo/> 
INSERT {
   GRAPH <> {
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
    } 
} WHERE { 
    BIND ("$givenName"@fr-CA as ?givenName) . 
    BIND ("$familyName"@fr-CA as ?familyName) . 
    BIND ("$title"@fr-CA as ?title) . 
    BIND (strlang(concat(?givenName, " ", ?familyName),"fr-CA") as ?name) . 
    <http://localhost:8080/vivo/individual/n> sfnc:hasNewIRI ?vivoIndv ; 
        sfnc:hasNewIRI ?vcardIndv ; 
        sfnc:hasNewIRI ?vcardHasName ; 
        sfnc:hasNewIRI ?vcardHasTitle .   
} 

EOF
```

In this context, the use of SPARQL function `sfnc:hasNewIRI` is useful to insert a new user in VIVO with unique identifiers to the data graph already contained in VIVO

# References
1. [VIVO Sparql Query API](https://wiki.lyrasis.org/display/VIVODOC111x/SPARQL+Query+API)  
2. [VIVO Sparwl Update API](https://wiki.lyrasis.org/display/VIVODOC111x/SPARQL+Update+API)
3. [Jena ARQ - Writing Property Functions](https://jena.apache.org/documentation/query/writing_propfuncs.html)
4. [Jena ARQ - Writing Filter Functions](https://jena.apache.org/documentation/query/writing_functions.html)
5. [TDB Transactions](https://jena.apache.org/documentation/tdb/tdb_transactions.html)
