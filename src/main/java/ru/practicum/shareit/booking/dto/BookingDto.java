package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.status.BookingStatus;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    private BookingDto.Item item;
    private Booker booker;
    private BookingStatus status;

    @Builder
    @Data
    public static class Booker {
        private final int id;
        private final String name;
    }

    @Builder
    @Data
    public static class Item {
        private final int id;
        private final String name;
    }
}