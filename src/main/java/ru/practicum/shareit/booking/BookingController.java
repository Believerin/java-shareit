package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto acceptOrDenyBooking(@RequestParam boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") int userId,
                                          @PathVariable int bookingId) {
        return bookingService.acceptOrDenyBooking(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsByBooker(@RequestHeader("X-Sharer-User-Id") int bookerId,
                                                         @RequestParam(required = false, defaultValue = "ALL")
                                                         String state) {
        return bookingService.getAllBookingsByBooker(bookerId, state);
    }

    @GetMapping("owner")
    public Collection<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                                         @RequestParam(required = false, defaultValue = "ALL")
                                                         String state) {
        return bookingService.getAllBookingsByOwner(ownerId, state);
    }
}