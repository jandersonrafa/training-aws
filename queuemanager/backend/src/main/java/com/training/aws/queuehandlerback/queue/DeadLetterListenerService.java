package com.training.aws.queuehandlerback.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.aws.queuehandlerback.domain.TypeAction;
import com.training.aws.queuehandlerback.model.DeadLetter;
import com.training.aws.queuehandlerback.repository.DeadLetterRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DeadLetterListenerService {

    @Autowired
    private Jdbi jdbi;

    @SqsListener(value = "${consumer.queue.message.dead.letter}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void messageConsumer(String message, @Headers Map<String, String> messageAttributes,
                                @Payload String payload) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> attributes = messageAttributes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        attributes.remove("Visibility");
        // Do something
        jdbi.withExtension(DeadLetterRepository.class, repository -> {
            DeadLetter user = new DeadLetter();
            user.setQueueName(Optional.ofNullable(attributes.get("originalQueueName")).orElse("consumer_queue_message"));
            user.setId(UUID.randomUUID().toString());
            user.setOriginalHeaders(mapper.writeValueAsString(attributes));
            user.setOriginalMessage(message);
            user.setTypeAction(TypeAction.PENDENT.name());
            return repository.insertBean(user);
        });

        System.out.println("Messages content: " + message);
    }
}
