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
//@NoArgsConstructor(force=true)
public class ItemRequest {
    int id; //уникальный идентификатор запроса
    String description; //текст запроса, содержащий описание требуемой вещи
    User requestor; //пользователь, создавший запрос
    LocalDateTime created; //дата и время создания запроса
}
