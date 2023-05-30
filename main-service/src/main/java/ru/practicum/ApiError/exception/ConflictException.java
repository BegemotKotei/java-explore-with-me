package ru.practicum.ApiError.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String error) {
        super(error);
    }

}