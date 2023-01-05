package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(int userId, User modifyingUser, UserDto userDto) {
        return new User(
                userId,
                userDto.getName() != null ? userDto.getName() : modifyingUser.getName(),
                userDto.getEmail() != null ? userDto.getEmail() : modifyingUser.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}