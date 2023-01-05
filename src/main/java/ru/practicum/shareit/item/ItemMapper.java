package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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

    public static Item toItem(int userId, Item modifyingItem, ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId() != null ? itemDto.getId() : modifyingItem.getId())
                .name(itemDto.getName() != null ? itemDto.getName() : modifyingItem.getName())
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : modifyingItem.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : modifyingItem.getAvailable())
                .owner(userId)
                .request(modifyingItem.getRequest())
                .build();
    }

    public static Item toItem(int userId, ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(userId)
                .build();
    }
}