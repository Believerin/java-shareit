package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

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

    public static Comment toComment(User authorId, int itemId, CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(itemId)
                .authorName(authorId)
                .created(LocalDateTime.now())
                .build();
    }
}