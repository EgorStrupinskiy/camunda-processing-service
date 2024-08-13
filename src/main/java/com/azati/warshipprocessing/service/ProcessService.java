package com.azati.warshipprocessing.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ProcessService {

    void start(UUID sessionId);

}
