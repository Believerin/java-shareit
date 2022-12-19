package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDbStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDbStorage userStorage;

    public UserServiceImpl(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public User addUser(User user) {
        User o = userStorage.addUser(user);
        if (o == null) {
            throw new AlreadyExistsException("адрес почты уже используется");
        }
        return o;
    }

    @Override
    public User modifyUser(int userId, UserDto userDto) {
        User modifyingUser = userStorage.getUser(userId);
        User user = userStorage.modifyUser(userId, UserMapper.toUser(userId, modifyingUser, userDto));
        if (user == null) {
            throw new AlreadyExistsException("адрес почты уже используется");
        }
        return user;
    }

    @Override
    public UserDto getUser(int userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NoSuchBodyException("Запрашиваемый пользователь");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(int userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NoSuchBodyException("Запрашиваемый пользователь");
        }
        userStorage.deleteUser(userId);
    }
}