package com.training.aws.queuehandlerback.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class DeadLetter {

    private String id;
    private String queueName;
    private String originalMessage;
    private String originalHeaders;
    private String resubmitMessage;
    private String typeAction;

    @JdbiConstructor
    public DeadLetter(String id, String queueName, String originalHeaders, String originalMessage, String typeAction) {
        this.id = id;
        this.queueName = originalMessage;
        this.originalMessage = originalMessage;
        this.originalHeaders = originalHeaders;
        this.resubmitMessage = resubmitMessage;
        this.typeAction = typeAction;
    }
}