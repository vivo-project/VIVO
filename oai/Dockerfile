FROM docker.io/eclipse-temurin:21-jdk as builder

ENV VIVO_URL=${VIVO_URL}

WORKDIR /tmp
RUN apt-get update && apt-get install -y ant unzip git maven
RUN git clone https://github.com/NCAR/joai-project
COPY Identify.patch ./

RUN patch joai-project/web/oai_requests/Identify.jsp Identify.patch

WORKDIR /usr/local
RUN curl --location 'https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.118/bin/apache-tomcat-9.0.118.tar.gz' | tar xz
RUN git clone https://github.com/litvinovg/rdf-to-openaire
RUN git clone https://github.com/litvinovg/joai-connector
RUN cd apache-tomcat* && echo "catalina.home=$(pwd)" > ~/build.properties
COPY . /tmp/joai-project

WORKDIR /tmp/joai-project
RUN ant dist
RUN mkdir /war

WORKDIR /war
RUN unzip /tmp/joai-project/dist/oai.war

#Metadata format conversion
RUN sed -i '/WEB-INF\/xsl_files/a\    <context-param>\n      <param-name>xslconverter - VIVO RDF/XML to openAIRE converter</param-name>\n      <param-value>rdf_to_openaire.xsl|rdf|oai_cerif_openaire_v1_2</param-value>\n    </context-param>' WEB-INF/web.xml

#OpenAIRE format description
RUN sed -i '/org.dlese.dpc.oai.provider.OAIProviderServlet/a\    <init-param>\n      <param-name>xmlformatinfo - oai_cerif_openaire_v1_2 schema and namespace</param-name>\n      <param-value>oai_cerif_openaire_v1_2|https://www.openaire.eu/schema/cris/1.2/openaire-cerif-profile.xsd|https://www.openaire.eu/cerif-profile/1.2/</param-value>\n    </init-param>' WEB-INF/web.xml

WORKDIR /usr/local/rdf-to-openaire
RUN mvn install
RUN cp src/main/resources/rdf_to_openaire.xsl /war/WEB-INF/xsl_files/

WORKDIR /usr/local/joai-connector
RUN mvn install

FROM docker.io/tomcat:9.0-jre21-temurin 

WORKDIR /usr/local/tomcat/webapps
RUN apt-get update && apt-get install -y cron
COPY --from=builder /war/ ./ROOT/
COPY --from=builder /usr/local/joai-connector/target/joai_connector.jar /usr/local/bin/
RUN \
  curl -L -o ../lib/woodstox-core-5.0.3.jar 'https://search.maven.org/remotecontent?filepath=com/fasterxml/woodstox/woodstox-core/5.0.3/woodstox-core-5.0.3.jar' && \
  curl -L -o ../lib/stax2-api-4.0.0.jar 'https://search.maven.org/remotecontent?filepath=org/codehaus/woodstox/stax2-api/4.0.0/stax2-api-4.0.0.jar' && \
  mkdir -p /joai/config/harvester /joai/config/repository && \
  ln -s /joai/config/harvester /usr/local/tomcat/webapps/ROOT/WEB-INF/harvester_settings_and_data && \
  ln -s /joai/config/repository /usr/local/tomcat/webapps/ROOT/WEB-INF/repository_settings_and_data

COPY crontab /joai/config/
RUN chmod 0744 /joai/config/crontab
RUN crontab -u root /joai/config/crontab

COPY get-data.sh /joai/config/
RUN chmod 0744 /joai/config/get-data.sh

COPY connector.properties /joai/config/
COPY start.sh /joai/config/
RUN chmod 0744 /joai/config/start.sh

COPY config/ /joai/config/repository/

WORKDIR /usr/local/tomcat/conf
RUN openssl req -new -newkey rsa:4096 -days 36500 -nodes -x509 -subj "/C=US/ST=Denial/L=Springfield/O=Dis/CN=example.com" -keyout key.pem -out cert.pem
RUN sed -i '/<Service name="Catalina">/a\    <Connector port="8443" protocol="org.apache.coyote.http11.Http11AprProtocol"\n               maxThreads="150" SSLEnabled="true" >\n        <UpgradeProtocol className="org.apache.coyote.http2.Http2Protocol" />\n        <SSLHostConfig>\n            <Certificate certificateKeyFile="conf/key.pem"\n                         certificateFile="conf/cert.pem"\n                         type="RSA" />\n        </SSLHostConfig>\n    </Connector>' server.xml


WORKDIR /usr/local/tomcat
CMD ["/joai/config/start.sh"]

# just the config
VOLUME /joai/config

# the harvested/provided records
VOLUME /joai/data
