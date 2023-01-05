package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

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
    public Collection<ItemDto> findOwnAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findAllOwn(userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("search")
    public Collection<ItemDto> searchByKeyWord(@RequestParam String text) {
        return itemService.searchByKeyWord(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId, @Valid @RequestBody CommentDto text) {
        return itemService.addComment(userId, itemId, text);
    }
}