#!/bin/bash
cd /joai/data
. /joai/config/.env
flock -n /tmp/get-data.lock -c "/opt/java/openjdk/bin/java -jar /usr/local/bin/joai_connector.jar -url ${VIVO_URL} -config /joai/config/connector.properties" &> /proc/1/fd/1
