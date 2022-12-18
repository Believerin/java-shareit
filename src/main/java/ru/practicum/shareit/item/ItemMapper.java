package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static Item toItem(int userId, Item modifyingItem, ItemDto itemDto) {
        return new Item(
                itemDto.getId() != null ? itemDto.getId() : modifyingItem.getId(),
                itemDto.getName() != null ? itemDto.getName() : modifyingItem.getName(),
                itemDto.getDescription() != null ? itemDto.getDescription() : modifyingItem.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : modifyingItem.getAvailable(),
                userId,
                modifyingItem.getRequest()
        );
    }
}