package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreated;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .start(booking.getStart())
                .id(booking.getId())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(toItemBooked(booking))
                .booker(toBooker(booking))
                .build();
    }

    public static Booking toBooking(BookingDtoCreated bookingDtoCreated, Item item, User booker) {
        return Booking.builder()
                .start(bookingDtoCreated.getStart())
                .end(bookingDtoCreated.getEnd())
                .item(item)
                .booker(booker)
                .build();
    }

    public static BookingDto.Booker toBooker(Booking booking) {
        return BookingDto.Booker.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build();
    }

    public static BookingDto.Item toItemBooked(Booking booking) {
        return BookingDto.Item.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();
    }
}