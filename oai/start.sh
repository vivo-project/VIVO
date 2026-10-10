#!/bin/bash

shutdown() {
  exit 0;
}
trap 'shutdown' SIGTERM
echo "VIVO_URL=${VIVO_URL}" > /joai/config/.env
cron  &
/usr/local/tomcat/bin/catalina.sh run &
PID=$!
wait $PID
