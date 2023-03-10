package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    @GetMapping("all")
    public Collection<ResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                            @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "20") int size) {
        return requestService.getAll(userId, from, size);
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