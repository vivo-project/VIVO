FROM tomcat:9-jdk11-openjdk

ARG SOLR_URL=http://localhost:8983/solr/vivocore
ARG VIVO_DIR=/opt/vivo/home
ARG TDB_FILE_MODE=direct

ENV SOLR_URL=${SOLR_URL}
ENV JAVA_OPTS="${JAVA_OPTS} -Dvivo-dir=$VIVO_DIR -Dtdb:fileMode=$TDB_FILE_MODE"

RUN mkdir /opt/vivo
RUN mkdir /opt/vivo/home

COPY ./installer/webapp/target/vivo.war /usr/local/tomcat/webapps/ROOT.war

COPY ./home/src/main/resources/config/default.applicationSetup.n3 /applicationSetup.n3
COPY ./home/src/main/resources/config/default.runtime.properties /runtime.properties

COPY start.sh /start.sh

EXPOSE 8080

CMD /start.sh $SOLR_URL
