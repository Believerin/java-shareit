package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDto, int userId);

    BookingDto acceptOrDenyBooking(int bookingId, boolean approved, int userId);

    BookingDto getBooking(int userId, int bookingId);

    Collection<BookingDto> getAllBookingsByBooker(int bookerId, String state);

    Collection<BookingDto> getAllBookingsByOwner(int ownerId, String state);
}