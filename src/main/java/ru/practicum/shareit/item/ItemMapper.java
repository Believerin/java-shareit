package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }

    public static ItemDtoCreated toItemDtoCreated(Item item) {
        return ItemDtoCreated.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(int userId, ItemDtoToGet itemDtoToGet) {
        return Item.builder()
                .name(itemDtoToGet.getName())
                .description(itemDtoToGet.getDescription())
                .available(itemDtoToGet.getAvailable())
                .owner(userId)
                .build();
    }

    public static ItemDto.Booking toBooking(Booking booking) {
        return ItemDto.Booking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}