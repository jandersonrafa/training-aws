package com.training.aws.queuehandlerback.queue;

import com.training.aws.queuehandlerback.model.MessageConsumer;
import com.training.aws.queuehandlerback.repository.MessageConsumerRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageConsumerListenerService {

    @Autowired
    private Jdbi jdbi;

    @SqsListener(value = "${consumer.queue.message}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void messageConsumer(@Headers Map<String, Object> messageAttributes,
                                @Payload String message) throws Exception {
        // Do something
        if (message.contains("error")) {
            throw new Exception("Erro tratado");
        };
        jdbi.withExtension(MessageConsumerRepository.class, repository -> {
            MessageConsumer user = new MessageConsumer();
            user.setMessage(message);
            return repository.insertBean(user);
        });

        System.out.println("Messages attributes: " + messageAttributes);
        System.out.println("Body: " + message);
    }
}
