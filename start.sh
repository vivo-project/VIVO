#!/bin/bash

set -e

# allow easier debugging with `docker run -e VERBOSE=yes`
if [[ "$VERBOSE" = "yes" ]]; then
  set -x
fi

# allow easier reset home with `docker run -e RESET_HOME=true`
if [[ "$RESET_HOME" = "true" ]]; then
  echo "Clearing VIVO HOME $VIVO_HOME"
  rm -rf "$VIVO_HOME/*"
fi

# copy home config if not exists
if [ ! -d $VIVO_HOME/config ]; then
  echo "Copying home config directory to $VIVO_HOME/config"
  cp -r /vivo-home/config "$VIVO_HOME/config"
fi

# copy runtime.properties if it does not already exist in target home directory
if [ -f "$VIVO_HOME/config/example.runtime.properties" ]; then
  if [ ! -f "$VIVO_HOME/config/runtime.properties" ]
  then
    echo "Copying example.runtime.properties to $VIVO_HOME/config/runtime.properties"
    cp "$VIVO_HOME/config/example.runtime.properties" "$VIVO_HOME/config/runtime.properties"

    # template runtime.properties vitro.local.solr.url value to $SOLR_URL value
    echo "Templating runtime.properties vitro.local.solr.url = $SOLR_URL"
    sed -i "s,http://localhost:8983/solr/vivocore,$SOLR_URL,g" "$VIVO_HOME/config/runtime.properties"
  else
    echo "Using existing $VIVO_HOME/config/runtime.properties"
  fi
fi

# copy applicationSetup.n3 if it does not already exist in target home directory
if [ -f "$VIVO_HOME/config/example.applicationSetup.n3" ]; then
  if [ ! -f "$VIVO_HOME/config/applicationSetup.n3" ]
  then
    echo "Copying example.applicationSetup.n3 to $VIVO_HOME/config/applicationSetup.n3"
    cp "$VIVO_HOME/config/example.applicationSetup.n3" "$VIVO_HOME/config/applicationSetup.n3"
  else
    echo "Using existing $VIVO_HOME/config/applicationSetup.n3"
  fi
fi

catalina.sh run
