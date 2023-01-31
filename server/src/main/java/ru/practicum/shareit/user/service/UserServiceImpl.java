package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoSuchBodyException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream().map(user -> userMapper.toUserDto(user)).collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(int userId, UserDto userDto) {
        User actualUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchBodyException("Запрашиваемый пользователь"));
        return userMapper.toUserDto(toUser(userId, actualUser, userDto));
    }

    @Override
    public UserDto get(int id) {
        User o = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchBodyException("Запрашиваемый пользователь"));
        return userMapper.toUserDto(o);
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