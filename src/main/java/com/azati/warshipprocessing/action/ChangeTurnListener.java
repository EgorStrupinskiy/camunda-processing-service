package com.azati.warshipprocessing.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static com.azati.warshipprocessing.util.VariableConstants.ACTIVE_USER_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeTurnListener implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Start of ChangeTurnAction method");
        log.info("User missed, current active user changed");

        var activeUserId = delegateExecution.getVariable(ACTIVE_USER_ID);
        log.info("New active user id: {}", activeUserId);
    }
}
