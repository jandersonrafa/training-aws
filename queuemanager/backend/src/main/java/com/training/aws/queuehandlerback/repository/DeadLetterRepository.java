package com.training.aws.queuehandlerback.repository;

import com.training.aws.queuehandlerback.config.LogSqlFactory;
import com.training.aws.queuehandlerback.model.DeadLetter;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@LogSqlFactory
public interface DeadLetterRepository {

    @SqlUpdate("INSERT INTO deadLetter(id, queueName, originalMessage, originalHeaders, typeAction) VALUES (:id, :queueName, :originalMessage, :originalHeaders, :typeAction)")
    int insertBean(@BindBean DeadLetter deadLetter);

    @SqlQuery("SELECT * FROM deadLetter ORDER BY id")
    @RegisterBeanMapper(DeadLetter.class)
    List<DeadLetter> findAll();

    @SqlUpdate("update deadLetter set typeAction = :typeAction, resubmitHeaders = :resubmitHeaders, resubmitMessage = :resubmitMessage where id = :id")
    int update(@Bind("id") String id, @Bind("typeAction") String typeAction, @Bind("resubmitHeaders") String resubmitHeaders, @Bind("resubmitMessage") String resubmitMessage);

    @SqlQuery("SELECT * FROM deadLetter WHERE id = :id")
    @RegisterBeanMapper(DeadLetter.class)
    DeadLetter findById(@Bind("id") String id);

}
