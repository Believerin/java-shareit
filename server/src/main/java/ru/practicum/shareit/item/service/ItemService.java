package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreated;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> findAllOwn(int userId, int from, int size);

    ItemDtoCreated add(int userId, ItemDtoCreated itemDtoCreated);

    ItemDtoCreated update(int userId, int itemId, ItemDtoCreated itemDtoCreated);

    ItemDto get(int userId, int itemId);

    Collection<ItemDto> searchByKeyWord(String text, int from, int size);

    CommentDto addComment(int userId, int itemId, CommentDto text);
}