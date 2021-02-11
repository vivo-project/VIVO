FROM tomcat:9-jdk11-openjdk

ENV JAVA_OPTS="${JAVA_OPTS} -Dvivo-dir=/opt/vivo/home/"

RUN mkdir /opt/vivo
RUN mkdir /opt/vivo/home

COPY ./installer/webapp/target/vivo.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
