package com.azati.warshipprocessing.action;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.azati.warshipprocessing.sender.QueueSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static com.azati.warshipprocessing.util.VariableConstants.EXCEPTION;
import static com.azati.warshipprocessing.util.VariableConstants.PROCESSING_MESSAGE;
import static com.azati.warshipprocessing.util.VariableConstants.RECEIVED_MESSAGE_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.WRONG_REQUEST_TYPE;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendWrongRequestTypeExceptionAction implements JavaDelegate {

    private final QueueSender queueSender;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Start of SendWrongRequestTypeExceptionAction method");
        log.info("Incorrect request type, for player with id {}", delegateExecution.getVariable(RECEIVED_MESSAGE_USER_ID));

        var message = (ProcessingMessage) delegateExecution.getVariable(PROCESSING_MESSAGE);
        message.setAction(EXCEPTION);
        message.setStatus(WRONG_REQUEST_TYPE);

        queueSender.send(message);
    }
}
