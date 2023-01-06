package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto add(BookingDto bookingDto, int userId);

    BookingDto acceptOrDeny(int bookingId, boolean approved, int userId);

    BookingDto get(int userId, int bookingId);

    Collection<BookingDto> getAllByBooker(int bookerId, String state);

    Collection<BookingDto> getAllByOwner(int ownerId, String state);
}