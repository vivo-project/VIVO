#!/bin/bash

set -e

# allow easier debugging with `docker run -e VERBOSE=yes`
if [[ "$VERBOSE" = "yes" ]]; then
  set -x
fi

# allow easier reset core with `docker run -e RESET_CORE=true`
if [[ "$RESET_CORE" = "true" ]]; then
  echo 'Removing core /opt/solr/server/solr/mycores/vivocore'
  rm -rf /opt/solr/server/solr/mycores/vivocore
fi

if [ ! -f "/opt/solr/server/solr/mycores/vivocore/core.properties" ]; then
  start-local-solr
  solr create -c vivocore -d "/opt/solr/server/solr/configsets/vivocore" -p 8983
  stop-local-solr
  mv "/opt/solr/server/solr/vivocore" /opt/solr/server/solr/mycores/
else
  echo "vivocore collection already exists";
fi

exec solr -f
