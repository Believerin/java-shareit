package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NoAccessException extends RuntimeException {

    private final String parameter;

    public NoAccessException(String parameter) {
        this.parameter = parameter;
    }
}