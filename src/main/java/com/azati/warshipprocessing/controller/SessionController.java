package com.azati.warshipprocessing.controller;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.model.CreateSessionRequest;
import com.azati.warshipprocessing.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@Tag(name = "Session controller", description = "Controller for session management")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/open")
    @Operation(
            summary = "Get all open sessions",
            description = "Method to get all sessions, where there is only one player"
    )
    public ResponseEntity<List<SessionDTO>> getOpenSessions() {
        return ResponseEntity.ok(sessionService.getOpen());
    }

    @GetMapping
    @Operation(
            summary = "Get all sessions",
            description = "Method to get all sessions"
    )
    public ResponseEntity<List<SessionDTO>> getSessions() {
        return ResponseEntity.ok(sessionService.getAll());
    }

    @GetMapping("/{id}")
    @PostMapping
    @Operation(
            summary = "Get session by id",
            description = "Method to get existing session by id"
    )
    public ResponseEntity<SessionDTO> getSessionById(
            @PathVariable @Parameter(description = "Session id") UUID id) {
        return ResponseEntity.ok(sessionService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create new session",
            description = "Method to create new session or to join existing session",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(examples = {
                            @ExampleObject(name = "Created session entity",
                                    description = "Retrieves a created session.",
                                    value = "{\"id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", " +
                                            "\"firstUserId\":\"firstUserId\", " +
                                            "\"secondUserId\":\"secondUserId\", " +
                                            "\"creationDate\":\"2024-08-14T08:02:58.305Z\"}"
                            ),
                    }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", content = @Content(examples = {
                            @ExampleObject(name = "No such session exception",
                                    description = "User will get exception when trying to connect to session by id, when there is no such session.",
                                    value = "{\"status\":\"NOT_FOUND\", " +
                                            "\"message\":\"Session with id c4335079-2395-4604-b710-94d5db653b56 not found\"}"
                            ),
                    }, mediaType = MediaType.APPLICATION_JSON_VALUE))
            })
    public ResponseEntity<SessionDTO> createSession(@RequestBody CreateSessionRequest request) {
        return ResponseEntity.ok(sessionService.create(request));
    }
}
