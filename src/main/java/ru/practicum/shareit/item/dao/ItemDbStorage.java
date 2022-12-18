package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemDbStorage {

    private Map<Integer, List<Item>> items = new HashMap<>();
    private int nextId = 0;

    public Collection<Item> findAllOwn(int userId) {
        return items.get(userId);
    }

    public Item addItem(int userId, Item item) {
        item.setId(++nextId);
        item.setOwner(userId);
        if (!items.containsKey(userId)) {
            items.put(userId, new ArrayList<>());
        }
        items.get(userId).add(item);
        return item;
    }

    public Item modifyItem(int userId, Item item) {
        if (items.get(userId).removeIf(o -> o.getId() == item.getId())) {
            items.get(userId).add(item);
            return item;
        } else {
            return null;
        }
    }

    public Item getItem(int itemId) {
        for (List<Item> list : items.values()) {
            for (Item i : list) {
                if (i.getId() == itemId) return i;
            }
        }
        return null;
    }

    public Collection<Item> searchByKeyWord(String text) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)))
                .collect(Collectors.toList());
    }
}