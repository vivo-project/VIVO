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

# ensure home config directory exists
mkdir -p /opt/vivo/home/config

# only move runtime.properties first time and if it does not already exist in target home directory
if [ -f /runtime.properties ]
then

  # template runtime.properties vitro.local.solr.url value to $SOLR_URL value
  echo "Templating runtime.properties vitro.local.solr.url = $SOLR_URL"
  sed -i "s,http://localhost:8983/solr/vivocore,$SOLR_URL,g" /runtime.properties

  if [ ! -f /opt/vivo/home/config/runtime.properties ]
  then
    echo "First time: moving /runtime.properties to /opt/vivo/home/config/runtime.properties"
    mv -n /runtime.properties /opt/vivo/home/config/runtime.properties
  else
    echo "Using existing /opt/vivo/home/config/runtime.properties"
  fi
fi

# only move applicationSetup.n3 first time and if it does not already exist in target home directory
if [ -f /applicationSetup.n3 ]
then
  if [ ! -f /opt/vivo/home/config/applicationSetup.n3 ]
  then
    echo "First time: moving /applicationSetup.n3 to /opt/vivo/home/config/applicationSetup.n3"
    mv -n /applicationSetup.n3 /opt/vivo/home/config/applicationSetup.n3
  else
    echo "Using existing /opt/vivo/home/config/applicationSetup.n3"
  fi
fi

catalina.sh run
