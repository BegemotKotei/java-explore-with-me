package ru.practicum.exceptions;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import ru.practicum.DateConstants;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final HttpStatus status;

    private final String reason;

    private final String message;

    @DateTimeFormat(pattern = DateConstants.DATE_PATTERN)
    private final String timestamp;

    public ErrorResponse(HttpStatus status, String reason, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.reason = reason;
        this.timestamp = timestamp.format(DateConstants.DTF);
    }

}