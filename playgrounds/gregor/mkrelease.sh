#!/usr/bin/env bash
cd ../../examples
mvn clean
mvn install -DskipTests=true


cd ../matsim
mvn clean
mvn install -DskipTests=true

cd ../contribs
mvn clean
mvn install -fae -DskipTests=true

cd ../playgrounds/gregor
mvn clean
mvn -Prelease -DskipTests=true

