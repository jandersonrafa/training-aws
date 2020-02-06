package com.training.aws.queuehandlerback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

@Getter
@Setter
@NoArgsConstructor
public class DeadLetterListingDto {

    private String id;
    private String queueName;
    private String originalMessage;
    private String originalHeaders;
    private String filteredOriginalHeaders;
    private String resubmitMessage;
    private String resubmitHeaders;
    private String resubmitQueueName;
    private String typeAction;

    @JdbiConstructor
    public DeadLetterListingDto(String id, String queueName, String originalHeaders, String filteredOriginalHeaders, String originalMessage, String resubmitMessage, String resubmitHeaders, String resubmitQueueName , String typeAction) {
        this.id = id;
        this.queueName = originalMessage;
        this.originalMessage = originalMessage;
        this.originalHeaders = originalHeaders;
        this.filteredOriginalHeaders = filteredOriginalHeaders;
        this.resubmitMessage = resubmitMessage;
        this.resubmitHeaders = resubmitHeaders;
        this.typeAction = typeAction;
        this.resubmitQueueName = resubmitQueueName;
    }
}