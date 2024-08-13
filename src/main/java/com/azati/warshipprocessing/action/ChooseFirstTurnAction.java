package com.azati.warshipprocessing.action;

import com.azati.warshipprocessing.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.azati.warshipprocessing.util.VariableConstants.ACTIVE_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.PASSIVE_USER_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseFirstTurnAction implements JavaDelegate {

    private final SessionService sessionService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Start of ChooseFirstTurn method");
        var sessionId = UUID.fromString(delegateExecution.getBusinessKey());
        var session = sessionService.getById(sessionId);

        delegateExecution.setVariable(ACTIVE_USER_ID, session.getFirstUserId());
        delegateExecution.setVariable(PASSIVE_USER_ID, session.getSecondUserId());

        log.info("First turn will be made by user with id {}", session.getFirstUserId());
    }
}
