package com.azati.warshipprocessing.controller;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<SessionDTO>> getOpenSessions() {
        return ResponseEntity.ok(sessionService.getOpen());
    }

    @GetMapping
    public ResponseEntity<List<SessionDTO>> getSessions() {
        return ResponseEntity.ok(sessionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSessionById(@PathVariable UUID id) {
        return ResponseEntity.ok(sessionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SessionDTO> createSession(@RequestBody CreateSessionRequest request) {
        return ResponseEntity.ok(sessionService.create(request));
    }
}
