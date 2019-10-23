package com.training.aws.queuehandlerback.repository;

import com.training.aws.queuehandlerback.config.LogSqlFactory;
import com.training.aws.queuehandlerback.model.MessageConsumer;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@LogSqlFactory
public interface MessageConsumerRepository {

    @SqlUpdate("INSERT INTO messageconsumer(message) VALUES (:message)")
    int insertBean(@BindBean MessageConsumer messageConsumer);
}
