package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> findAllOwn(int userId);

    ItemDto addItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);

    ItemDto getItem(int userId, int itemId);

    Collection<ItemDto> searchByKeyWord(String text);

    CommentDto addComment(int userId, int itemId, CommentDto text);
}