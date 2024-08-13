package com.azati.warshipprocessing.model;

import lombok.NonNull;

import java.util.UUID;


public record CreateSessionRequest (UUID sessionId, @NonNull String firstUserId, String secondUserId) {}
