package com.azati.warshipprocessing.messaging;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.azati.warshipprocessing.util.TestVariableConstants.ACTION_QUEUE_NAME;
import static org.camunda.bpm.admin.impl.plugin.resources.MetricsRestService.objectMapper;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Service
@RequiredArgsConstructor
public class ActionQueueListener {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StandardPBEStringEncryptor encryptor;


    public ProcessingMessage getProcessingMessage() throws JsonProcessingException {
        String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
        assertNotNull(receivedMessage);
        return objectMapper.readValue(encryptor.decrypt(receivedMessage), ProcessingMessage.class);
    }
}
