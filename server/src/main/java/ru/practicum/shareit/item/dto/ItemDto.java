package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ItemDto {
    @EqualsAndHashCode.Exclude
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer request;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;

    @Builder
    @Data
    public static class Booking {
        private final long id;
        private final long bookerId;
    }
}