package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Integer request;
    private ItemDto.Booking lastBooking;
    private ItemDto.Booking nextBooking;
    private List<CommentDto> comments;

    @Builder
    @Data
    public static class Booking {
        private final long id;
        private final long bookerId;
    }
}