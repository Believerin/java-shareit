package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    UserDto add(UserDto userDto);

    UserDto update(int userId, UserDto userDto);

    UserDto get(int userId);

    void delete(int userId);
}