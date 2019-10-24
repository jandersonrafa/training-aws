package com.training.aws.queuehandlerback.controller;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.aws.queuehandlerback.domain.TypeAction;
import com.training.aws.queuehandlerback.model.DeadLetter;
import com.training.aws.queuehandlerback.repository.DeadLetterRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dead-letter")
public class DeadLetterController {

    @Autowired
    private Jdbi jdbi;

    @Autowired
    private AmazonSQS amazonSQS;

    @GetMapping
    public List<DeadLetter> getAllEmployees() {
        return jdbi.withExtension(DeadLetterRepository.class, repository -> repository.findAll());
    }

    @PutMapping("/delete/{id}")
    public void delete(@PathVariable("id") String id) {
        jdbi.withExtension(DeadLetterRepository.class, repository -> repository.update(id, TypeAction.DELETED.name(), "", ""));
    }

    @PutMapping("/resubmit/{id}")
    public void resend(@PathVariable("id") String id, @RequestBody DeadLetter deadLetterRes) throws JsonProcessingException {
        jdbi.withExtension(DeadLetterRepository.class, repository -> repository.update(id, TypeAction.RESUBMITTED.name(), "", deadLetterRes.getResubmitMessage()));
        DeadLetter deadLetter = jdbi.withExtension(DeadLetterRepository.class, repository -> repository.findById(id));

        ObjectMapper mapper = new ObjectMapper();

        final SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(deadLetter.getQueueName())
                .withMessageBody(deadLetterRes.getResubmitMessage())
//                .withMessageAttributes(mapper.readValue(deadLetter.getOriginalHeaders(), Map.class))
                ;

        amazonSQS.sendMessage(sendMessageRequest);
    }
}
