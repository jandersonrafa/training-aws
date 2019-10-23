#!/bin/bash
aws sqs delete-queue --queue-url https://sqs.us-east-2.amazonaws.com/834318500728/consumer_queue_message
aws sqs delete-queue --queue-url https://sqs.us-east-2.amazonaws.com/834318500728/consumer_queue_message_dead_letter

