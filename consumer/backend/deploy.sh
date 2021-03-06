#!/bin/bash
mvn clean install -Dspring.profiles.active=test -DskipTests
docker build -t training-aws-consumerback .
docker rm --force training-aws-consumerback
docker container run --publish 8081:8081 -e "SPRING_PROFILES_ACTIVE=test" --detach --name training-aws-consumerback --net=host training-aws-consumerback
