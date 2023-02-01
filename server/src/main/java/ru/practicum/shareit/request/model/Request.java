package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@Table(name = "requests", schema = "public")
public class Request {
    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requester;
    @Column(name = "creation_date")
    private LocalDateTime created;
}