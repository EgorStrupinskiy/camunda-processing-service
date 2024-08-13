package com.azati.warshipprocessing.messaging;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResponseQueueSender {

    private final RabbitTemplate rabbitTemplate;

    public void send(ProcessingMessage processingMessage) {
        String exchangeName = "test";
        String key = "key";
        ObjectMapper mapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(exchangeName, key, mapper.writeValueAsString(processingMessage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
