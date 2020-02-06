package com.training.aws.queuehandlerback.controller;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.training.aws.queuehandlerback.domain.TypeAction;
import com.training.aws.queuehandlerback.domain.TypeFilteredHeaders;
import com.training.aws.queuehandlerback.dto.DeadLetterListingDto;
import com.training.aws.queuehandlerback.repository.DeadLetterRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.training.aws.queuehandlerback.domain.TypeAction.PENDENT;

@RestController
@RequestMapping("/api/dead-letter")
public class DeadLetterController {

    @Autowired
    private Jdbi jdbi;

    @Autowired
    private AmazonSQS amazonSQS;

    @GetMapping
    public List<DeadLetterListingDto> getAllEmployees() {
        List<DeadLetterListingDto> deadLetters = jdbi.withExtension(DeadLetterRepository.class, repository -> repository.findAll())
                .map(this::setDefaultValues)
                .collect(Collectors.toList());
        return deadLetters;
    }

    @PutMapping("/delete/{id}")
    public void delete(@PathVariable("id") String id) {
        jdbi.withExtension(DeadLetterRepository.class, repository -> repository.update(id, TypeAction.DELETED.name(), "", "", ""));
    }

    @PutMapping("/resubmit/{id}")
    public void resend(@PathVariable("id") String id, @RequestBody DeadLetterListingDto dto) throws JsonProcessingException {
        validate(dto);
        jdbi.withExtension(DeadLetterRepository.class, repository -> repository.update(id, TypeAction.RESUBMITTED.name(), dto.getResubmitHeaders(), dto.getResubmitMessage(), dto.getResubmitQueueName()));

        Map<String, MessageAttributeValue> resubmitHeaders = convertHeader(dto.getResubmitHeaders());
        final SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(dto.getResubmitQueueName())
                .withMessageBody(dto.getResubmitMessage())
                .withMessageAttributes(resubmitHeaders);

        amazonSQS.sendMessage(sendMessageRequest);
    }

    private void validate(DeadLetterListingDto deadLetterRes) {
        if (Strings.isNullOrEmpty(deadLetterRes.getResubmitMessage())) {
            throw new RuntimeException("Change message não pode ser nulo");
        }
        if (Strings.isNullOrEmpty(deadLetterRes.getResubmitHeaders())) {
            throw new RuntimeException("Change headers não pode ser nulo");
        }
        if (Strings.isNullOrEmpty(deadLetterRes.getResubmitQueueName())) {
            throw new RuntimeException("Destination queue name não pode ser nulo");
        }
    }

    private DeadLetterListingDto setDefaultValues(DeadLetterListingDto dto) {
        if (PENDENT.name().equals(dto.getTypeAction())) {
            dto.setResubmitHeaders(dto.getFilteredOriginalHeaders());
            dto.setResubmitMessage(dto.getOriginalMessage());
            dto.setResubmitQueueName(dto.getQueueName());
        }
        return dto;
    }

    private Map<String, MessageAttributeValue> convertHeader(String resubmitHeaders) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> listHeaders = mapper.readValue(resubmitHeaders, List.class);
        Map<String, Object> messageAttributes = listHeaders
                .stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return messageAttributes.entrySet().stream().map(es -> {
            MessageAttributeValue messageAttributeValue = new MessageAttributeValue();
            Optional<TypeFilteredHeaders> typeHeader = TypeFilteredHeaders.getByName(es.getKey());
            String dataType = typeHeader.isPresent() ? typeHeader.get().getType() : "String";
            messageAttributeValue.setDataType(dataType);
            messageAttributeValue.setStringValue(es.getValue().toString());
            return new AbstractMap.SimpleEntry<>(es.getKey(), messageAttributeValue);
        }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

}
