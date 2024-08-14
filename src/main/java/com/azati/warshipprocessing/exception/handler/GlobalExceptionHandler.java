package com.azati.warshipprocessing.exception.handler;

import com.azati.warshipprocessing.exception.NoSuchSessionException;
import com.azati.warshipprocessing.model.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchSessionException.class)
    public ResponseEntity<ExceptionResponse> handleNoSuchSessionException(NoSuchSessionException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(HttpStatus.NOT_FOUND, e.getMessage()));
    }
}