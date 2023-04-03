#!/bin/sh

# $1 = Base pom.xml
./mvnw -f $1 test $2
