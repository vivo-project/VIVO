This directory contains "ABox" files with with RDF assertions about named individuals to be loaded by the VIVO application when it starts.  There is a companion "tbox" directory that contains ontology class and property definitions.

The TBox and ABox are separated because VIVO caches ontology classes and properties in memory for improved performance.  The contents of this directory will not be added to the cache.

Each file in this directory corresponds to a single graph in the VIVO graph store.  For example, the contents of a file in this directory named example.owl would be loaded into graph named:

http://vitro.mannlib.cornell.edu/filegraph/abox/example.owl

At next startup, this graph will be checked against the contents of the file in the directory.  If the two are not isomorphic, the graph in the store will be cleared and reloaded to match the current contents of the file.  If the file no longer exists in the directory, the graph will be dropped entirely.

If a file contains any syntax errors, it will not be able to be parsed and its corresponding graph will not be updated at all.  The parse error will be logged in vivo.all.log.

The following file formats are supported:

RDF/XML   (.rdf or .owl)
N3        (.n3)
Turtle    (.ttl)
N-triples (.nt)
