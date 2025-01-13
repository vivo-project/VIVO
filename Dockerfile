FROM tomcat:9-jdk11-openjdk

ARG TDB_FILE_MODE=direct
ARG VIVO_HOME=/usr/local/vivo/home
ARG SOLR_URL=http://localhost:8983/solr/vivocore

ENV JAVA_OPTS="${JAVA_OPTS} -Dtdb:fileMode=$TDB_FILE_MODE"
ENV VIVO_HOME=${VIVO_HOME}
ENV SOLR_URL=${SOLR_URL}

RUN mkdir -p ${VIVO_HOME}

# Copy VIVO home onto image for backup, initialization, and reset
COPY ./installer/webapp/target/vivo.war /usr/local/tomcat/webapps/ROOT.war

COPY start.sh /start.sh

EXPOSE 8080

CMD ["/bin/bash", "/start.sh"]
