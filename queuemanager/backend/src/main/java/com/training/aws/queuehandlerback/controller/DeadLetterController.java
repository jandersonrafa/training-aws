package com.training.aws.queuehandlerback.controller;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.training.aws.queuehandlerback.domain.TypeAction;
import com.training.aws.queuehandlerback.domain.TypeFilteredHeaders;
import com.training.aws.queuehandlerback.dto.DeadLetterFilterDto;
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

//    @GetMapping
//    public List<DeadLetterListingDto> getAllEmployees() {
//        List<DeadLetterListingDto> deadLetters = jdbi.withExtension(DeadLetterRepository.class, repository -> repository.findAll())
//                .map(this::setDefaultValues)
//                .collect(Collectors.toList());
//        return deadLetters;
//    }

    @GetMapping("/filter")
    public List<DeadLetterListingDto> getAllEmployeesByFilter(DeadLetterFilterDto filter) {
        List<DeadLetterListingDto> deadLetters = jdbi.withExtension(DeadLetterRepository.class, repository -> repository.findByTypeAction(filter.getTypeAction()))
                .map(this::setDefaultValues)
                .collect(Collectors.toList());
        return deadLetters;
    }

    @PutMapping("/delete/{id}")
    public void delete(@PathVariable("id") String id) {
        jdbi.withExtension(DeadLetterRepository.class, repository -> repository.update(id, TypeAction.DELETED.name(), "", "", ""));
    }

    @PutMapping("/resubmit/{id}")
    public void resend(@PathVariable("id") String id, @RequestBody DeadLetterListingDto dto) {
        validate(dto);
        resubmit(dto);
    }

    @PutMapping("/resubmit-messages")
    public void resendMessages(@RequestBody List<String> deadletters) {
        List<DeadLetterListingDto> dtos = deadletters.parallelStream()
                .map((deadId) -> jdbi.withExtension(DeadLetterRepository.class, repository -> repository.findDtoById(deadId)))
                .collect(Collectors.toList());
        dtos.parallelStream().forEach(this::validate);
        dtos.parallelStream().forEach(this::resubmit);
    }

    private void validate(DeadLetterListingDto dto) {
        String messageId = "Message id : " + dto.getId() + ": ";
        if (Strings.isNullOrEmpty(dto.getResubmitMessage())) {
            throw new RuntimeException(messageId + "Change message não pode ser nulo");
        }
        if (Strings.isNullOrEmpty(dto.getResubmitHeaders())) {
            throw new RuntimeException(messageId + "Change headers não pode ser nulo");
        }
        if (Strings.isNullOrEmpty(dto.getResubmitQueueName())) {
            throw new RuntimeException(messageId + "Destination queue name não pode ser nulo");
        }
    }

    private void resubmit(DeadLetterListingDto dto) {
        jdbi.withExtension(DeadLetterRepository.class, repository -> repository.update(dto.getId(), TypeAction.RESUBMITTED.name(), dto.getResubmitHeaders(), dto.getResubmitMessage(), dto.getResubmitQueueName()));
        Map<String, MessageAttributeValue> headers = convertHeader(dto.getResubmitHeaders());
        final SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(dto.getResubmitQueueName())
                .withMessageBody(dto.getResubmitMessage())
                .withMessageAttributes(headers);

        amazonSQS.sendMessage(sendMessageRequest);
    }

    private DeadLetterListingDto setDefaultValues(DeadLetterListingDto dto) {
        if (PENDENT.name().equals(dto.getTypeAction())) {
            if (Strings.isNullOrEmpty(dto.getResubmitHeaders())){
                dto.setResubmitHeaders(dto.getFilteredOriginalHeaders());
            }
            if (Strings.isNullOrEmpty(dto.getResubmitMessage())){
                dto.setResubmitMessage(dto.getOriginalMessage());
            }
            if (Strings.isNullOrEmpty(dto.getResubmitQueueName())){
                dto.setResubmitQueueName(dto.getQueueName());
            }
        }
        return dto;
    }

    private Map<String, MessageAttributeValue> convertHeader(String resubmitHeaders) {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> listHeaders = readMapper(resubmitHeaders, mapper);

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

    private List readMapper(String resubmitHeaders, ObjectMapper mapper) {
        try {
            return mapper.readValue(resubmitHeaders, List.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error convert headers");
        }
    }

}
