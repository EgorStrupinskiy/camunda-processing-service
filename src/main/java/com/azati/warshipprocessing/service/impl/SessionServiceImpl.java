package com.azati.warshipprocessing.service.impl;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.entity.Session;
import com.azati.warshipprocessing.exception.NoSuchSessionException;
import com.azati.warshipprocessing.mapper.SessionMapper;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.repository.SessionRepository;
import com.azati.warshipprocessing.service.ProcessService;
import com.azati.warshipprocessing.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final ProcessService processService;

    @Override
    public SessionDTO create(CreateSessionRequest request) {
        Session session;
        if (request.sessionId() == null) {
            session = handleNewSessionRequest(request);
        } else {
            session = handleExistingSessionRequest(request);
        }
        var sessionEntity = sessionRepository.save(session);
        if (sessionEntity.getSecondUserId() != null) {
            processService.start(sessionEntity.getId());
        }
        return sessionMapper.toDTO(sessionEntity);
    }

    private Session handleNewSessionRequest(CreateSessionRequest request) {
        if (request.secondUserId() != null) {
            return Session.builder()
                    .firstUserId(request.firstUserId())
                    .secondUserId(request.secondUserId())
                    .build();
        }
        var sessions = sessionRepository.findOpenSessions();
        if (sessions.isEmpty()) {
            return Session.builder()
                    .firstUserId(request.firstUserId())
                    .build();
        } else {
            Session session = sessions.get(0);
            session.setSecondUserId(request.firstUserId());
            return session;
        }
    }

    private Session handleExistingSessionRequest(CreateSessionRequest request) {
        if (request.secondUserId() != null) {
            return handleNewSessionRequest(request);
        }
        var existedSession = sessionRepository.findById(request.sessionId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchSessionException(String.format("Session not found with id: %s", request.sessionId())));
        existedSession.setSecondUserId(request.firstUserId());
        return existedSession;
    }

    @Override
    public List<SessionDTO> getOpen() {
        return sessionMapper.toDTOList(sessionRepository.findOpenSessions());
    }

    @Override
    public List<SessionDTO> getAll() {
        return sessionMapper.toDTOList(sessionRepository.findAll());
    }

    @Override
    public SessionDTO getById(UUID sessionId) {
        return sessionMapper.toDTO(sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchSessionException(String.format("Session with id %s not found", sessionId))));
    }
}
