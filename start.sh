#!/bin/bash

set -e

# allow easier debugging with `docker run -e VERBOSE=yes`
if [[ "$VERBOSE" = "yes" ]]; then
  set -x
fi

# allow easier reset home with `docker run -e RESET_HOME=true`
if [[ "$RESET_HOME" = "true" ]]; then
  echo 'Clearing VIVO HOME $VIVO_DIR'
  rm -rf $VIVO_DIR/*
fi

# ensure home config directory exists
mkdir -p $VIVO_DIR/config

# generate digest.md5 for existing VIVO home if not already exist
if [ ! -f $VIVO_DIR/digest.md5 ]; then
  find $VIVO_DIR -type f | grep -E "^$VIVO_DIR/bin/|^$VIVO_DIR/config/|^$VIVO_DIR/rdf/" | xargs md5sum > $VIVO_DIR/digest.md5
  echo "Generated digest.md5 for VIVO home"
  cat $VIVO_DIR/digest.md5
fi

# only move runtime.properties first time and if it does not already exist in target home directory
if [ -f /runtime.properties ]; then
  # template runtime.properties vitro.local.solr.url value to $SOLR_URL value
  echo "Templating runtime.properties vitro.local.solr.url = $SOLR_URL"
  sed -i "s,http://localhost:8983/solr/vivocore,$SOLR_URL,g" /runtime.properties

  if [ ! -f $VIVO_DIR/config/runtime.properties ]
  then
    echo "First time: moving /runtime.properties to $VIVO_DIR/config/runtime.properties"
    mv -n /runtime.properties $VIVO_DIR/config/runtime.properties
  else
    echo "Using existing $VIVO_DIR/config/runtime.properties"
  fi
fi

# only move applicationSetup.n3 first time and if it does not already exist in target home directory
if [ -f /applicationSetup.n3 ]; then
  if [ ! -f $VIVO_DIR/config/applicationSetup.n3 ]
  then
    echo "First time: moving /applicationSetup.n3 to $VIVO_DIR/config/applicationSetup.n3"
    mv -n /applicationSetup.n3 $VIVO_DIR/config/applicationSetup.n3
  else
    echo "Using existing $VIVO_DIR/config/applicationSetup.n3"
  fi
fi

echo "Giving time for Solr to startup..."
sleep 15
echo "Starting Tomcat"

catalina.sh run
