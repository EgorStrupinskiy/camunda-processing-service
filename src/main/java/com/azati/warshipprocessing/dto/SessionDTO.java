package com.azati.warshipprocessing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {

    @NonNull
    private UUID id;

    @NonNull
    private String firstUserId;

    private String secondUserId;

    @NonNull
    private Instant creationDate;
}
