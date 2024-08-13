package com.azati.warshipprocessing.integration;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.entity.Session;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.repository.SessionRepository;
import com.azati.warshipprocessing.util.DatabaseCleaner;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static com.azati.warshipprocessing.util.TestVariableConstants.FIRST_USER_ID;
import static com.azati.warshipprocessing.util.TestVariableConstants.SECOND_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @PostConstruct
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @BeforeEach
    void setUpSession() {
        databaseCleaner.cleanDatabase();
    }

    @Test
    void shouldCreateNewSessionWithTwoPlayersWithoutSessionIdSuccessfully() {

        CreateSessionRequest request = new CreateSessionRequest(null, FIRST_USER_ID, SECOND_USER_ID);

        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getFirstUserId()).isEqualTo(FIRST_USER_ID);
        assertThat(response.getBody().getSecondUserId()).isEqualTo(SECOND_USER_ID);
    }

    @Test
    void shouldCreateNewSessionWithTwoPlayersWithSessionIdSuccessfully() {
        var startSessionId = UUID.randomUUID();
        CreateSessionRequest request = new CreateSessionRequest(startSessionId, FIRST_USER_ID, SECOND_USER_ID);

        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getId()).isNotEqualTo(startSessionId);
        assertThat(response.getBody().getFirstUserId()).isEqualTo(FIRST_USER_ID);
        assertThat(response.getBody().getSecondUserId()).isEqualTo(SECOND_USER_ID);
    }

    @Test
    void shouldCreateNewSessionWithOnePlayerSuccessfullyWhenThereAreNoOpenSessions() {

        CreateSessionRequest request = new CreateSessionRequest(null, FIRST_USER_ID, null);

        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getFirstUserId()).isEqualTo(FIRST_USER_ID);
        assertThat(response.getBody().getSecondUserId()).isNull();
    }

    @Test
    void shouldConnectToSessionSuccessfullyWhenThereAreOpenSessions() {
        var existedSessionId = UUID.randomUUID();
        sessionRepository.save(new Session(existedSessionId, FIRST_USER_ID, null, Instant.now()));

        CreateSessionRequest request = new CreateSessionRequest(null, SECOND_USER_ID, null);

        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(existedSessionId);
        assertThat(response.getBody().getFirstUserId()).isEqualTo(FIRST_USER_ID);
        assertThat(response.getBody().getSecondUserId()).isEqualTo(SECOND_USER_ID);
    }

    @Test
    void shouldConnectToSessionByIdSuccessfully() {
        var existedSessionId = UUID.randomUUID();
        sessionRepository.save(new Session(existedSessionId, FIRST_USER_ID, null, Instant.now()));

        CreateSessionRequest request = new CreateSessionRequest(existedSessionId, SECOND_USER_ID, null);

        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(existedSessionId);
        assertThat(response.getBody().getFirstUserId()).isEqualTo(FIRST_USER_ID);
        assertThat(response.getBody().getSecondUserId()).isEqualTo(SECOND_USER_ID);
    }

    @Test
    void shouldThrowExceptionWhileConnectionWhenThereIsNoSessionWithId() {
        // Arrange
        var notFoundSessionId = UUID.randomUUID();
        CreateSessionRequest request = new CreateSessionRequest(notFoundSessionId, "newUserId", null);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/sessions", request, String.class);
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

}