Contains data that is specific to both a particular site and a particular distribution of VIVO, 
so subdirectories might be named "scripps1.3" or "indiana1.4"

Each subdirectory contains a VIVO home directory called "data", and the file storage 
area should be populated with appropriate images so the tests won't throw errors if the
data-model is expecting an image file.

Of course, the Solr index for each "version" will be stored in its home directory.

The version directory is also a place to capture the tomcat logs, so we can inspect them later.

