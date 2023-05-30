package ru.practicum.ApiError;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.postgresql.util.PSQLException;
import ru.practicum.ApiError.exception.BadRequestException;
import ru.practicum.ApiError.exception.ConflictException;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.ApiError.exception.ValidationException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onConstraintViolationException(ConstraintViolationException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,
                "Невалидные данные.",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,
                "Incorrectly made request (SQL).",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(PSQLException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse onBusyEmail(PSQLException e) {
        return new ErrorResponse(HttpStatus.CONFLICT,
                "Incorrectly made request (SQL).",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse onValidationException(ValidationException e) {
        return new ErrorResponse(HttpStatus.FORBIDDEN,
                "Событие не удовлетворяет правилам создания.",
                e.getMessage(),
                LocalDateTime.now());
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,
                "Такого метода нет.",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onBadRequestException(BadRequestException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST,
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse onConflictException(ConflictException e) {
        return new ErrorResponse(HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse onNotFoundException(NotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now());
    }

}