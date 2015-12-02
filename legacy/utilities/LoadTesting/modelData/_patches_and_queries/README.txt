Contains:

Patches - used to modify the model to make it ready for testing.
  userAccountsPatch.n3
    Ingest this into the user-accounts model. It contains RDF for testAdmin@mydomain.edu
    and selfEditor@mydomain.edu. Each has logged in previously, and each has the
    password of "Password".

Queries - used to extract data from the model, for the tests to use.
  get_info_resource_uris.sparql
    Execute this SPARQL query and get the results in CSV. Remove any
    URIs that are not in the default namespace, and then remove the default namespace
    from each remaining URI. save as infoResourceUris.csv in the 
    modelData/[sitename] directory.
  get_person_uris.sparql
    Like the previous, but saved as personUris.csv
  image_file_query.sparql
    Save the results without alteration as modelData/[sitename]/imageFileInfo.csv, to 
    be processed by the _fakeUploadedFiles script.
