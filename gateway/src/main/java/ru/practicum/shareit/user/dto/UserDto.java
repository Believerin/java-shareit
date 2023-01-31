package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.user.model.Create;
import ru.practicum.shareit.user.model.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotBlank(groups = Create.class)
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    private String email;
}