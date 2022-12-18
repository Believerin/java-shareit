package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    User addUser(User user);

    User modifyUser(int userId, UserDto userDto);

    UserDto getUser(int userId);

    void deleteUser(int userId);
}