package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(int userId, UserDto userDto);

    UserDto getUser(int userId);

    void deleteUser(int userId);
}