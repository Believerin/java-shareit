package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.user.model.Create;
import ru.practicum.shareit.user.model.Update;

import javax.validation.constraints.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank(groups = Create.class)
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    private String email;
}