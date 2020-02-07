package com.training.aws.queuehandlerback.repository;

import com.training.aws.queuehandlerback.config.LogSqlFactory;
import com.training.aws.queuehandlerback.dto.DeadLetterListingDto;
import com.training.aws.queuehandlerback.model.DeadLetter;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@LogSqlFactory
public interface DeadLetterRepository {

    @SqlUpdate("INSERT INTO deadLetter(id, queueName, originalMessage, originalHeaders, filteredOriginalHeaders, createdDate, typeAction) VALUES (:id, :queueName, :originalMessage, :originalHeaders, :filteredOriginalHeaders, :createdDate, :typeAction)")
    int insertBean(@BindBean DeadLetter deadLetter);

    @SqlQuery("SELECT * FROM deadLetter ORDER BY createdDate desc")
    @RegisterBeanMapper(DeadLetterListingDto.class)
    Stream<DeadLetterListingDto> findAll();

    @SqlQuery("SELECT * FROM deadLetter WHERE typeAction = :typeAction ORDER BY createdDate desc")
    @RegisterBeanMapper(DeadLetterListingDto.class)
    Stream<DeadLetterListingDto> findByTypeAction(@Bind("typeAction") String typeAction);

    @SqlUpdate("update deadLetter set typeAction = :typeAction, resubmitHeaders = :resubmitHeaders, resubmitMessage = :resubmitMessage, resubmitQueueName = :resubmitQueueName, updatedDate = :updatedDate where id = :id")
    int update(@Bind("id") String id, @Bind("typeAction") String typeAction, @Bind("resubmitHeaders") String resubmitHeaders, @Bind("resubmitMessage") String resubmitMessage, @Bind("resubmitQueueName") String resubmitQueueName, @Bind("updatedDate")LocalDateTime updatedDate);

    @SqlQuery("SELECT * FROM deadLetter WHERE id = :id")
    @RegisterBeanMapper(DeadLetter.class)
    DeadLetter findById(@Bind("id") String id);

    @SqlQuery("SELECT * FROM deadLetter WHERE id = :id")
    @RegisterBeanMapper(DeadLetterListingDto.class)
    DeadLetterListingDto findDtoById(@Bind("id") String id);

}
