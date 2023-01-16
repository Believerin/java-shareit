package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoCreated;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    @GetMapping("all")
    public Collection<ResponseDto> findAll(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "20") int size) {
        return requestService.getAll(from, size);
    }

    @GetMapping
    public Collection<ResponseDto> findOwnAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "20") int size) {
        return requestService.getAllOwn(userId, from, size);
    }

    @PostMapping
    public ResponseDtoCreated add(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody RequestDto requestDto) {
        return requestService.add(requestDto, userId);
    }

    @GetMapping("{requestId}")
    public ResponseDto get(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int requestId) {
        return requestService.get(userId, requestId);
    }
}