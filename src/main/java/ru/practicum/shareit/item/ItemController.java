package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> findOwnAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "20") int size) {
        return itemService.findAllOwn(userId, from, size);
    }

    @PostMapping
    public ItemDtoCreated add(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDtoToGet itemDtoToGet) {
        return itemService.add(userId, itemDtoToGet);
    }

    @PatchMapping("{itemId}")
    public ItemDtoCreated update(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId, @RequestBody ItemDtoToGet itemDtoToGet) {
        return itemService.update(userId, itemId, itemDtoToGet);
    }

    @GetMapping("{itemId}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.get(userId, itemId);
    }

    @GetMapping("search")
    public Collection<ItemDto> searchByKeyWord(@RequestParam String text,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size) {
        return itemService.searchByKeyWord(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId, @Valid @RequestBody CommentDto text) {
        return itemService.addComment(userId, itemId, text);
    }
}