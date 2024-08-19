package com.azati.warshipprocessing.sender;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final StandardPBEStringEncryptor cryptoConverter;


    @Value("${actionQueue.exchangeName}")
    private String exchangeName;

    @Value("${actionQueue.key}")
    private String key;

    public void send(ProcessingMessage request) {
        try {
            var jsonMessage = objectMapper.writeValueAsString(request);
            var encryptedObject = cryptoConverter.encrypt(jsonMessage);
            rabbitTemplate.convertAndSend(exchangeName, key, encryptedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
