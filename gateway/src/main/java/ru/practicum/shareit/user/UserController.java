package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.Create;
import ru.practicum.shareit.user.model.Update;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> add(@Validated(Create.class) @RequestBody UserDto userDto) {
        return userClient.add(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(@PathVariable int userId, @Validated(Update.class) @RequestBody UserDto userDto) {
        return userClient.update(userId, userDto);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> get(@PathVariable int userId) {
        return userClient.get(userId);
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable int userId) {
        userClient.delete(userId);
    }
}