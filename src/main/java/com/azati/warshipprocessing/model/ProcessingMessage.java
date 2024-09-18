package com.azati.warshipprocessing.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class ProcessingMessage {

    @NotNull
    private UUID sessionId;

    @NotNull
    private String userId;

    private Integer x;

    private Integer y;

    @NotNull
    private String action;

    private String status;
}

