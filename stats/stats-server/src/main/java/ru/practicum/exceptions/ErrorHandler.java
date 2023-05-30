package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.BadRequest;


import java.time.LocalDateTime;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(BadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onConstraintViolationException(BadRequest e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,
                "Некорректный запрос.",
                e.getMessage(),
                LocalDateTime.now());
    }

}