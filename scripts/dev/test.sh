#!/bin/sh

# $1 = Base pom.xml
if [ -z "$1" ]
  then
    echo "Base pom.xml argument not supplied"
    echo "Ex: sh ./scripts/dev/test.sh ./product-service/pom.xml \n"
    exit 1
fi

./mvnw -f $1 test $2
