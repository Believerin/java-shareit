package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDbStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDbStorage itemStorage;
    private final UserService userService;

    public ItemServiceImpl(ItemDbStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public Collection<Item> findAllOwn(int userId) {
        return itemStorage.findAllOwn(userId);
    }

    @Override
    public Item addItem(int userId, Item item) {
        try {
            userService.getUser(userId);
            return itemStorage.addItem(userId, item);
        } catch (NoSuchBodyException e) {
            throw new NoSuchBodyException("Владелец данного предмета");
        }
    }

    @Override
    public Item modifyItem(int userId, int itemId, ItemDto itemDto) {
        Item modifyingItem = itemStorage.getItem(itemId);
        if (userId != modifyingItem.getOwner()) {
            throw new NoAccessException("попытка редактировать чужой предмет");
        }
        return itemStorage.modifyItem(userId, ItemMapper.toItem(userId, modifyingItem, itemDto));
    }

    @Override
    public ItemDto getItem(int itemId) {
        Item item = itemStorage.getItem(itemId);
        if (item == null) {
            throw new NoSuchBodyException("Запрашиваемый предмет");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> searchByKeyWord(String text) {
        return text.isBlank() ? new ArrayList<>() : itemStorage.searchByKeyWord(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}