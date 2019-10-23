package com.training.aws.queuehandlerback.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class MessageConsumer {

    private String id;
    private String message;

    @JdbiConstructor
    public MessageConsumer(String id, String message) {
        this.id = id;
        this.message = message;
    }
}