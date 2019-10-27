#!/bin/bash
mvn clean install -Dspring.profiles.active=test -DskipTests
docker build -t training-aws-queuemanagerback .
docker rm --force training-aws-queuemanagerback
docker container run --publish 8081:8081 -e "SPRING_PROFILES_ACTIVE=test" --detach --name training-aws-queuemanagerback --net=host training-aws-queuemanagerback
