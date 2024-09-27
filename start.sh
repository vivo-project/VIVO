#!/bin/bash

set -e

# allow easier debugging with `docker run -e VERBOSE=yes`
if [[ "$VERBOSE" = "yes" ]]; then
  set -x
fi

# allow easier reset home with `docker run -e RESET_HOME=true`
if [[ "$RESET_HOME" = "true" ]]; then
  echo 'Clearing VIVO HOME $VIVO_HOME'
  rm -rf $VIVO_HOME/*
fi

# ensure home config directory exists 
mkdir -p $VIVO_HOME/config

# generate digest.md5 for existing VIVO home if not already exist
if [ ! -f $VIVO_HOME/digest.md5 ]; then
  find $VIVO_HOME -type f | grep -E "^$VIVO_HOME/bin/|^$VIVO_HOME/config/|^$VIVO_HOME/rdf/" | xargs md5sum > $VIVO_HOME/digest.md5
  echo "Generated digest.md5 for VIVO home"
  cat $VIVO_HOME/digest.md5
fi

# load sample data
if [[ "$RESET_HOME" = "true" ]] && [[ "$LOAD_SAMPLE_DATA" = "true" ]]; then
  echo "Cloning sample-data branch $SAMPLE_DATA_BRANCH from $SAMPLE_DATA_REPO_URL"
  git clone --branch $SAMPLE_DATA_BRANCH $SAMPLE_DATA_REPO_URL sample-data > /dev/null

  # ensure home rdf directory exists 
  mkdir -p $VIVO_HOME/rdf

  echo "Loading $SAMPLE_DATA_DIRECTORY/*"
  cp -r sample-data/$SAMPLE_DATA_DIRECTORY/* $VIVO_HOME/rdf/.
fi

# only add/update runtime.properties if it does not already exist in target home directory or reconfigure env variable true
if [ ! -f $VIVO_HOME/config/runtime.properties ] || [[ "$RECONFIGURE" = "true" ]]
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

  echo "Copying /tmp/runtime.properties to /$VIVO_HOME/config/runtime.properties"
  cp -r /tmp/runtime.properties $VIVO_HOME/config/runtime.properties
else
  echo "Using existing $VIVO_HOME/config/runtime.properties"
fi

# only add/update applicationSetup.n3 if it does not already exist in target home directory or reconfigure env variable true
if [ ! -f $VIVO_HOME/config/applicationSetup.n3 ] || [[ "$RECONFIGURE" = "true" ]]
then
  echo "Copying /tmp/applicationSetup.n3 to $VIVO_HOME/config/applicationSetup.n3"
  cp -r /tmp/applicationSetup.n3 $VIVO_HOME/config/applicationSetup.n3
else
  echo "Using existing $VIVO_HOME/config/applicationSetup.n3"
fi

# Define the file path for setenv.sh
SETENV_PATH="$CATALINA_HOME/bin/setenv.sh"

# Create or overwrite setenv.sh with the hashbang
echo "#!/bin/sh" > $SETENV_PATH

# Append the JAVA_OPTS export line
echo 'export JAVA_OPTS="${JAVA_OPTS} -Dvivo-dir=$VIVO_HOME -Droot-user-address=$ROOT_USER_ADDRESS -Ddefault-namespace=$DEFAULT_NAMESPACE -Dtdb:fileMode=$TDB_FILE_MODE"' >> $SETENV_PATH

# Make the setenv.sh file executable
chmod +x $SETENV_PATH

# Copy VIVO artifact to Tomcat webapps
echo "Deploying VIVO $CATALINA_HOME/webapps/$TOMCAT_CONTEXT_PATH.war"
cp -r /tmp/vivo.war $CATALINA_HOME/webapps/$TOMCAT_CONTEXT_PATH.war

catalina.sh run
