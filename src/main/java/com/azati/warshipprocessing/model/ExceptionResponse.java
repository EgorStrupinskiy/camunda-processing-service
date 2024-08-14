package com.azati.warshipprocessing.model;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ExceptionResponse(HttpStatus status, String message) {
}
