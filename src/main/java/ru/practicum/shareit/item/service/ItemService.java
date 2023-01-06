package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> findAllOwn(int userId);

    ItemDto add(int userId, ItemDto itemDto);

    ItemDto update(int userId, int itemId, ItemDto itemDto);

    ItemDto get(int userId, int itemId);

    Collection<ItemDto> searchByKeyWord(String text);

    CommentDto addComment(int userId, int itemId, CommentDto text);
}