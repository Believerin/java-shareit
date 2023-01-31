package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.Create;
import ru.practicum.shareit.user.model.Update;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public UserDto add(@Validated(Create.class) @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable int userId, @Validated(Update.class) @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @GetMapping("{userId}")
    public UserDto get(@PathVariable int userId) {
        return userService.get(userId);
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable int userId) {
        userService.delete(userId);
    }
}