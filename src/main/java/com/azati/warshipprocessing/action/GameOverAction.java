package com.azati.warshipprocessing.action;

import com.azati.warshipprocessing.model.ProcessingMessage;
import com.azati.warshipprocessing.sender.QueueSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static com.azati.warshipprocessing.util.VariableConstants.ACTIVE_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.LOOSE;
import static com.azati.warshipprocessing.util.VariableConstants.PASSIVE_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.PROCESSING_MESSAGE;
import static com.azati.warshipprocessing.util.VariableConstants.WIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameOverAction implements JavaDelegate {

    private final QueueSender queueSender;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Start of GameOverActivity method");
        log.info("GAME OVER!!!!");

        var message = (ProcessingMessage) delegateExecution.getVariable(PROCESSING_MESSAGE);
        var passiveUserId = (String) delegateExecution.getVariable(PASSIVE_USER_ID);

        message.setAction(LOOSE);
        message.setUserId(passiveUserId);
        queueSender.send(message);
        log.info("GameOver message was send to looser");

        var activeUserId = (String) delegateExecution.getVariable(ACTIVE_USER_ID);
        message.setAction(WIN);
        message.setUserId(activeUserId);
        queueSender.send(message);
        log.info("GameOver message was send to winner");
    }
}
