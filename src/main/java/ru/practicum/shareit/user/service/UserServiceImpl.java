package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAllBy(UserDto.class);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        try {
            User user = userRepository.save(UserMapper.toUser(userDto));
            return UserMapper.toUserDto(user);
        } catch (EmptyResultDataAccessException e) {
            throw new AlreadyExistsException("адрес почты уже используется");
        }
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User modifyingUser;
        Optional<User> o = userRepository.findById(userId);
        if (o.isPresent()) {
            modifyingUser = o.get();
        } else {
            throw new NoSuchBodyException("Запрашиваемый пользователь");
        }
        try {
            User user = UserMapper.toUser(userId, modifyingUser, userDto);
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (EmptyResultDataAccessException e) {
            throw new AlreadyExistsException("адрес почты уже используется");
        }
    }

    @Override
    public UserDto getUser(int id) {
        Optional<User> o = userRepository.findById(id);
        if (o.isPresent()) {
            return UserMapper.toUserDto(o.get());
        } else {
            throw new NoSuchBodyException("Запрашиваемый пользователь");
        }
    }

    @Override
    public void deleteUser(int userId) {
        getUser(userId);
        userRepository.deleteById(userId);
    }
}