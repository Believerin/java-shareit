package ru.practicum.shareit.comment.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank
    private String text;
    private int item;
    private String authorName;
    private LocalDateTime created;
}