package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    int id;
    String description;
    User requester;
    LocalDateTime created;
}
