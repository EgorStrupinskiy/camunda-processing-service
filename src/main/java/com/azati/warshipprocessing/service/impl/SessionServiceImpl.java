package com.azati.warshipprocessing.service.impl;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.entity.Session;
import com.azati.warshipprocessing.exception.NoSuchSessionException;
import com.azati.warshipprocessing.mapper.SessionMapper;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.repository.SessionRepository;
import com.azati.warshipprocessing.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Override
    public SessionDTO create(CreateSessionRequest request) {
        Session session;
        if (request.sessionId() == null) {
            session = handleNewSessionRequest(request);
        } else {
            session = handleExistingSessionRequest(request);
        }
        return sessionMapper.toDto(sessionRepository.save(session));
    }

    private Session handleNewSessionRequest(CreateSessionRequest request) {
        var sessions = sessionRepository.findOpenSessions();
        if (sessions.isEmpty()) {
            return new Session(UUID.randomUUID(), request.userId(), Instant.now());
        } else {
            Session session = sessions.get(0);
            session.setSecondUserId(request.userId());
            return session;
        }
    }

    private Session handleExistingSessionRequest(CreateSessionRequest request) {
        var existedSession = sessionRepository.findById(request.sessionId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchSessionException(String.format("Session not found with id: %s", request.sessionId())));
        existedSession.setSecondUserId(request.userId());
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
        return sessionMapper.toDto(sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchSessionException(String.format("Session with id %s not found", sessionId))));
    }
}
