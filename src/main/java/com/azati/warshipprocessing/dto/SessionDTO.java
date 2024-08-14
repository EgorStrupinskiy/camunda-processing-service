package com.azati.warshipprocessing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Session DTO")
@AllArgsConstructor
public class SessionDTO {

    @NotNull
    private UUID id;

    @NotNull
    private String firstUserId;

    private String secondUserId;

    @NotNull
    private Instant creationDate;
}
