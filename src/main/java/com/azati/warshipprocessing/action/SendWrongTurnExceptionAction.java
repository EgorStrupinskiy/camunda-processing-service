package com.azati.warshipprocessing.action;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.azati.warshipprocessing.sender.QueueSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static com.azati.warshipprocessing.util.VariableConstants.ACTIVE_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.EXCEPTION;
import static com.azati.warshipprocessing.util.VariableConstants.INCORRECT_TURN;
import static com.azati.warshipprocessing.util.VariableConstants.PROCESSING_MESSAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendWrongTurnExceptionAction implements JavaDelegate {

    private final QueueSender queueSender;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        var message = (ProcessingMessage) delegateExecution.getVariable(PROCESSING_MESSAGE);
        message.setAction(EXCEPTION);
        message.setStatus(INCORRECT_TURN);

        log.info("Start of SendWrongTurnExceptionAction method");
        log.info("Only active user can make turn now, waiting for a message from user with id {}", delegateExecution.getVariable(ACTIVE_USER_ID));
        log.info("Received message from user with id {}", message.getUserId());

        queueSender.send(message);
    }
}
