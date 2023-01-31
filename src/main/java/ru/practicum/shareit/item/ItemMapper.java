package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(comments)
                .build();
    }

    public static ItemDtoCreated toItemDtoCreated(Item item) {
        return ItemDtoCreated.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(int userId, ItemDtoCreated itemDtoCreated, Request request) {
        return Item.builder()
                .name(itemDtoCreated.getName())
                .description(itemDtoCreated.getDescription())
                .available(itemDtoCreated.getAvailable())
                .owner(userId)
                .request(request)
                .build();
    }

    public static ItemDto.Booking toBooking(Booking booking) {
        return ItemDto.Booking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}