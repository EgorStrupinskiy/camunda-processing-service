package com.azati.warshipprocessing.messaging;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResponseQueueSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final StandardPBEStringEncryptor cryptoConverter;

    public void send(ProcessingMessage processingMessage) {
        String exchangeName = "test";
        String key = "key";
        try {
            var jsonMessage = objectMapper.writeValueAsString(processingMessage);
            var encryptedObject = cryptoConverter.encrypt(jsonMessage);
            rabbitTemplate.convertAndSend(exchangeName, key, encryptedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
