#!/bin/bash
readonly QUEUES="$(dirname "$0")/../constants/queues.dat"
for queue in $(cat $QUEUES)
do
	echo $queue
	aws sqs create-queue --queue-name $queue
done
aws sqs set-queue-attributes --queue-url https://sqs.us-east-2.amazonaws.com/834318500728/consumer_queue_message --attributes file://"$(dirname "$0")/set-queue-attributes.json"

