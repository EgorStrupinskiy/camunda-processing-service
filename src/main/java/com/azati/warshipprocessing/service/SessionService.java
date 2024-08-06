package com.azati.warshipprocessing.service;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.model.CreateSessionRequest;

import java.util.List;
import java.util.UUID;

public interface SessionService {
    List<SessionDTO> getOpen();

    SessionDTO create(CreateSessionRequest request);

    List<SessionDTO> getAll();

    SessionDTO getById(UUID sessionId);
}
