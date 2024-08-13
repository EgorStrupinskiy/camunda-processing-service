package com.azati.warshipprocessing.action;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.azati.warshipprocessing.sender.QueueSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.azati.warshipprocessing.util.VariableConstants.ACTIVE_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.SHOOT;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendShotRequestAction implements JavaDelegate {

    private final QueueSender queueSender;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Start of SendShotRequestAction method");

        var sessionId = UUID.fromString(delegateExecution.getBusinessKey());
        var activeUserId = (String) delegateExecution.getVariable(ACTIVE_USER_ID);

        var actionRequest = ProcessingMessage.builder()
                .sessionId(sessionId)
                .userId(activeUserId)
                .action(SHOOT)
                .build();

        queueSender.send(actionRequest);
        log.info("Shoot Request was send to user with id {}, waiting for shot coordinates", delegateExecution.getVariable(ACTIVE_USER_ID));
    }
}
