# training-aws

# postgres local
docker run -p 5432:5432 --name training-aws-postgres -e POSTGRES_PASSWORD=trainingaws -e POSTGRES_USER=trainingaws -e POSTGRES_DB=trainingaws -d postgres
CREATE SCHEMA IF NOT exists AUTHORIZATION trainingaws;
CREATE TABLE trainingaws.messageconsumer (
	id bigserial NOT NULL,
	message varchar(4000) NOT NULL,
	CONSTRAINT messageconsumer_pkey PRIMARY KEY (id)
);

CREATE TABLE trainingaws.deadLetter (
	id  varchar(200)  NOT NULL,
	queueName varchar(100) NOT NULL,
	originalMessage varchar(4000) NOT NULL,
	originalHeaders varchar(4000) NOT NULL,
	filteredOriginalHeaders varchar(4000) NULL,
	resubmitMessage varchar(4000) NULL,
	resubmitHeaders varchar(4000) NULL,
	resubmitQueueName varchar(4000) NULL,
	typeAction varchar(15) NOT NULL,
	createdDate TIMESTAMP default now(),
	updateDate TIMESTAMP,
	CONSTRAINT deadletter_pkey PRIMARY KEY (id)
);

# deadletter example
INSERT INTO trainingaws.deadletter
(id, queuename, originalmessage, originalheaders, resubmitmessage, resubmitheaders, typeaction, filteredoriginalheaders, resubmitqueuename)
VALUES('365241ff-8a7a-4e32-aec3-96a7c843dfe1', 'teste', 'teste', '{"LogicalResourceId":"consumer_queue_message_dead_letter","ApproximateReceiveCount":"11","SentTimestamp":"1580836195702","ReceiptHandle":"AQEBM7mT81uk+Nw8Z0ZZVBGUgwTFdHmeq8EhkB6rZwkckagdtf5eYabpmg4nVbRlLaU7r5X2FYrN9HJlo6r2t6w+blL+7e4612rj3Ltgx6sX1MAkYPyEbo5fFX2ZWXbn33yPxWXaJ0OPJxYBFP+cndIHcJhD0mLf/fNYRdJ6i4OpXZrnZEF2i0p05zRh3GvAhIB9Zb3RXtnu+zltZfZ2Wd1BFgHQO4jJsB/oEIDxviAAJGJoUZm1QQmY6c9c19sxIurWqpSkpvFcbUvbEOzxrZCONNqrJyek3cmNidY8mbWSk7TMlus4TMO/U8D9HeGrUIvdngHLCNA5JeaYJEg4js+iLunkAqfmAf1HsdYfyTCshlWWWJymM0ECDIg6JMwD+p06Z0Owfbi/cLUr+WGmgySza2c8e86b/naf/hZ1XaUfFFk=","SenderId":"AIDAIXAZVALSQEJD5XSG4","lookupDestination":"consumer_queue_message_dead_letter","ApproximateFirstReceiveTimestamp":"1580836195702","MessageId":"6fc51fcc-faea-4c74-b95e-0a2207ce4561"}', '{     "productId": 126952,     "txName": "Conjunto 4 Cadeiras Dkr Charles Eames Wood Estofada Botonê - Marrom",     "blActive": true,     "productType": "SINGLE_ITEM",     "dhCreated": "2020-02-04T16:07:11.271371",     "dhLastUpdated": "2020-02-04T16:07:11.271371",     "txModel": "Charles Eames Wood Estofada Botonê",     "categoryId": 4221,     "productItems": [         {             "productItemId": 144330,             "blActive": true,             "dhCreated": "2020-02-04T16:07:11.275713",             "txEan": "7908067202678",             "txSize": null,             "txVoltage": null,             "txColor": "Marrom",             "productItemSellerSet": [                 {                     "productItemSellerId": 146874,                     "itemStatus": "APPROVED",                     "dhCreated": "2020-02-04T16:07:11.273912",                     "txSku": "3716",                     "dcPrice": 791.96,                     "dcHeight": 55,                     "dcLength": 52,                     "dcWeight": 70,                     "irDeliveryTimeDays": 1,                     "dcWidth": 65,                     "sellerId": 170,   "blIntegrated": false,                  "irStock": 66                 }             ]         }     ] }', '[{"SentTimestamp":"1580846284782"},{"ReceiptHandle":"AQEBwcogUic7cDacEWCAKnoyuuN3GpJsWkCDg5s+2xzxWvnuWmqABybcqg5+aLGw4DikVqrtGKOyvzADvQd2zf9UI/OFqRO6HMitKzY5m5S5XXmqSjgGNcX8p+aR/6wiVCRNnmYRqQv6CtP5o051gUij+FZPYAx+JPlNHs/3ZyyjCtaUqDTQHzYLB4JN4PZNccRWW/1cUy/FMJlot9GVElQtzZD33n4w06NPYJerTnW43lTpJsZ76C0CUSWkGyKB0Hv6O5YrR9yA8YQnCJdcTqExnDbkUGZC5nRzhDB1S4ZGu+szbQdE0ddFLINh6S9bdd8lqB645Drf0YaQGpPiB6QK1AonMKKfVWNI5RGIYen8RBFcg8shJGfYNFtQ4RwSRrtV2g9qxaLXBbA72eX/NyVVV9zAbb50RD4NvH0Z4udEfVY="},{"lookupDestination":"consumer_queue_message_dead_letter"}]', 'RESUBMITTED', '[{"SentTimestamp":"1580836195702"},{"ReceiptHandle":"AQEBM7mT81uk+Nw8Z0ZZVBGUgwTFdHmeq8EhkB6rZwkckagdtf5eYabpmg4nVbRlLaU7r5X2FYrN9HJlo6r2t6w+blL+7e4612rj3Ltgx6sX1MAkYPyEbo5fFX2ZWXbn33yPxWXaJ0OPJxYBFP+cndIHcJhD0mLf/fNYRdJ6i4OpXZrnZEF2i0p05zRh3GvAhIB9Zb3RXtnu+zltZfZ2Wd1BFgHQO4jJsB/oEIDxviAAJGJoUZm1QQmY6c9c19sxIurWqpSkpvFcbUvbEOzxrZCONNqrJyek3cmNidY8mbWSk7TMlus4TMO/U8D9HeGrUIvdngHLCNA5JeaYJEg4js+iLunkAqfmAf1HsdYfyTCshlWWWJymM0ECDIg6JMwD+p06Z0Owfbi/cLUr+WGmgySza2c8e86b/naf/hZ1XaUfFFk="},{"lookupDestination":"consumer_queue_message_dead_letter"}]', 'teste');




$ curl -X PUT http://localhost:2010/dead-letter/delete/4b663bfa-9b2e-4444-ba8b-ffa07c79c809
$ curl -X PUT http://localhost:2010/dead-letter/resubmit/4b663bfa-9b2e-4444-ba8b-ffa07c79c809?resubmitMessage=teste%20message
