package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestClient requestClient;

    @GetMapping("all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                          @Positive @RequestParam(defaultValue = "20") int size) {
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> findOwnAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Positive @RequestParam(defaultValue = "20") int size) {
        return requestClient.getAllOwn(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") int userId,
                                  @Valid @RequestBody RequestDto requestDto) {
        return requestClient.add(requestDto, userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") int userId,
                           @PathVariable int requestId) {
        return requestClient.get(userId, requestId);
    }
}