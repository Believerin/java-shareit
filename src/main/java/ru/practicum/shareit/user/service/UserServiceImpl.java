package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

//@Transactional
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    private TransactionTemplate template;

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(int userId, UserDto userDto) {
        User actualUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchBodyException("Запрашиваемый пользователь"));
        return UserMapper.toUserDto(toUser(userId, actualUser, userDto));
    }

    @Override
    public UserDto get(int id) {
        Optional<User> o = userRepository.findById(id);
        if (o.isPresent()) {
            return UserMapper.toUserDto(o.get());
        } else {
            throw new NoSuchBodyException("Запрашиваемый пользователь");
        }
    }

    @Transactional
    @Override
    public void delete(int userId) {
        get(userId);
        userRepository.deleteById(userId);
    }

    //--------------------------------------Служебный метод-------------------------------------------------

    public static User toUser(int userId, User updatingUser, UserDto userDto) {
        updatingUser.setId(userId);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatingUser.setName(userDto.getName());
        } else if (userDto.getName() == null) {
            updatingUser.setName(updatingUser.getName());
        } else {
            throw new ValidationException("имя пусто либо состоит из пробелов");
        }
        updatingUser.setEmail(userDto.getEmail() != null ? userDto.getEmail() : updatingUser.getEmail());
        return updatingUser;
    }
}