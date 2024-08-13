package com.azati.warshipprocessing.service;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.entity.Session;
import com.azati.warshipprocessing.exception.NoSuchSessionException;
import com.azati.warshipprocessing.mapper.SessionMapper;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.repository.SessionRepository;
import com.azati.warshipprocessing.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionMapper sessionMapper;

    @Mock
    private ProcessService processService;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void shouldCreateNewSessionWithOnePlayerSuccessfullyWhenThereAreNoOpenSessions() {
        CreateSessionRequest request = new CreateSessionRequest(null, "userID", null);
        Session session = new Session(UUID.randomUUID(), request.firstUserId(), Instant.now());

        when(sessionRepository.findOpenSessions()).thenReturn(List.of());
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDTO(any(Session.class)))
                .thenReturn(new SessionDTO(session.getId(), session.getFirstUserId(), session.getSecondUserId(), session.getCreationDate()));

        SessionDTO sessionDTO = sessionService.create(request);

        assertNotNull(sessionDTO);
        assertEquals(request.firstUserId(), sessionDTO.getFirstUserId());
        assertNull(sessionDTO.getSecondUserId());

        verify(sessionRepository, times(1)).findOpenSessions();
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(sessionMapper, times(1)).toDTO(any(Session.class));
    }

    @Test
    void shouldCreateNewSessionWithTwoPlayersWithoutSessionIdSuccessfully() {
        CreateSessionRequest request = new CreateSessionRequest(null, "firstUserId", "firstUserId");
        Session session = new Session(UUID.randomUUID(), request.firstUserId(), request.secondUserId(), Instant.now());

        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDTO(any(Session.class)))
                .thenReturn(new SessionDTO(session.getId(), session.getFirstUserId(), session.getSecondUserId(), session.getCreationDate()));

        SessionDTO sessionDTO = sessionService.create(request);

        assertNotNull(sessionDTO);
        assertEquals(request.firstUserId(), sessionDTO.getFirstUserId());
        assertEquals(request.secondUserId(), sessionDTO.getSecondUserId());

        verify(sessionRepository, times(0)).findOpenSessions();
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(sessionMapper, times(1)).toDTO(any(Session.class));
    }

    @Test
    void shouldCreateNewSessionWithTwoPlayersWithSessionIdSuccessfully() {
        CreateSessionRequest request = new CreateSessionRequest(UUID.randomUUID(), "firstUserId", "firstUserId");
        Session session = new Session(UUID.randomUUID(), request.firstUserId(), request.secondUserId(), Instant.now());

        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDTO(any(Session.class)))
                .thenReturn(new SessionDTO(session.getId(), session.getFirstUserId(), session.getSecondUserId(), session.getCreationDate()));

        SessionDTO sessionDTO = sessionService.create(request);

        assertNotNull(sessionDTO);
        assertEquals(request.firstUserId(), sessionDTO.getFirstUserId());
        assertEquals(request.secondUserId(), sessionDTO.getSecondUserId());

        verify(sessionRepository, times(0)).findOpenSessions();
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(sessionMapper, times(1)).toDTO(any(Session.class));
    }

    @Test
    void shouldConnectToSessionSuccessfullyWhenThereAreOpenSessions() {
        CreateSessionRequest request = new CreateSessionRequest(null, "firstUserId", null);
        var existedSession = new Session(UUID.randomUUID(), "existedUserId", Instant.now());
        existedSession.setSecondUserId(request.firstUserId());

        when(sessionRepository.findOpenSessions())
                .thenReturn(List.of(existedSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(existedSession);
        when(sessionMapper.toDTO(any(Session.class)))
                .thenReturn(new SessionDTO(existedSession.getId(), existedSession.getFirstUserId(), existedSession.getSecondUserId(), existedSession.getCreationDate()));

        SessionDTO sessionDTO = sessionService.create(request);

        assertNotNull(sessionDTO);
        assertNotNull(sessionDTO.getId());
        assertNotNull(sessionDTO.getSecondUserId());
        assertNotNull(sessionDTO.getFirstUserId());

        verify(sessionRepository, times(1)).findOpenSessions();
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(sessionMapper, times(1)).toDTO(any(Session.class));
    }

    @Test
    void shouldThrowExceptionWhileConnectionWhenThereIsNoSessionWithId() {
        CreateSessionRequest request = new CreateSessionRequest(UUID.randomUUID(), "firstUserId", null);

        Mockito.when(sessionRepository.findById(request.sessionId()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchSessionException.class, () -> sessionService.create(request));
        verify(sessionRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void shouldConnectToSessionByIdSuccessfully() {
        CreateSessionRequest request = new CreateSessionRequest(UUID.randomUUID(), "firstUserId", null);
        var existedSession = new Session(request.sessionId(), "existedUserId", Instant.now());
        existedSession.setSecondUserId(request.firstUserId());

        when(sessionRepository.findById(request.sessionId()))
                .thenReturn(Optional.of(existedSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(existedSession);
        when(sessionMapper.toDTO(any(Session.class)))
                .thenReturn(new SessionDTO(existedSession.getId(), existedSession.getFirstUserId(), existedSession.getSecondUserId(), existedSession.getCreationDate()));

        SessionDTO sessionDTO = sessionService.create(request);

        assertNotNull(sessionDTO);
        assertNotNull(sessionDTO.getId());
        assertNotNull(sessionDTO.getSecondUserId());
        assertNotNull(sessionDTO.getFirstUserId());

        verify(sessionRepository, times(1)).findById(any(UUID.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(sessionMapper, times(1)).toDTO(any(Session.class));
    }

    @Test
    void shouldReturnOpenSessions() {
        var existedSession = new Session(UUID.randomUUID(), "existedUserId", Instant.now());

        when(sessionRepository.findOpenSessions())
                .thenReturn(List.of(existedSession));
        when(sessionMapper.toDTOList(any(List.class)))
                .thenReturn(List.of(new SessionDTO(existedSession.getId(), existedSession.getFirstUserId(),
                        existedSession.getSecondUserId(), existedSession.getCreationDate())));

        var sessions = sessionService.getOpen();
        assertEquals(1, sessions.size());
        assertEquals(1, sessions.size());

        verify(sessionRepository, times(1)).findOpenSessions();
        verify(sessionMapper, times(1)).toDTOList(any(List.class));
    }

    @Test
    void shouldReturnAllSessions() {
        var existedSession = new Session(UUID.randomUUID(), "existedUserId", Instant.now());

        when(sessionRepository.findAll())
                .thenReturn(List.of(existedSession));
        when(sessionMapper.toDTOList(any(List.class)))
                .thenReturn(List.of(new SessionDTO(existedSession.getId(), existedSession.getFirstUserId(),
                        existedSession.getSecondUserId(), existedSession.getCreationDate())));

        var sessions = sessionService.getAll();
        assertEquals(1, sessions.size());
        assertEquals(1, sessions.size());

        verify(sessionRepository, times(1)).findAll();
        verify(sessionMapper, times(1)).toDTOList(any(List.class));
    }

    @Test
    void shouldReturnSessionByIdSuccessfully() {
        var existedSession = new Session(UUID.randomUUID(), "existedFirstUserId",
                "existedSecondUserId", Instant.now());

        when(sessionRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(existedSession));
        when(sessionMapper.toDTO(any(Session.class)))
                .thenReturn(new SessionDTO(existedSession.getId(), existedSession.getFirstUserId(),
                        existedSession.getSecondUserId(), existedSession.getCreationDate()));

        var session = sessionService.getById(existedSession.getId());
        assertEquals("existedFirstUserId", session.getFirstUserId());
        assertEquals("existedSecondUserId", session.getSecondUserId());

        verify(sessionRepository, times(1)).findById(any(UUID.class));
        verify(sessionMapper, times(1)).toDTO(any(Session.class));
    }

    @Test
    void shouldThrowExceptionWhenThereIsNoSessionWithId() {

        when(sessionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());


        assertThrows(NoSuchSessionException.class, () -> sessionService.getById(UUID.randomUUID()));
        verify(sessionRepository, times(1)).findById(any(UUID.class));
    }
}
