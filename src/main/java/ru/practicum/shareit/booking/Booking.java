package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.status.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
//@NoArgsConstructor(force=true)
public class Booking {
    int id; //уникальный идентификатор бронирования
    LocalDateTime start; //дата и время начала бронирования
    LocalDateTime end; //дата и время конца бронирования
    Item item; //вещь, которую пользователь бронирует
    User booker; //пользователь, который осуществляет бронирование
    BookingStatus status; /*статус бронирования. Может принимать одно из следующих
                          значений: WAITING — новое бронирование, ожидает одобрения, APPROVED —
                          Дополнительные советы ментора 2
                          бронирование подтверждено владельцем, REJECTED — бронирование
                          отклонено владельцем, CANCELED — бронирование отменено создателем*/
}