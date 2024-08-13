package com.azati.warshipprocessing.sender;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${actionQueue.exchangeName}")
    private String exchangeName;

    @Value("${actionQueue.key}")
    private String key;

    public void send(ProcessingMessage request) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, key, objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
