package com.sup.event_management.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MESSAGE = "Something went wrong";

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
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) throws Exception {

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        if (path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-ui.html") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars")) {
            throw ex;
        }

        ErrorResponse response = new ErrorResponse(
                MESSAGE,
                ExceptionType.SYSTEM,
                ExceptionSeverity.FATAL,
                500,
                ex.getMessage()
        );

        return ResponseEntity.internalServerError().body(response);
    }
}
