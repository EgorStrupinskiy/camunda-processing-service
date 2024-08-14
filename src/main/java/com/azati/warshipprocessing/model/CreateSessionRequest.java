package com.azati.warshipprocessing.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Request to create a session or to join existing one")
public record CreateSessionRequest (
        @Schema(description = "Session id", example = "c4335079-2395-4604-b710-94d5db643b56", nullable = true) UUID sessionId,
        @Schema(description = "First user id", example = "firstUserId") @NotNull String firstUserId,
        @Schema(description = "Second user id", example = "secondUserId", nullable = true) String secondUserId) {}
