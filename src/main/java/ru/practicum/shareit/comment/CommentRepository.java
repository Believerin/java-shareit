package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.*;
import ru.practicum.shareit.comment.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

/*    @Query(value = "select new ru.practicum.shareit.comment.model.Comment(" +
            "c.id, c.text, c.item, c.authorName, c.created) " +
            "from Comment as c " +
            "where c.item = ?1")*/
    List<Comment> findAllByItem(int itemId);

    List<Comment> getCommentByItemIn(Collection<Integer> itemId);
}