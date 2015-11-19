The sites directory contains sub-directories, one for each site whose data we are testing.

Each site directory (cornell, indiana, etc.) contains information specific to that site.

In particular, it contains a site_properties.rb file, which defines the default_namespace
and the email address of the root user, for inclusion in the deploy.properties file
by _deploy script.
