package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoCreated;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findOwnAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "20") int size) {
        return itemClient.findAllOwn(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDtoCreated itemDtoCreated) {
        return itemClient.add(userId, itemDtoCreated);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId, @RequestBody ItemDtoCreated itemDtoCreated) {
        return itemClient.update(userId, itemId, itemDtoCreated);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemClient.get(userId, itemId);
    }

    @GetMapping("search")
    public ResponseEntity<Object> searchByKeyWord(@RequestHeader("X-Sharer-User-Id") int userId,
                                                  @RequestParam String text,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "20") int size) {
        return itemClient.searchByKeyWord(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @PathVariable int itemId,
                                 @Valid @RequestBody CommentDto text) {
        return itemClient.addComment(userId, itemId, text);
    }
}