package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank
    private String description;
    @NotNull
    private int requester;
    @NotNull
    private LocalDateTime created;
    private List<ItemOffer> items;

    @Builder
    @Data
    public static class ItemOffer {
        int id;
        String name;
        String description;
        boolean available;
        int requestId;
    }
}