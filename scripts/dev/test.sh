#!/bin/bash

# $1 = Service artifactId
if [ -z "$1" ]
  then
    echo -e "Service artifactId argument not supplied"
    echo -e "Ex: sh ./scripts/dev/test.sh product-service \n"
    exit 1
fi

pom="$1"
shift

echo -e "\n"Command: ./mvnw -am -pl "$pom" test "${@}" "\n"
./mvnw -am -pl "$pom" test "${@}"