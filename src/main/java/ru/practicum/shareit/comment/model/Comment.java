package ru.practicum.shareit.comment.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "comments", schema = "public")
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String text;
    @Column(name = "item_id")
    private int item;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User authorName;
    @Column(name = "creation_date")
    private LocalDateTime created;
}