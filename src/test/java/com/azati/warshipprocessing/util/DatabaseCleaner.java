package com.azati.warshipprocessing.util;

import com.azati.warshipprocessing.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseCleaner {

    private final SessionRepository sessionRepository;

    public void cleanDatabase() {
        sessionRepository.deleteAll();
    }
}
