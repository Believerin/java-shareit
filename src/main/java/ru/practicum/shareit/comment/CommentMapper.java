package ru.practicum.shareit.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem())
                .item(comment.getItem())
                .authorName(comment.getAuthorName().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(User author, int itemId, CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(itemId)
                .authorName(author)
                .created(LocalDateTime.now())
                .build();
    }
}