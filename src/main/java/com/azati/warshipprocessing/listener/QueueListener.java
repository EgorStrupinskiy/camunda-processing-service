package com.azati.warshipprocessing.listener;

import com.azati.warshipprocessing.exception.NoSuchSessionException;
import com.azati.warshipprocessing.model.ProcessingMessage;
import com.azati.warshipprocessing.sender.QueueSender;
import com.azati.warshipprocessing.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

import static com.azati.warshipprocessing.util.VariableConstants.EXCEPTION;
import static com.azati.warshipprocessing.util.VariableConstants.NO_SUCH_PROCESS_INSTANCE;
import static com.azati.warshipprocessing.util.VariableConstants.NO_SUCH_SESSION;
import static com.azati.warshipprocessing.util.VariableConstants.PROCESSING_MESSAGE;
import static com.azati.warshipprocessing.util.VariableConstants.RECEIVED_MESSAGE_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.RESPONSE;
import static com.azati.warshipprocessing.util.VariableConstants.SHOOT;
import static com.azati.warshipprocessing.util.VariableConstants.SHOT_REQUEST;
import static com.azati.warshipprocessing.util.VariableConstants.SHOT_RESPONSE;
import static com.azati.warshipprocessing.util.VariableConstants.STATUS;
import static com.azati.warshipprocessing.util.VariableConstants.WRONG_REQUEST_TYPE;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QueueListener {

    private final QueueSender queueSender;
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;
    private final RuntimeService runtimeService;
    private final StandardPBEStringEncryptor encryptor;

    @RabbitListener(queues = "${responseQueue.name}")
    public void listen(String message) {
        ProcessingMessage parsedMessage;
        parsedMessage = getDecriptedProcessingMessage(message);
        if (parsedMessage == null) return;

        var variables = putMessageInfoInCamundaVariables(parsedMessage);

        if (!isSessionWithIdExist(parsedMessage)) return;
        if (!isProcessInstanceByIdExist(parsedMessage)) return;
        sendCorrespondingMessage(parsedMessage, variables);
    }

    private static HashMap<String, Object> putMessageInfoInCamundaVariables(ProcessingMessage parsedMessage) {
        var variables = new HashMap<String, Object>();
        variables.put(PROCESSING_MESSAGE, parsedMessage);
        variables.put(RECEIVED_MESSAGE_USER_ID, parsedMessage.getUserId());
        return variables;
    }

    private void sendCorrespondingMessage(ProcessingMessage parsedMessage, HashMap<String, Object> variables) {
        try {
            switch (parsedMessage.getAction()) {
                case SHOOT -> runtimeService.createMessageCorrelation(SHOT_REQUEST)
                        .processInstanceBusinessKey(String.valueOf(parsedMessage.getSessionId()))
                        .setVariables(variables)
                        .correlate();
                case RESPONSE -> {
                    variables.put(STATUS, parsedMessage.getStatus());
                    runtimeService.createMessageCorrelation(SHOT_RESPONSE)
                            .processInstanceBusinessKey(String.valueOf(parsedMessage.getSessionId()))
                            .setVariables(variables)
                            .correlate();
                }
                default -> throw new IllegalStateException("Unexpected value: " + parsedMessage.getAction());
            }
        } catch (Exception e) {
            log.error("User can`t make this type of request now");
            runtimeService.createMessageCorrelation(WRONG_REQUEST_TYPE)
                    .processInstanceBusinessKey(String.valueOf(parsedMessage.getSessionId()))
                    .setVariables(variables)
                    .correlate();
        }
    }

    private boolean isProcessInstanceByIdExist(ProcessingMessage parsedMessage) {
        try {
            runtimeService.createProcessInstanceByKey(parsedMessage.getSessionId().toString());
        } catch (NoSuchSessionException e) {
            log.error("There is session with this id, but there is no process instance with id: {}", parsedMessage.getSessionId());
            queueSender.send(ProcessingMessage.builder()
                    .sessionId(parsedMessage.getSessionId())
                    .userId(parsedMessage.getUserId())
                    .action(EXCEPTION)
                    .status(NO_SUCH_PROCESS_INSTANCE)
                    .build());
            return false;
        }
        return true;
    }

    private boolean isSessionWithIdExist(ProcessingMessage parsedMessage) {
        try {
            sessionService.getById(parsedMessage.getSessionId());
        } catch (NoSuchSessionException e) {
            log.error("There is no session with this id: {}", parsedMessage.getSessionId());
            queueSender.send(ProcessingMessage.builder()
                    .sessionId(parsedMessage.getSessionId())
                    .userId(parsedMessage.getUserId())
                    .action(EXCEPTION)
                    .status(NO_SUCH_SESSION)
                    .build());
            return false;
        } catch (Exception e) {
            log.error("User sent message without sessionId, ignoring it");
            return false;
        }
        return true;
    }

    private ProcessingMessage getDecriptedProcessingMessage(String message) {
        ProcessingMessage parsedMessage;
        try {
            var decryptedMessage = encryptor.decrypt(message);
            parsedMessage = objectMapper.readValue(decryptedMessage, ProcessingMessage.class);
            log.info("Message read from processing queue : {}", parsedMessage);
        } catch (Exception e) {
            log.error("Error while parsing message in queue");
            return null;
        }
        return parsedMessage;
    }
}
