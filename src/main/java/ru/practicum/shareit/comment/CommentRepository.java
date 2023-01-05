package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "select new ru.practicum.shareit.comment.dto.CommentDto(" +
            "c.id, c.text, c.item, c.authorName, c.created) " +
            "from Comment as c " +
            "where c.item = ?1")
    List<CommentDto> findAllByItem(int itemId, Class<CommentDto> type);
}