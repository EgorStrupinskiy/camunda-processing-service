package com.azati.warshipprocessing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public final class ProcessingMessage {

    @NonNull
    private UUID sessionId;

    @NonNull
    private String userId;

    private int x;

    private int y;

    @NonNull
    private String action;

    private String status;
}

