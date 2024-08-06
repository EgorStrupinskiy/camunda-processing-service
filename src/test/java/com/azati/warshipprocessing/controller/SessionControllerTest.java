package com.azati.warshipprocessing.controller;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateSessionRequest createSessionRequest;
    private SessionDTO sessionDTO;

    @BeforeEach
    void setUp() {
        createSessionRequest = new CreateSessionRequest(null, "firstUserId");
        sessionDTO = new SessionDTO(UUID.randomUUID(), "firstUserId", "secondUserId", Instant.now());
    }

    @Test
    void shouldCreateSessionSuccessfully() throws Exception {
        Mockito.when(sessionService.create(createSessionRequest)).thenReturn(sessionDTO);

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSessionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(sessionDTO)));

        Mockito.verify(sessionService, Mockito.times(1)).create(createSessionRequest);
    }

    @Test
    void shouldGetSessionByIdSuccessfully() throws Exception {
        Mockito.when(sessionService.getById(sessionDTO.getId())).thenReturn(sessionDTO);

        mockMvc.perform(get("/sessions/{id}", sessionDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(sessionDTO)));

        Mockito.verify(sessionService, Mockito.times(1)).getById(sessionDTO.getId());
    }

    @Test
    void shouldGetAllSessionSuccessfully() throws Exception {
        Mockito.when(sessionService.getAll()).thenReturn(List.of(sessionDTO));

        mockMvc.perform(get("/sessions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(sessionDTO))));

        Mockito.verify(sessionService, Mockito.times(1)).getAll();
    }

    @Test
    void shouldGetAllOpenSessionSuccessfully() throws Exception {
        Mockito.when(sessionService.getOpen()).thenReturn(List.of(sessionDTO));

        mockMvc.perform(get("/sessions/open")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(sessionDTO))));

        Mockito.verify(sessionService, Mockito.times(1)).getOpen();
    }
}
