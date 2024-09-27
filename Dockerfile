FROM tomcat:9-jdk11-openjdk
ARG USER_ID=3001
ARG USER_NAME=vivo
ARG USER_HOME_DIR=/home/$USER_NAME

ENV TOMCAT_CONTEXT_PATH=ROOT
ENV VIVO_HOME=/usr/local/vivo/home
ENV TDB_FILE_MODE=direct
ENV ROOT_USER_ADDRESS=vivo_root@mydomain.edu
ENV DEFAULT_NAMESPACE=http://vivo.mydomain.edu/individual/

ENV SOLR_URL=http://localhost:8983/solr/vivocore
ENV SELF_ID_MATCHING_PROPERTY=http://vivo.mydomain.edu/ns#networkId

ENV LOAD_SAMPLE_DATA=false
ENV SAMPLE_DATA_REPO_URL=https://github.com/vivo-project/sample-data.git
ENV SAMPLE_DATA_BRANCH=main
ENV SAMPLE_DATA_DIRECTORY=i18n

ENV RECONFIGURE=false

COPY ./installer/webapp/target/vivo.war /tmp/vivo.war

COPY ./home/src/main/resources/config/default.applicationSetup.n3 /tmp/applicationSetup.n3
COPY ./home/src/main/resources/config/default.runtime.properties /tmp/runtime.properties

COPY start.sh /usr/local/vivo/start.sh

RUN \
apt-get update -y && \
apt-get upgrade -y && \
apt-get install -y git && \
apt-get clean && \
rm -rf /var/lib/apt/lists/* && \
addgroup --disabled-password --gid ${USER_ID} ${USER_NAME} && \
adduser --disabled-password --home ${USER_HOME_DIR} --uid ${USER_ID} --gid ${USER_ID} ${USER_NAME} && \
mkdir -p ${VIVO_HOME} && \
chown -R ${USER_ID}:${USER_ID} ${VIVO_HOME} /usr/local/tomcat /usr/local/vivo /tmp

USER ${USER_NAME}

EXPOSE 8080

CMD ["/bin/bash", "/usr/local/vivo/start.sh"]
