package com.azati.warshipprocessing.exception.handler;

import com.azati.warshipprocessing.exception.NoSuchSessionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchSessionException.class)
    public ResponseEntity<String> handleNoSuchSessionException(NoSuchSessionException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}