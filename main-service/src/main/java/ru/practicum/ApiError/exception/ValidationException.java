package ru.practicum.ApiError.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String error) {
        super(error);
    }

}