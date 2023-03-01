#!/bin/bash

set -e

printenv

# allow easier debugging with `docker run -e VERBOSE=yes`
if [[ "$VERBOSE" = "yes" ]]; then
  set -x
fi

# allow easier reset home with `docker run -e RESET_HOME=true`
if [[ "$RESET_HOME" = "true" ]]; then
  echo 'Clearing VIVO HOME $VIVO_HOME_DIR'
  rm -rf $VIVO_HOME_DIR/*
fi

# ensure home config directory exists 
mkdir -p $VIVO_HOME_DIR/config

# load sample data
if [[ "$LOAD_SAMPLE_DATA" = "true" ]]; then
  echo "Cloning sample-data branch $SAMPLE_DATA_BRANCH from $SAMPLE_DATA_REPO_URL"
  git clone --branch $SAMPLE_DATA_BRANCH $SAMPLE_DATA_REPO_URL sample-data > /dev/null

  # ensure home rdf directory exists 
  mkdir -p $VIVO_HOME_DIR/rdf

  echo "Loading $SAMPLE_DATA_DIRECTORY/*"
  cp -r sample-data/$SAMPLE_DATA_DIRECTORY/* $VIVO_HOME_DIR/rdf/.
fi

# only move runtime.properties first time and if it does not already exist in target home directory
if [ -f /tmp/runtime.properties ]; then
  if [ ! -f $VIVO_HOME_DIR/config/runtime.properties ]
  then
    # template runtime.properties

    echo "Templating runtime.properties vitro.local.solr.url = $SOLR_URL"
    sed -i "s,vitro.local.solr.url = http://localhost:8983/solr/vivocore,vitro.local.solr.url = $SOLR_URL,g" /tmp/runtime.properties

    echo "Templating runtime.properties selfEditing.idMatchingProperty = $SELF_ID_MATCHING_PROPERTY"
    sed -i "s,selfEditing.idMatchingProperty = http://vivo.mydomain.edu/ns#networkId,selfEditing.idMatchingProperty = $SELF_ID_MATCHING_PROPERTY,g" /tmp/runtime.properties

    if [[ ! -z "${EMAIL_SMTP_HOST}" ]]; then
      echo "Templating runtime.properties email.smtpHost = $EMAIL_SMTP_HOST"
      sed -i "s,  # email.smtpHost = smtp.mydomain.edu,email.smtpHost = $EMAIL_SMTP_HOST,g" /tmp/runtime.properties
    fi
    if [[ ! -z "${EMAIL_PORT}" ]]; then
      echo "Templating runtime.properties email.port = $EMAIL_PORT"
      sed -i "s,  # email.port = 25 or 587,email.port = $EMAIL_PORT,g" /tmp/runtime.properties
    fi
    if [[ ! -z "${EMAIL_USERNAME}" ]]; then
      echo "Templating runtime.properties email.username = $EMAIL_USERNAME"
      sed -i "s,  # email.username = vivtroAdmin@mydomain.edu,email.username = $EMAIL_USERNAME,g" /tmp/runtime.properties
    fi
    if [[ ! -z "${EMAIL_PASSWORD}" ]]; then
      echo "Templating runtime.properties email.password = ***"
      sed -i "s,  # email.password = secret,email.password = $EMAIL_PASSWORD,g" /tmp/runtime.properties
    fi
    if [[ ! -z "${EMAIL_REPLY_TO}" ]]; then
      echo "Templating runtime.properties email.replyTo = $EMAIL_REPLY_TO"
      sed -i "s,  # email.replyTo = vitroAdmin@mydomain.edu,email.replyTo = $EMAIL_REPLY_TO,g" /tmp/runtime.properties
    fi

    if [[ ! -z "${LANGUAGE_FILTER_ENABLED}" ]]; then
      echo "Templating runtime.properties RDFService.languageFilter = $LANGUAGE_FILTER_ENABLED"
      sed -i "s,# RDFService.languageFilter = false,RDFService.languageFilter = $LANGUAGE_FILTER_ENABLED,g" /tmp/runtime.properties
    fi
    if [[ ! -z "${FORCE_LOCALE}" ]]; then
      echo "Templating runtime.properties languages.forceLocale = $FORCE_LOCALE"
      sed -i "s,# languages.forceLocale = en_US,languages.forceLocale = $FORCE_LOCALE,g" /tmp/runtime.properties
    fi
    if [[ ! -z "${SELECTABLE_LOCALES}" ]]; then
      echo "Templating runtime.properties languages.selectableLocales = $SELECTABLE_LOCALES"
      sed -i "s/# languages.selectableLocales = en_US, es_GO/languages.selectableLocales = $SELECTABLE_LOCALES/g" /tmp/runtime.properties
    fi

    echo "First time: moving /tmp/runtime.properties to /$VIVO_HOME_DIR/config/runtime.properties"
    mv -n /tmp/runtime.properties $VIVO_HOME_DIR/config/runtime.properties
  else
    echo "Using existing $VIVO_HOME_DIR/config/runtime.properties"
  fi
fi

# only move applicationSetup.n3 first time and if it does not already exist in target home directory
if [ -f /tmp/applicationSetup.n3 ]; then
  if [ ! -f $VIVO_HOME_DIR/config/applicationSetup.n3 ]
  then
    echo "First time: moving /tmp/applicationSetup.n3 to $VIVO_HOME_DIR/config/applicationSetup.n3"
    mv -n /tmp/applicationSetup.n3 $VIVO_HOME_DIR/config/applicationSetup.n3
  else
    echo "Using existing $VIVO_HOME_DIR/config/applicationSetup.n3"
  fi
fi

export JAVA_OPTS="${JAVA_OPTS} -Dvivo-dir=$VIVO_HOME_DIR -Droot-user-address=$ROOT_USER_ADDRESS -Ddefault-namespace=$DEFAULT_NAMESPACE -Dtdb:fileMode=$TDB_FILE_MODE"

echo "Giving time for Solr to startup..."
sleep 15
echo "Starting Tomcat"

catalina.sh run
