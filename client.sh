#!/bin/sh

java -Dserver.port=${SERVER_PORT} \
     -Deureka.client.service-url.defaultZone=${EUREKA_SERVICE_URL} \
     -jar /usr/local/src/app/client/build/libs/client-0.0.1.jar