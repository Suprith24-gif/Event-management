package com.sup.event_management.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private String message;
    private ExceptionType type;
    private ExceptionSeverity severity;
    private int status;
    private String details;
    private LocalDateTime timestamp;

    public ErrorResponse(String message,
                         ExceptionType type,
                         ExceptionSeverity severity,
                         int status,
                         String details) {
        this.message = message;
        this.type = type;
        this.severity = severity;
        this.status = status;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
