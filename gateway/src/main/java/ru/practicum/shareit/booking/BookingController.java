package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoCreated;
import ru.practicum.shareit.booking.status.AppealStatus;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @Valid @RequestBody BookingDtoCreated bookingDtoCreated) {
        return bookingClient.add(bookingDtoCreated, userId);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> acceptOrDeny(@RequestParam boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") int userId,
                                   @PathVariable int bookingId) {
        return bookingClient.acceptOrDeny(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId) {
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader("X-Sharer-User-Id") int bookerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        AppealStatus.getStatus(state);
        return bookingClient.getAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "20") int size) {
        return bookingClient.getAllByOwner(ownerId, state, from, size);
    }
}