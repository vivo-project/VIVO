ARG TOMCAT_CONTEXT_PATH=ROOT
ARG USER_ID=3001
ARG USER_NAME=vivo
ARG USER_HOME_DIR=/home/$USER_NAME

FROM tomcat:9-jdk11-openjdk
ARG TOMCAT_CONTEXT_PATH
ARG USER_ID
ARG USER_NAME
ARG USER_HOME_DIR

ARG VIVO_HOME_DIR=/opt/vivo/home
ARG TDB_FILE_MODE=direct
ARG ROOT_USER_ADDRESS=vivo_root@mydomain.edu
ARG DEFAULT_NAMESPACE=http://vivo.mydomain.edu/individual/

ARG SOLR_URL=http://localhost:8983/solr/vivocore
ARG SELF_ID_MATCHING_PROPERTY=http://vivo.mydomain.edu/ns

ARG LOAD_SAMPLE_DATA=false
ARG SAMPLE_DATA_REPO_URL=https://github.com/vivo-project/sample-data.git
ARG SAMPLE_DATA_BRANCH=main
ARG SAMPLE_DATA_DIRECTORY=openvivo

ENV JAVA_OPTS="${JAVA_OPTS} -Dvivo-dir=$VIVO_HOME_DIR -Droot-user-address=$ROOT_USER_ADDRESS -Ddefault-namespace=$DEFAULT_NAMESPACE -Dtdb:fileMode=$TDB_FILE_MODE"

ENV VIVO_HOME_DIR=${VIVO_HOME_DIR}

ENV SOLR_URL=${SOLR_URL}
ENV SELF_ID_MATCHING_PROPERTY=${SELF_ID_MATCHING_PROPERTY}

ENV LOAD_SAMPLE_DATA=${LOAD_SAMPLE_DATA}
ENV SAMPLE_DATA_REPO_URL=${SAMPLE_DATA_REPO_URL}
ENV SAMPLE_DATA_BRANCH=${SAMPLE_DATA_BRANCH}
ENV SAMPLE_DATA_DIRECTORY=${SAMPLE_DATA_DIRECTORY}

RUN \
  apt-get update -y && \
  apt-get upgrade -y && \
  apt install git -y

RUN \
  addgroup --disabled-password --gid $USER_ID $USER_NAME && \
  adduser --disabled-password --home $USER_HOME_DIR --uid $USER_ID --gid $USER_ID $USER_NAME

RUN mkdir -p $VIVO_HOME_DIR

COPY ./installer/webapp/target/vivo.war /usr/local/tomcat/webapps/$TOMCAT_CONTEXT_PATH.war

COPY ./home/src/main/resources/config/default.applicationSetup.n3 /tmp/applicationSetup.n3
COPY ./home/src/main/resources/config/default.runtime.properties /tmp/runtime.properties

COPY start.sh /opt/vivo/start.sh

EXPOSE 8080

CMD ["/bin/bash", "/opt/vivo/start.sh"]
