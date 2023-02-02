package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoCreated;

import java.util.Collection;

public interface RequestService {

    ResponseDtoCreated add(RequestDto requestDto, int userId);

    ResponseDto get(int userId, int requestId);

    Collection<ResponseDto> getAllOwn(int userId, int from, int size);

    Collection<ResponseDto> getAll(int userId, int from, int size);
}