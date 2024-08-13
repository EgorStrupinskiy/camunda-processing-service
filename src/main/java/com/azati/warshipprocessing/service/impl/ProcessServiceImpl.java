package com.azati.warshipprocessing.service.impl;

import com.azati.warshipprocessing.service.ProcessService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.azati.warshipprocessing.util.VariableConstants.PROCESS_ID;

@Service
public class ProcessServiceImpl implements ProcessService {

    @Override
    public void start(UUID sessionId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        runtimeService.createProcessInstanceByKey(PROCESS_ID)
                .businessKey(sessionId.toString())
                .executeWithVariablesInReturn();
    }
}
