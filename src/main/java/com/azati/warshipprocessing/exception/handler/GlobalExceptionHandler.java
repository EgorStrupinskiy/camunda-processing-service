package com.azati.warshipprocessing.exception.handler;

import com.azati.warshipprocessing.exception.NoSuchSessionException;
import com.azati.warshipprocessing.exception.SameUsersInSessionException;
import com.azati.warshipprocessing.exception.UserAlreadyInSessionException;
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

    @ExceptionHandler(UserAlreadyInSessionException.class)
    public ResponseEntity<ExceptionResponse> handleUserAlreadyInSessionException(UserAlreadyInSessionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(SameUsersInSessionException.class)
    public ResponseEntity<ExceptionResponse> handleSameUsersInSessionException(SameUsersInSessionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
}