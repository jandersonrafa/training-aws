#!/bin/bash
aws sqs create-queue --queue-name consumer_queue_message_dead_letter
aws sqs create-queue --queue-name consumer_queue_message --attributes file://queue-attributes.json