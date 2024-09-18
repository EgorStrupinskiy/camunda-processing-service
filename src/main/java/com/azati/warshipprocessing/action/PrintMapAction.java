package com.azati.warshipprocessing.action;

import com.azati.warshipprocessing.exception.NoSuchCellException;
import com.azati.warshipprocessing.model.ProcessingMessage;
import com.azati.warshipprocessing.repository.MapCellRepository;
import com.azati.warshipprocessing.service.SessionService;
import com.azati.warshipprocessing.util.MapPrinter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static com.azati.warshipprocessing.util.VariableConstants.PROCESSING_MESSAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrintMapAction implements JavaDelegate {

    private final MapPrinter mapPrinter;
    private final SessionService sessionService;
    private final MapCellRepository mapCellRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Start of PrintMapAction method");
        var message = (ProcessingMessage) delegateExecution.getVariable(PROCESSING_MESSAGE);

        var gameMapCell = mapCellRepository.findByFields(
                        message.getX(),
                        message.getY(),
                        message.getUserId(),
                        message.getSessionId())
                .orElseThrow(NoSuchCellException::new);
        gameMapCell.setStatus(message.getStatus());

        mapCellRepository.save(gameMapCell);
        mapPrinter.printMap(sessionService.getById(message.getSessionId()));
    }
}
