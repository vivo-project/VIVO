This directory contains ontology "TBox" files with class and property definitions to be loaded by the VIVO application when it starts.  There is a companion "abox" directory that contains RDF assertions about named individuals.

The TBox and ABox are separated because VIVO caches ontology classes and properties in memory for improved performance.  The contents of this directory will be added to this cache, while the ABox data will not.

Each file in this directory corresponds to a single graph in the VIVO graph store.  For example, the contents of a file in this directory named example.owl would be loaded into graph named:

http://vitro.mannlib.cornell.edu/filegraph/tbox/example.owl

At next startup, this graph will be checked against the contents of the file in the directory.  If the two are not isomorphic, the graph in the store will be cleared and reloaded to match the current contents of the file.  If the file no longer exists in the directory, the graph will be dropped entirely.

If a file contains any syntax errors, it will not be able to be parsed and its corresponding graph will not be updated at all. The parse error will be logged in vivo.all.log.

The following file formats are supported:

RDF/XML   (.rdf or .owl)
N3        (.n3)
Turtle    (.ttl)
N-triples (.nt)

The files included by default in this directory roughly correspond to the files in the VIVO-ISF ontology source directory: https://github.com/vivo-isf/vivo-isf-ontology/tree/develop/src/ontology/source.  There are some additional files for VIVO-specific extensions and application controls, and some VIVO-ISF content not directly related to researcher networking has been excluded.

Action Items:
- [ ] identify where to document @ShahimEssaid recommendations regarding best practices for local ontology extensions:
 * Don't create without discussing use case with vivo ontology mailing list on sourceforge -- new classes and new properties
 * Don't re-use existing vocab to VIVO-ISF mailing list
