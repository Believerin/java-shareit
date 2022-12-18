package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<Item> findOwnAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findAllOwn(userId);
    }

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody Item item) {
        return itemService.addItem(userId, item);
    }

    @PatchMapping("{itemId}")
    public Item modifyItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId, @RequestBody ItemDto itemDto) {
        return itemService.modifyItem(userId, itemId, itemDto);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("search")
    public Collection<ItemDto> searchByKeyWord(@RequestParam String text) {
        return itemService.searchByKeyWord(text);
    }
}