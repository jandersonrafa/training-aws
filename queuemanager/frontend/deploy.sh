#!/bin/bash
#npm run buildProd
docker rm --force training-aws-queuemanagerfront
docker build -t training-aws-queuemanagerfront .
docker container run --publish 8080:80 --detach --name training-aws-queuemanagerfront training-aws-queuemanagerfront
