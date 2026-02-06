package com.sup.event_management.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    final String Message = "Something went wrong";

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {

        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getType(),
                ex.getSeverity(),
                ex.getStatus().value(),
                ex.getDetails()
        );

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception ex) {

        ErrorResponse response = new ErrorResponse(
                Message,
                ExceptionType.SYSTEM,
                ExceptionSeverity.FATAL,
                500,
                ex.getMessage()
        );

        return ResponseEntity.internalServerError().body(response);
    }
}
