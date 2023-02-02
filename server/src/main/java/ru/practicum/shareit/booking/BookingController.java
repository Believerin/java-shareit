package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") int userId,
                          @RequestBody BookingCreatedDto bookingCreatedDto) {
        return bookingService.add(bookingCreatedDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto acceptOrDeny(@RequestParam boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") int userId,
                                   @PathVariable int bookingId) {
        return bookingService.acceptOrDeny(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") int userId,
                          @PathVariable int bookingId) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") int bookerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        return bookingService.getAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("owner")
    public Collection<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "20") int size) {
        return bookingService.getAllByOwner(ownerId, state, from, size);
    }
}