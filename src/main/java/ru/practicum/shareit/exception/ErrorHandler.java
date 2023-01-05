package ru.practicum.shareit.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidBody(final ValidationException e) {
        return new ErrorResponse(String.format("Ошибка валидации: %s", e.getParameter()));
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidBody(final  MethodArgumentNotValidException e) {
        return new ErrorResponse(String.format("Ошибка валидации: %s", e.getParameter()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoAccess(final NoAccessException e) {
        return new ErrorResponse(String.format("Ошибка доступа: %s", e.getParameter()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistingBody(final AlreadyExistsException e) {
        return new ErrorResponse(String.format("Ошибка добавления: %s", e.getParameter()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundBody(final NoSuchBodyException e) {
        return new ErrorResponse(String.format("%s отсутствует или не найден", e.getParameter()));
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotFoundStatus(final UnsupportedStatusException e) {
        return new ErrorResponse(String.format("Unknown state: %s", e.getParameter()));
    }
}