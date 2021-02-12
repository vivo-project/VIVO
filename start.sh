#!/bin/bash

set -e

# allow easier debugging with `docker run -e VERBOSE=yes`
if [[ "$VERBOSE" = "yes" ]]; then
  set -x
fi

# allow easier reset home with `docker run -e RESET_HOME=true`
if [[ "$RESET_HOME" = "true" ]]; then
  echo 'Clearing VIVO HOME /opt/vivo/home'
  rm -rf /opt/vivo/home/*
fi

echo "Solr URL: $1"

sed -i "s,http://localhost:8983/solr/vivocore,$1,g" /runtime.properties

mkdir -p /opt/vivo/home/config

mv -n /runtime.properties /opt/vivo/home/config/runtime.properties
mv -n /applicationSetup.n3 /opt/vivo/home/config/applicationSetup.n3

catalina.sh run
