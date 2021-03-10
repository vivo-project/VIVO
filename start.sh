#!/bin/bash

set -e

# allow easier debugging with `docker run -e VERBOSE=yes`
if [[ "$VERBOSE" = "yes" ]]; then
  set -x
fi

# allow easier reset home with `docker run -e RESET_HOME=true`
if [[ "$RESET_HOME" = "true" ]]; then
  echo 'Clearing VIVO HOME /usr/local/vivo/home'
  rm -rf /usr/local/vivo/home/*
fi

# copy home bin if not exists
if [ ! -d /usr/local/vivo/home/bin ]; then
  echo "Copying home bin directory to /usr/local/vivo/home/bin"
  cp -r /vivo-home/bin /usr/local/vivo/home/bin
fi

# copy home config if not exists
if [ ! -d /usr/local/vivo/home/config ]; then
  echo "Copying home config directory to /usr/local/vivo/home/config"
  cp -r /vivo-home/config /usr/local/vivo/home/config
fi

# copy home rdf if not exists
if [ ! -d /usr/local/vivo/home/rdf ]; then
  echo "Copying home rdf directory to /usr/local/vivo/home/rdf"
  cp -r /vivo-home/rdf /usr/local/vivo/home/rdf
fi

# copy runtime.properties if it does not already exist in target home directory
if [ -f /usr/local/vivo/home/config/example.runtime.properties ]; then
  if [ ! -f /usr/local/vivo/home/config/runtime.properties ]
  then
    echo "Copying example.runtime.properties to /usr/local/vivo/home/config/runtime.properties"
    cp /usr/local/vivo/home/config/example.runtime.properties /usr/local/vivo/home/config/runtime.properties

    # template runtime.properties vitro.local.solr.url value to $SOLR_URL value
    echo "Templating runtime.properties vitro.local.solr.url = $SOLR_URL"
    sed -i "s,http://localhost:8983/solr/vivocore,$SOLR_URL,g" /usr/local/vivo/home/config/runtime.properties
  else
    echo "Using existing /usr/local/vivo/home/config/runtime.properties"
  fi
fi

# copy applicationSetup.n3 if it does not already exist in target home directory
if [ -f /usr/local/vivo/home/config/example.applicationSetup.n3 ]; then
  if [ ! -f /usr/local/vivo/home/config/applicationSetup.n3 ]
  then
    echo "Copying example.applicationSetup.n3 to /usr/local/vivo/home/config/applicationSetup.n3"
    cp /usr/local/vivo/home/config/example.applicationSetup.n3 /usr/local/vivo/home/config/applicationSetup.n3
  else
    echo "Using existing /usr/local/vivo/home/config/applicationSetup.n3"
  fi
fi

catalina.sh run
