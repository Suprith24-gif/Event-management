package com.sup.event_management.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    private final ExceptionType type;
    private final ExceptionSeverity severity;
    private final HttpStatus status;
    private final String details;

    public AppException(String message,
                        ExceptionType type,
                        ExceptionSeverity severity,
                        HttpStatus status,
                        String details) {
        super(message);
        this.type = type;
        this.severity = severity;
        this.status = status;
        this.details = details;
    }
}
