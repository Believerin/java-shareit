package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.status.*;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, int bookerId) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("начало периода позже, чем его конец");
        }
        Item item;
        Optional<Item> o = itemRepository.findById(bookingDto.getItemId());
        if (o.isEmpty()) {
            throw new NoSuchBodyException("Предмет для бронирования");
        } else {
            item = o.get();
            if (item.getOwner() == bookerId) {
                throw new NoAccessException("бронируемый предмет в собственности бронирующего");
            }
        }
        if (!item.getAvailable()) {
            throw new ValidationException("предмет не доступен для бронирования");
        }
        User booker;
        Optional<User> foo = userRepository.findById(bookerId);
        if (foo.isEmpty()) {
            throw new NoSuchBodyException("Владелец предмета для бронирования");
        } else {
            booker = foo.get();
        }
        Booking b = BookingMapper.toBooking(bookingDto, item, booker);
        b.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(b);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto acceptOrDenyBooking(int bookingId, boolean approved, int userId) {
        Optional<BookingDto> o = bookingRepository.findBookingByOwner(userId, bookingId);
        if (o.isPresent()) {
            if (o.get().getStatus().equals(BookingStatus.APPROVED) && approved) {
                throw new ValidationException("бронирование уже подтверждено");
            }
            bookingRepository.acceptBooking(bookingId, approved);
            return getBooking(userId, bookingId);
        } else {
            throw new NoAccessException("нет прав для редактирования статуса");
        }
    }

    @Override
    public BookingDto getBooking(int userId, int bookingId) {
        Optional<BookingDto> o = bookingRepository.findBooking(userId, bookingId);
        if (o.isPresent()) {
            return o.get();
        } else {
            throw new NoSuchBodyException("Номер бронирования");
        }
    }

    @Override
    public Collection<BookingDto> getAllBookingsByBooker(int bookerId, String state) {
        userService.getUser(bookerId);
        int status;
        try {
            status = AppealStatus.valueOf(state).getAppealId();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(state);
        }
        return bookingRepository.getAllBookingsByBookerOrOwner(bookerId, status, false);
    }

    @Override
    public Collection<BookingDto> getAllBookingsByOwner(int ownerId, String state) {
        userService.getUser(ownerId);
        int status;
        try {
            status = AppealStatus.valueOf(state).getAppealId();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(state);
        }
        return bookingRepository.getAllBookingsByBookerOrOwner(ownerId, status,  true);
    }
}