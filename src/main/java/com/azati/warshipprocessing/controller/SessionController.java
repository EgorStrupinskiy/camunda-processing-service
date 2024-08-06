package com.azati.warshipprocessing.controller;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/open")
    public List<SessionDTO> getOpenSessions() {
        return sessionService.getOpen();
    }

    @GetMapping
    public List<SessionDTO> getSessions() {
        return sessionService.getAll();
    }

    @GetMapping("/{id}")
    public SessionDTO getSessionById(@PathVariable UUID id) {
        return sessionService.getById(id);
    }

    @PostMapping
    public SessionDTO createSession(@RequestBody CreateSessionRequest request) {
        return sessionService.create(request);
    }
}
