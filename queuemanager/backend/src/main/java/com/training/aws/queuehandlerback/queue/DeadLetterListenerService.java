package com.training.aws.queuehandlerback.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.training.aws.queuehandlerback.domain.TypeAction;
import com.training.aws.queuehandlerback.domain.TypeFilteredHeaders;
import com.training.aws.queuehandlerback.model.DeadLetter;
import com.training.aws.queuehandlerback.repository.DeadLetterRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DeadLetterListenerService {

    @Autowired
    private Jdbi jdbi;

    @SqsListener(value = "${consumer.queue.message.dead.letter}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void messageConsumer(String message, @Headers Map<String, Object> messageAttributes,
                                @Payload String payload) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> attributes = messageAttributes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (attr) -> toString(attr)));
        attributes.remove("Visibility");
        // Do something
        jdbi.withExtension(DeadLetterRepository.class, repository -> {
            DeadLetter user = new DeadLetter();
            user.setQueueName(Optional.ofNullable(attributes.get("originalQueueName")).orElse("consumer_queue_message"));
            user.setId(UUID.randomUUID().toString());
            user.setOriginalHeaders(mapToString(mapper, attributes));
            mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
            mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            mapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
            mapper.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true);
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
            mapper.configure(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID, true);
            mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
            mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            user.setFilteredOriginalHeaders(mapper.writeValueAsString(filteredHeaders(attributes)));
            user.setOriginalMessage(message);
            user.setTypeAction(TypeAction.PENDENT.name());
            return repository.insertBean(user);
        });

        System.out.println("Messages content: " + message);
    }

    private String toString(Map.Entry<String, Object> attr) {
        return attr.getValue().toString();
    }

    private List<Map.Entry<String, String>> filteredHeaders(Map<String, String> messageAttributes) {
        return messageAttributes
                .entrySet()
                .stream()
                .filter((es) -> TypeFilteredHeaders.getByName(es.getKey()).isPresent())
                .collect(Collectors.toList());
    }

    private String mapToString(ObjectMapper mapper, Map<String, String> attributes) throws JsonProcessingException {
        return mapper.writeValueAsString(attributes);
    }
}
