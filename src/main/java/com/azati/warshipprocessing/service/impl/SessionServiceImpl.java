package com.azati.warshipprocessing.service.impl;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.entity.MapCell;
import com.azati.warshipprocessing.entity.Session;
import com.azati.warshipprocessing.exception.NoSuchSessionException;
import com.azati.warshipprocessing.exception.SameUsersInSessionException;
import com.azati.warshipprocessing.exception.UserAlreadyInSessionException;
import com.azati.warshipprocessing.mapper.SessionMapper;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.repository.MapCellRepository;
import com.azati.warshipprocessing.repository.SessionRepository;
import com.azati.warshipprocessing.service.ProcessService;
import com.azati.warshipprocessing.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.azati.warshipprocessing.util.VariableConstants.GAME_MAP_CELLS_COUNT;
import static com.azati.warshipprocessing.util.VariableConstants.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final ProcessService processService;
    private final MapCellRepository mapCellRepository;

    @Override
    public SessionDTO create(CreateSessionRequest request) {
        Session session;
        //look, if request has a sessionId
        if (request.sessionId() == null) {
            session = handleNewSessionRequest(request);
        } else {
            session = handleExistingSessionRequest(request);
        }
        var sessionEntity = sessionRepository.save(session);
        log.info("SessionId: {}", sessionEntity.getId());

        //Check if we can start the game
        if (sessionEntity.getSecondUserId() != null) {
            log.info("Session with two players created successfully, starting new game");
            processService.start(sessionEntity.getId());
            createGameMaps(sessionEntity);
        }
        return sessionMapper.toDTO(sessionEntity);
    }

    private Session handleNewSessionRequest(CreateSessionRequest request) {
        //Check if the request has secondUserId, so we need to create session instantly
        if (request.secondUserId() != null) {
            log.info("Create new session with two players");
            if (request.firstUserId().equals(request.secondUserId())) {
                log.error("There can`t be a session with two users with same id`s");
                throw new SameUsersInSessionException("There can`t be two players with the same id in one session");
            }
            return Session.builder()
                    .firstUserId(request.firstUserId())
                    .secondUserId(request.secondUserId())
                    .build();
        }
        var sessions = sessionRepository.findOpenSessions();
        if (sessions.isEmpty()) {
            log.info("There are no open sessions, create new session with one player");
            return Session.builder()
                    .firstUserId(request.firstUserId())
                    .build();
        } else {
            //Try to find open session without this user
            var session = sessions.stream()
                    .filter(s -> !s.getFirstUserId().equals(request.firstUserId()))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("There is already an open session for user with id {}", request.firstUserId());
                        return new UserAlreadyInSessionException(String.format("User with id %s is already in session", request.firstUserId()));
                    });
            session.setSecondUserId(request.firstUserId());
            log.info("Connected player to open session with id {}", session.getId());
            return session;
        }
    }

    private Session handleExistingSessionRequest(CreateSessionRequest request) {
        if (request.secondUserId() != null) {
            return handleNewSessionRequest(request);
        }
        log.info("Connecting to session with id {}", request.sessionId());
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

    private void createGameMaps(Session session) {
        var map = new ArrayList<MapCell>();
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < GAME_MAP_CELLS_COUNT; i++) {
                for (int j = 0; j < GAME_MAP_CELLS_COUNT; j++) {
                    map.add(MapCell.builder()
                            .x(j)
                            .y(i)
                            .sessionId(session.getId())
                            .userId(k == 0 ? session.getFirstUserId() : session.getSecondUserId())
                            .status(OK)
                            .build());
                }
            }
        }
        mapCellRepository.saveAll(map);
    }
}
