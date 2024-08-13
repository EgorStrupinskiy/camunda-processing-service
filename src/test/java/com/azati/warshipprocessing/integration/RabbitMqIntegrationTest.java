package com.azati.warshipprocessing.integration;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.messaging.ResponseQueueSender;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.model.ProcessingMessage;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.azati.warshipprocessing.util.TestVariableConstants.ACTION_QUEUE_NAME;
import static com.azati.warshipprocessing.util.TestVariableConstants.FIRST_USER_ID;
import static com.azati.warshipprocessing.util.TestVariableConstants.HIT;
import static com.azati.warshipprocessing.util.TestVariableConstants.MISS;
import static com.azati.warshipprocessing.util.TestVariableConstants.SECOND_USER_ID;
import static com.azati.warshipprocessing.util.VariableConstants.GAME_OVER;
import static com.azati.warshipprocessing.util.VariableConstants.INCORRECT_TURN;
import static com.azati.warshipprocessing.util.VariableConstants.LOOSE;
import static com.azati.warshipprocessing.util.VariableConstants.RESPONSE;
import static com.azati.warshipprocessing.util.VariableConstants.SHOOT;
import static com.azati.warshipprocessing.util.VariableConstants.SHOT_INFO;
import static com.azati.warshipprocessing.util.VariableConstants.WIN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.camunda.bpm.admin.impl.plugin.resources.MetricsRestService.objectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RabbitMqIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ResponseQueueSender responseQueueSender;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @PostConstruct
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @BeforeEach
    void beforeEach() {
        rabbitTemplate.execute(channel -> {
            channel.queuePurge("action-queue");
            channel.queuePurge("response-queue");
            return null;
        });
    }

    @Test
    void testCorrectGameRoundWithAMiss() {
        CreateSessionRequest request = new CreateSessionRequest(null, FIRST_USER_ID, SECOND_USER_ID);
        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(FIRST_USER_ID, response.getBody().getFirstUserId());
        assertEquals(SECOND_USER_ID, response.getBody().getSecondUserId());
        var sessionId = response.getBody().getId();

        //wait for first shot request
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOOT, parsedMessage.getAction());
        });
        //send first shot request
        ProcessingMessage firstMessage = new ProcessingMessage(sessionId, FIRST_USER_ID, SHOOT);
        responseQueueSender.send(firstMessage);

        //wait for request to response to a shot for another player
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(SECOND_USER_ID, parsedMessage.getUserId());
            assertEquals(RESPONSE, parsedMessage.getAction());
        });

        //sending shot response, it`s a miss
        ProcessingMessage secondMessage = new ProcessingMessage(sessionId, SECOND_USER_ID, 1, 1, RESPONSE, MISS);
        responseQueueSender.send(secondMessage);

        //wait for a shot status for firstUser, it must be a miss
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOT_INFO, parsedMessage.getAction());
            assertEquals(MISS, parsedMessage.getStatus());
        });

        //wait for second player shot request
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(SECOND_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOOT, parsedMessage.getAction());
        });
    }

    @Test
    void testCorrectGameRoundWithAHit() {
        CreateSessionRequest request = new CreateSessionRequest(null, FIRST_USER_ID, SECOND_USER_ID);
        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(FIRST_USER_ID, response.getBody().getFirstUserId());
        assertEquals(SECOND_USER_ID, response.getBody().getSecondUserId());
        var sessionId = response.getBody().getId();

        //wait for first shot request
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOOT, parsedMessage.getAction());
        });
        //send first shot request
        ProcessingMessage firstMessage = new ProcessingMessage(sessionId, FIRST_USER_ID, SHOOT);
        responseQueueSender.send(firstMessage);

        //wait for request to response to a shot for another player
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(SECOND_USER_ID, parsedMessage.getUserId());
            assertEquals(RESPONSE, parsedMessage.getAction());
        });

        //sending shot response, it`s a miss
        ProcessingMessage secondMessage = new ProcessingMessage(sessionId, SECOND_USER_ID, 1, 1, RESPONSE, HIT);
        responseQueueSender.send(secondMessage);

        //wait for a shot status for firstUser, it must be a hit
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOT_INFO, parsedMessage.getAction());
            assertEquals(HIT, parsedMessage.getStatus());
        });

        //wait for one more shot request for first player
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOOT, parsedMessage.getAction());
        });
    }

    @Test
    void testExceptionWhenWrongUserIsShooting() {
        CreateSessionRequest request = new CreateSessionRequest(null, FIRST_USER_ID, SECOND_USER_ID);
        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(FIRST_USER_ID, response.getBody().getFirstUserId());
        assertEquals(SECOND_USER_ID, response.getBody().getSecondUserId());
        var sessionId = response.getBody().getId();

        //wait for first shot request
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOOT, parsedMessage.getAction());
        });
        //send first shot request
        ProcessingMessage firstMessage = new ProcessingMessage(sessionId, SECOND_USER_ID, SHOOT);
        responseQueueSender.send(firstMessage);

        //wait for wrong user request message
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(SECOND_USER_ID, parsedMessage.getUserId());
            assertEquals(INCORRECT_TURN, parsedMessage.getAction());
        });
    }

    @Test
    void testExceptionWhenUserIsRespondingInsteadOfShooting() {
        CreateSessionRequest request = new CreateSessionRequest(null, FIRST_USER_ID, SECOND_USER_ID);
        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(FIRST_USER_ID, response.getBody().getFirstUserId());
        assertEquals(SECOND_USER_ID, response.getBody().getSecondUserId());
        var sessionId = response.getBody().getId();

        //wait for first shot request
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOOT, parsedMessage.getAction());
        });
        //send first shot request
        ProcessingMessage firstMessage = new ProcessingMessage(sessionId, SECOND_USER_ID, RESPONSE);
        responseQueueSender.send(firstMessage);

        //wait for wrong user request message
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(SECOND_USER_ID, parsedMessage.getUserId());
            assertEquals(INCORRECT_TURN, parsedMessage.getAction());
        });
    }

    @Test
    void testExceptionWhenIncorrectUserIsResponding() {
        CreateSessionRequest request = new CreateSessionRequest(null, FIRST_USER_ID, SECOND_USER_ID);
        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(FIRST_USER_ID, response.getBody().getFirstUserId());
        assertEquals(SECOND_USER_ID, response.getBody().getSecondUserId());
        var sessionId = response.getBody().getId();

        //wait for first shot request
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOOT, parsedMessage.getAction());
        });
        //send first shot request
        ProcessingMessage firstMessage = new ProcessingMessage(sessionId, FIRST_USER_ID, SHOOT);
        responseQueueSender.send(firstMessage);

        //wait for request to response to a shot for another player
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(SECOND_USER_ID, parsedMessage.getUserId());
            assertEquals(RESPONSE, parsedMessage.getAction());
        });

        //sending response with incorrect user id
        ProcessingMessage secondMessage = new ProcessingMessage(sessionId, FIRST_USER_ID, 1, 1, RESPONSE, null);
        responseQueueSender.send(secondMessage);

        //wait for wrong request type message
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(INCORRECT_TURN, parsedMessage.getAction());
        });
    }

    @Test
    void testCorrectGameWithGameOver() {
        CreateSessionRequest request = new CreateSessionRequest(null, FIRST_USER_ID, SECOND_USER_ID);
        ResponseEntity<SessionDTO> response = restTemplate.postForEntity(baseUrl + "/sessions", request, SessionDTO.class);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(FIRST_USER_ID, response.getBody().getFirstUserId());
        assertEquals(SECOND_USER_ID, response.getBody().getSecondUserId());
        var sessionId = response.getBody().getId();

        //wait for first shot request
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(SHOOT, parsedMessage.getAction());
        });
        //send first shot request
        ProcessingMessage firstMessage = new ProcessingMessage(sessionId, FIRST_USER_ID, SHOOT);
        responseQueueSender.send(firstMessage);

        //wait for request to response to a shot for another player
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(SECOND_USER_ID, parsedMessage.getUserId());
            assertEquals(RESPONSE, parsedMessage.getAction());
        });

        //sending shot response, it`s a game over
        ProcessingMessage secondMessage = new ProcessingMessage(sessionId, SECOND_USER_ID, 1, 1, RESPONSE, GAME_OVER);
        responseQueueSender.send(secondMessage);

        //wait for a game status for firstUser, it must be a win
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(SECOND_USER_ID, parsedMessage.getUserId());
            assertEquals(LOOSE, parsedMessage.getAction());
        });

        //wait for a game status for second User, it must be a loose
        await().atMost(10, SECONDS).untilAsserted(() -> {
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(ACTION_QUEUE_NAME);
            assertNotNull(receivedMessage);
            ProcessingMessage parsedMessage = objectMapper.readValue(receivedMessage, ProcessingMessage.class);
            assertEquals(sessionId, parsedMessage.getSessionId());
            assertEquals(FIRST_USER_ID, parsedMessage.getUserId());
            assertEquals(WIN, parsedMessage.getAction());
        });
    }
}