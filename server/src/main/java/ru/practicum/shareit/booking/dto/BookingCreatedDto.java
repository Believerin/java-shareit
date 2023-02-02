package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingCreatedDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer itemId;
}