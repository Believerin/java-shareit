package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.status.BookingStatus;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
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