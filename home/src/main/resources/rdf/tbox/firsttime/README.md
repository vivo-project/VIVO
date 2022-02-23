This directory contains ontology "TBox" files with class and property 
definitions or annotations that are intended to be editable in the VIVO GUI.

These files are loaded by the VIVO application when it starts for the first time
and during later restarts if the contents have changed.  A triple is updated
if there is no conflicting value for the same subject and predicate that was
added to the triple store via the GUI or data ingest (e.g. SPARQL UPDATE).

The file vitroAnnotations.n3 contains triples with predicates in the vitro
namespace and objects that are not literals with language tags.

The VIVO-languages project contains additional language-specific 
vitroAnnotations.n3 files where all of the triples contain language-tagged
literals.

VIVO-languages also provides additional annotation files 
(e.g. initialTBoxAnnotations_en_US.n3) containing triples with predicates
in ontologies/voabularies outside the vitro namespace (e.g. rdfs:label).

See ../filegraph/README.md for more information about "TBox" files.
