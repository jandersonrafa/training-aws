#!/bin/bash
readonly QUEUES="$(dirname "$0")/../constants/queues.dat"
for queue in $(cat $QUEUES)
do
	aws sqs delete-queue --queue-url https://sqs.us-east-2.amazonaws.com/834318500728/$queue;
done
