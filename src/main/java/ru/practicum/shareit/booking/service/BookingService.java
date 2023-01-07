package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToGet;

import java.util.Collection;

public interface BookingService {
    BookingDto add(BookingDtoToGet bookingDtoToGet, int userId);

    BookingDto acceptOrDeny(int bookingId, boolean approved, int userId);

    BookingDto get(int userId, int bookingId);

    Collection<BookingDto> getAllByBooker(int bookerId, String state);

    Collection<BookingDto> getAllByOwner(int ownerId, String state);
}