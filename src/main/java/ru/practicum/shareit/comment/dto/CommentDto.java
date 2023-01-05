package ru.practicum.shareit.comment.dto;

import lombok.*;
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
    private int id;
    @NotBlank
    private String text;
    private int item;
    private String authorName;
    private User author;
    private LocalDateTime created;

    public CommentDto(int id, String text, int item, User author, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }
}