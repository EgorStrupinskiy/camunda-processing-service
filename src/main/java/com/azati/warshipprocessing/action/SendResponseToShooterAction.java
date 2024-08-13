package com.azati.warshipprocessing.action;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.azati.warshipprocessing.sender.QueueSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static com.azati.warshipprocessing.util.VariableConstants.ACTIVE_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.PROCESSING_MESSAGE;
import static com.azati.warshipprocessing.util.VariableConstants.SHOT_INFO;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendResponseToShooterAction implements JavaDelegate {

    private final QueueSender queueSender;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Start of SendResponseToShooterAction method");


        var message = (ProcessingMessage) delegateExecution.getVariable(PROCESSING_MESSAGE);
        var activeUserId = (String) delegateExecution.getVariable(ACTIVE_USER_ID);

        message.setAction(SHOT_INFO);
        message.setStatus(message.getStatus());
        message.setUserId(activeUserId);

        queueSender.send(message);
        log.info("Shot response was send to user with id {}, his action status: {}", activeUserId, message.getStatus());
    }
}
