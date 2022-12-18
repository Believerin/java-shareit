package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Collection<Item> findAllOwn(int userId);

    Item addItem(int userId, Item item);

    Item modifyItem(int userId, int itemId, ItemDto itemDto);

    ItemDto getItem(int itemId);

    Collection<ItemDto> searchByKeyWord(String text);
}