FROM tomcat:9-jdk11-openjdk

ARG SOLR_URL=http://localhost:8983/solr/vivocore
ARG VIVO_DIR=/usr/local/vivo/home
ARG TDB_FILE_MODE=direct

ENV VIVO_DIR=${VIVO_DIR}
ENV SOLR_URL=${SOLR_URL}
ENV JAVA_OPTS="${JAVA_OPTS} -Dvivo-dir=$VIVO_DIR -Dtdb:fileMode=$TDB_FILE_MODE"

RUN mkdir -p $VIVO_DIR

COPY ./installer/webapp/target/vivo.war /usr/local/tomcat/webapps/ROOT.war

COPY ./home/src/main/resources/config/default.applicationSetup.n3 /applicationSetup.n3
COPY ./home/src/main/resources/config/default.runtime.properties /runtime.properties

COPY start.sh /start.sh

EXPOSE 8080

CMD ["/bin/bash", "/start.sh"]
