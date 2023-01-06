package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.status.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public BookingDto add(BookingDto bookingDto, int bookerId) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("начало периода позже либо равно его концу");
        }
        Item itemToBook;
        Optional<Item> actualItem = itemRepository.findById(bookingDto.getItemId());
        if (actualItem.isEmpty()) {
            throw new NoSuchBodyException("Предмет для бронирования");
        } else {
            itemToBook = actualItem.get();
            if (itemToBook.getOwner() == bookerId) {
                throw new NoAccessException("бронируемый предмет в собственности бронирующего");
            }
        }
        if (!itemToBook.getAvailable()) {
            throw new ValidationException("предмет не доступен для бронирования");
        }
        User booker;
        Optional<User> actualBooker = userRepository.findById(bookerId);
        if (actualBooker.isEmpty()) {
            throw new NoSuchBodyException("Владелец предмета для бронирования");
        } else {
            booker = actualBooker.get();
        }
        Booking b = BookingMapper.toBooking(bookingDto, itemToBook, booker);
        b.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(b);
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto acceptOrDeny(int bookingId, boolean approved, int userId) {
        Booking booking = bookingRepository.findByOwner(userId, bookingId)
                .orElseThrow(() -> new NoAccessException("нет прав для редактирования статуса"));
        if (booking.getStatus().equals(BookingStatus.APPROVED) && approved) {
            throw new ValidationException("бронирование уже подтверждено");
        }
        bookingRepository.accept(bookingId, approved);
        return get(userId, bookingId);
    }

    @Override
    public BookingDto get(int userId, int bookingId) {
        Optional<Booking> booking = bookingRepository.find(userId, bookingId);
        if (booking.isPresent()) {
            return BookingMapper.toBookingDto(booking.get());
        } else {
            throw new NoSuchBodyException("Номер бронирования");
        }
    }

    @Override
    public Collection<BookingDto> getAllByBooker(int bookerId, String state) {
        userService.get(bookerId);
        int status;
        try {
            status = AppealStatus.valueOf(state).getAppealId();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(state);
        }
        return bookingRepository.getAllByBookerOrOwner(bookerId, status, false).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllByOwner(int ownerId, String state) {
        userService.get(ownerId);
        int status;
        try {
            status = AppealStatus.valueOf(state).getAppealId();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(state);
        }
        return bookingRepository.getAllByBookerOrOwner(ownerId, status,  true).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}