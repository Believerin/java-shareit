package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.status.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public BookingDto add(BookingDtoCreated bookingDtoCreated, int bookerId) {
        if (bookingDtoCreated.getEnd().isBefore(bookingDtoCreated.getStart())
                || bookingDtoCreated.getEnd().equals(bookingDtoCreated.getStart())) {
            throw new ValidationException("начало периода позже либо равно его концу");
        }
        Item itemToBook = itemRepository.findById(bookingDtoCreated.getItemId())
                .orElseThrow(() -> new NoSuchBodyException("Владелец предмета для бронирования"));;
            if (itemToBook.getOwner() == bookerId) {
                throw new NoAccessException("бронируемый предмет в собственности бронирующего");
            }
        if (!itemToBook.getAvailable()) {
            throw new ValidationException("предмет не доступен для бронирования");
        }
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NoSuchBodyException("Владелец предмета для бронирования"));
        Booking booking = BookingMapper.toBooking(bookingDtoCreated, itemToBook, booker);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
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
        Booking booking = bookingRepository.find(userId, bookingId)
                .orElseThrow(() -> new NoSuchBodyException("Номер бронирования"));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getAllByBooker(int bookerId, String state, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        userService.get(bookerId);
        int status = getStatus(state);
        return bookingRepository.getAllByBookerOrOwner(bookerId, status, false, page).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllByOwner(int ownerId, String state, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        userService.get(ownerId);
        int status = getStatus(state);
        return bookingRepository.getAllByBookerOrOwner(ownerId, status,  true, page).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    //--------------------------------------Служебные методы-------------------------------------------------

    private int getStatus(String state) {
        int status;
        try {
            status = AppealStatus.valueOf(state).getAppealId();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(state);
        }
        return status;
    }
}