package ru.practicum.shareit.user.dto;

import lombok.*;

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
    private String name;
    private String email;
}