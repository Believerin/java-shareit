package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException {

    private final String parameter;

    public AlreadyExistsException(String parameter) {
        this.parameter = parameter;
    }
}